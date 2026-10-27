# Sistema POS Soda / Pulpería - Programación III

## Información del Curso
- **Curso:** EIF206 - Programación III (Universidad Nacional)
- **Profesoras:** Siandi Araya Bello y Kristel Duarte Pérez
- **Proyecto 1:** aplicación de escritorio en JavaFX con persistencia en memoria/CSV
- **Proyecto 2:** persistencia real en MySQL con JDBC y patrón DAO (entrega: 1 de noviembre)
- **Valor del Proyecto 2:** 20%

---

## Requisitos
- JDK 25
- Maven
- MySQL 8 o superior
- MySQL Workbench

## Configuración y ejecución

1. En MySQL Workbench abra y ejecute `database/schema.sql`. Crea la base `pos_soda_pulperia`
   con sus 8 tablas y datos iniciales (**borra la base si ya existía**).
2. Copie `src/main/resources/db.properties.example` como `src/main/resources/db.properties`
   y escriba su usuario y contraseña de MySQL. Este archivo no se sube a Git.
3. Ejecute `mvn clean javafx:run`, o la clase `Main` desde el IDE
   (si da error de JavaFX, ejecute `Launcher`).

### Usuarios de prueba (contraseña `admin123`)

| Usuario | Acceso |
|---------|--------|
| `admin` | Ambos negocios (con selector de negocio) y bitácora |
| `soda` | Soda |
| `pulperia` | Pulpería |

---

## Arquitectura

Arquitectura MVC con una capa de persistencia separada (patrón DAO):

```
model  →  dao (interfaces)  →  dao.jdbc (JDBC / MySQL)  →  service  →  controller + vistas FXML
```

- **model:** entidades (Cliente, Producto, Orden y sus subtipos, Pago y sus subtipos, Usuario, etc.).
- **dao:** interfaces `ClienteDAO`, `ProductoDAO`, `UsuarioDAO`, `OrdenDAO`, `LogDAO`.
- **dao.jdbc:** implementaciones con JDBC; toda consulta con datos variables usa `PreparedStatement`.
- **service:** lógica de negocio; no conoce SQL, solo usa las interfaces DAO.
- **controller / view:** pantallas JavaFX (FXML) y sus controladores.
- **util:** `ConexionBD` (lee `db.properties`).

### Modelo de datos (resumen)
`negocio`, `usuario`, `cliente`, `producto`, `pedido`, `detalle_pedido` (relación N:M entre pedido
y producto), `pago` y `log_accion`. Normalizado hasta 3FN.

---

## Novedades del Proyecto 2

- Persistencia en MySQL con DAO/JDBC (clientes, productos, usuarios, pedidos y bitácora).
- Un pedido se guarda en una **sola transacción**: pedido, detalle, pago y descuento de
  inventario (con rollback si algo falla o no hay stock).
- Operaciones de base de datos en hilos aparte (`Task`) para no congelar la interfaz.
- **Funcionalidad extra 1:** reporte de ventas del día por negocio.
- **Funcionalidad extra 2:** bitácora de acciones (login, logout, facturas), visible solo para el administrador.

---

## Bitácora de Contribuciones

### Contribuyentes y sus Aportes (Proyecto 1)

| Nombre                                           | Descripción de Contribuciones                                                                                                                                                                                                                                                                                                                                                                              |
|--------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Santiago Castro** (Castooor)                   | • Creación inicial del repositorio y primer commit<br>• Implementación de MainView, MainController y punto de entrada<br>• Creación del RepositorioGenerico<br>• Reorganización de archivos CSS a la carpeta de resources<br>• Implementación completa de la lógica de órdenes (OrdenService, OrdenView, OrdenController)<br>• Integración de módulos y inyección de dependencias                          |
| **Carlos Víquez** (CarlosViquez31)               | • Estructuración e inicialización del proyecto (carpetas, pom.xml, gitignore)<br>• Creación de la clase Launcher para JavaFX<br>• Inclusión de clases Cliente y Producto<br>• Implementación del catálogo de clientes<br>• Desarrollo de la interfaz de pago<br>• Agregación de órdenes al sistema<br>• Implementación del cambio de modo claro/oscuro<br>• Manejo de pull requests y merges con el equipo |
| **Tamara Ruiz** (TamaraRuiz20)                   | • Creación del catálogo de productos<br>• Desarrollo y actualización de la vista y lógica de productos                                                                                                                                                                                                                                                                                                     |
| **Valeria Cordero** (valeriacorderomurillo-beep) | • Inclusión de clases de órdenes<br>• Implementación del módulo de facturación<br>• Validaciones del sistema •Implementación de Reportes •Migración a SQL                                                                                                                                                                                                                                                  |  

### Aportes del Proyecto 2

_(Cada integrante: agregue aquí sus aportes del Proyecto 2, por ejemplo el esquema SQL,
los DAO JDBC, la transacción de pedidos, los hilos, el reporte de ventas o la bitácora.)_

---

## Tecnologías Utilizadas

- **Java / JavaFX:** lenguaje e interfaz gráfica
- **MySQL 8 + JDBC (mysql-connector-j):** base de datos y acceso a datos
- **Maven:** gestor de dependencias
- **CSS:** estilos de la aplicación (modo claro/oscuro)