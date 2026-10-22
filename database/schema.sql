-- ============================================================
-- schema.sql - Proyecto 2 EIF206 - POS Soda / Pulpería
-- MySQL 8 o superior
-- ADVERTENCIA: borra y vuelve a crear toda la base de datos.
-- ============================================================
DROP DATABASE IF EXISTS pos_soda_pulperia;
CREATE DATABASE pos_soda_pulperia CHARACTER SET utf8mb4;
USE pos_soda_pulperia;

-- ------------------------------------------------------------
-- Negocios que maneja el sistema (SODA y PULPERIA)
-- ------------------------------------------------------------
CREATE TABLE negocio (
    id_negocio INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(20) NOT NULL UNIQUE
);

-- ------------------------------------------------------------
-- Usuarios del sistema (el tipo coincide con el enum TipoNegocio)
-- ------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario  INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(30) NOT NULL UNIQUE,
    contrasena  VARCHAR(100) NOT NULL,
    tipo        ENUM('ADMIN','SODA','PULPERIA') NOT NULL
);

-- ------------------------------------------------------------
-- Clientes (compartidos por ambos negocios)
-- ------------------------------------------------------------
CREATE TABLE cliente (
    cedula    VARCHAR(20) PRIMARY KEY,
    nombre    VARCHAR(80) NOT NULL,
    telefono  VARCHAR(20) NOT NULL
);

-- ------------------------------------------------------------
-- Productos: cada producto pertenece a un negocio (catálogo separado)
-- ------------------------------------------------------------
CREATE TABLE producto (
    codigo      VARCHAR(20) PRIMARY KEY,
    nombre      VARCHAR(80) NOT NULL,
    categoria   VARCHAR(50) NOT NULL,
    precio      DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
    inventario  INT NOT NULL DEFAULT 0 CHECK (inventario >= 0),
    id_negocio  INT NOT NULL,
    CONSTRAINT fk_producto_negocio
        FOREIGN KEY (id_negocio) REFERENCES negocio(id_negocio)
);

-- ------------------------------------------------------------
-- Pedidos: una sola tabla para las tres clases de orden.
-- numero_mesa solo se llena en LOCAL; direccion solo en EXPRESS.
-- ------------------------------------------------------------
CREATE TABLE pedido (
    id_pedido       INT AUTO_INCREMENT PRIMARY KEY,
    id_negocio      INT NOT NULL,
    cedula_cliente  VARCHAR(20) NOT NULL,
    tipo_orden      ENUM('LOCAL','EXPRESS','MOSTRADOR') NOT NULL,
    numero_mesa     INT NULL,
    direccion       VARCHAR(150) NULL,
    fecha_hora      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_negocio
        FOREIGN KEY (id_negocio) REFERENCES negocio(id_negocio),
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (cedula_cliente) REFERENCES cliente(cedula),
    INDEX idx_pedido_fecha (fecha_hora)   -- acelera el reporte de ventas por día
);

-- ------------------------------------------------------------
-- Detalle del pedido: relación N:M entre pedido y producto.
-- precio_unitario guarda el precio al momento de la venta.
-- ------------------------------------------------------------
CREATE TABLE detalle_pedido (
    id_pedido        INT NOT NULL,
    codigo_producto  VARCHAR(20) NOT NULL,
    cantidad         INT NOT NULL CHECK (cantidad > 0),
    precio_unitario  DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_pedido, codigo_producto),
    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (codigo_producto) REFERENCES producto(codigo)
);

-- ------------------------------------------------------------
-- Pago: uno por pedido. monto = total cobrado (con IVA y envío).
-- ------------------------------------------------------------
CREATE TABLE pago (
    id_pedido  INT PRIMARY KEY,
    metodo     ENUM('EFECTIVO','TARJETA','SINPE') NOT NULL,
    monto      DECIMAL(10,2) NOT NULL CHECK (monto >= 0),
    CONSTRAINT fk_pago_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Log de acciones del sistema (uno solo para todo).
-- usuario es texto a propósito: el historial no debe borrarse
-- si se elimina al usuario. id_negocio es NULL en acciones generales.
-- ------------------------------------------------------------
CREATE TABLE log_accion (
    id_log      INT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario     VARCHAR(30) NOT NULL,
    id_negocio  INT NULL,
    accion      VARCHAR(30) NOT NULL,
    detalle     VARCHAR(255) NULL,
    CONSTRAINT fk_log_negocio
        FOREIGN KEY (id_negocio) REFERENCES negocio(id_negocio)
);

-- ============================================================
-- DATOS INICIALES (los que tenías en los CSV)
-- ============================================================
INSERT INTO negocio (nombre) VALUES ('SODA'), ('PULPERIA');

INSERT INTO usuario (nombre, contrasena, tipo) VALUES
    ('admin',    'admin123', 'ADMIN'),
    ('pulperia', 'admin123', 'PULPERIA'),
    ('soda',     'admin123', 'SODA');

INSERT INTO cliente (cedula, nombre, telefono) VALUES
    ('N/A',      'Cliente Genérico', 'N/A'),
    ('12009085', 'Juan Murillo',     '90908080');

INSERT INTO producto (codigo, nombre, categoria, precio, inventario, id_negocio) VALUES
    ('2003', 'Coca Cola',   'Gaseosa',   500.00,  7,
        (SELECT id_negocio FROM negocio WHERE nombre = 'SODA')),
    ('1001', 'Arroz 1kg',   'Abarrotes', 1500.00, 20,
        (SELECT id_negocio FROM negocio WHERE nombre = 'PULPERIA')),
    ('1002', 'Frijoles 1kg','Abarrotes', 1800.00, 15,
        (SELECT id_negocio FROM negocio WHERE nombre = 'PULPERIA'));
        
        
USE pos_soda_pulperia;
SHOW TABLES;                 -- deben salir 8 tablas
SELECT * FROM producto;      -- 3 productos
SELECT * FROM usuario;       -- 3 usuarios