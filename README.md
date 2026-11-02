# Sistema POS Soda / Pulpería - Programación III

## Información del Curso
- **Curso:** EIF206 - Programación III (Universidad Nacional)
- **Profesoras:** Siandi Araya Bello y Kristel Duarte Pérez
- **Proyecto 1:** aplicación de escritorio en JavaFX con persistencia en memoria/CSV
- **Proyecto 2:** persistencia real en MySQL con JDBC y patrón DAO (entrega: 1 de noviembre)
- **Valor del Proyecto 2:** 20%


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


## Arquitectura
Arquitectura MVC con una capa de persistencia separada (patrón DAO):
model  →  dao (interfaces)  →  dao.jdbc (JDBC / MySQL)  →  service  →  controller + vistas FXML

- **model:** entidades (Cliente, Producto, Orden y sus subtipos, Pago y sus subtipos, Usuario, etc.).
- **dao:** interfaces `ClienteDAO`, `ProductoDAO`, `UsuarioDAO`, `OrdenDAO`, `LogDAO`.
- **dao.jdbc:** implementaciones con JDBC; toda consulta con datos variables usa `PreparedStatement`.
- **service:** lógica de negocio; no conoce SQL, solo usa las interfaces DAO.
- **controller / view:** pantallas JavaFX (FXML) y sus controladores.
- **util:** `ConexionBD` (lee `db.properties`).

### Modelo de datos (resumen)
`negocio`, `usuario`, `cliente`, `producto`, `pedido`, `detalle_pedido` (relación N:M entre pedido
y producto), `pago` y `log_accion`. Normalizado hasta 3FN.

## Novedades del Proyecto 2
- Persistencia en MySQL con DAO/JDBC (clientes, productos, usuarios, pedidos y bitácora).
- Un pedido se guarda en una **sola transacción**: pedido, detalle, pago y descuento de
  inventario (con rollback si algo falla o no hay stock).
- Operaciones de base de datos en hilos aparte (`Task`) para no congelar la interfaz.
- **Funcionalidad extra 1:** reporte de ventas del día por negocio.
- **Funcionalidad extra 2:** bitácora de acciones (login, logout, facturas), visible solo para el administrador.

## Bitácora de Contribuciones

### Aportes del Proyecto 1
| Nombre | Descripción de Contribuciones |
|--------|-------------------------------|
| **Santiago Castro**<br>`(Castooor)` | • Creación inicial del repositorio y primer commit.<br>• Implementación de `MainView`, `MainController` y punto de entrada.<br>• Creación del `RepositorioGenerico`.<br>• Reorganización de archivos CSS a la carpeta de `resources`.<br>• Implementación completa de la lógica de órdenes (`OrdenService`, `OrdenView`, `OrdenController`).<br>• Integración de módulos e inyección de dependencias. |
| **Carlos Víquez**<br>`(CarlosViquez31)` | • Estructuración e inicialización del proyecto (`pom.xml`, `.gitignore`, estructura de carpetas).<br>• Creación de la clase `Launcher` para JavaFX.<br>• Inclusión de las entidades `Cliente` y `Producto`.<br>• Implementación del catálogo de clientes.<br>• Desarrollo de la interfaz de pago y agregación de órdenes al sistema.<br>• Implementación del alternador de tema Claro / Oscuro.<br>• Gestión de Pull Requests y *merges* con el equipo. |
| **Tamara Ruiz**<br>`(TamaraRuiz20)` | • Creación y diseño del catálogo de productos.<br>• Desarrollo y actualización de la vista y lógica asociada a los productos. |
| **Valeria Cordero**<br>`(valeriacorderomurillo-beep)` | • Inclusión de las clases para el manejo de órdenes.<br>• Implementación y desarrollo del módulo de facturación. |


### Aportes del Proyecto 2
| Nombre | Descripción de Contribuciones |
|--------|-------------------------------|
| **Santiago Castro**<br>`(Castooor)` | • Refactorización de la lógica del sistema para adaptar la nueva arquitectura.<br>• Agregación y mantenimiento de controladores.<br>• Pruebas de integración con la base de datos MySQL.<br>• Correcciones del Proyecto 1 basándose en la retroalimentación. |
| **Carlos Víquez**<br>`(CarlosViquez31)` | • Mejoras visuales y ajustes finales de interfaz gráfica.<br>• Separación y organización del módulo de productos.<br>• Implementación del esquema de base de datos, clases DAO y persistencia JDBC para productos.<br>• Corrección del buscador de productos. |
| **Tamara Ruiz**<br>`(TamaraRuiz20)` | • Implementación del CRUD de productos integrado con MySQL.<br>• Correcciones generales y mejoras en la interfaz de usuario.<br>• Creación y actualización de la documentación en el `README.md`. |
| **Valeria Cordero**<br>`(valeriacorderomurillo-beep)` | • Implementación de validaciones en todo el sistema para evitar entradas inconsistentes.<br>• Desarrollo del módulo de reportes de ventas.<br>• Migración del almacenamiento en archivos hacia consultas SQL.<br>• Edición, revisión y corrección de la documentación en el `README.md`.<br>• Mejoras en el flujo de experiencia de usuario y correcciones finales del Proyecto 1. |

## Tecnologías Utilizadas
- **Java / JavaFX:** lenguaje e interfaz gráfica
- **MySQL 8 + JDBC (mysql-connector-j):** base de datos y acceso a datos
- **Maven:** gestor de dependencias
- **CSS:** estilos de la aplicación (modo claro/oscuro)