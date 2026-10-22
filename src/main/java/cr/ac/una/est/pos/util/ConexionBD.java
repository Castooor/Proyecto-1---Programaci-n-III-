package cr.ac.una.est.pos.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Entrega conexiones a la base de datos MySQL del sistema.
 * Los datos de conexión (url, usuario y contraseña) se leen del
 * archivo db.properties para no escribirlos en el código.
 */
public class ConexionBD {

    private static final String ARCHIVO_CONFIG = "/db.properties";

    /** Clase utilitaria: no se crean objetos de ella. */
    private ConexionBD() {
    }

    /**
     * Abre una nueva conexión con la base de datos.
     * Quien la pida debe cerrarla (con try-with-resources).
     *
     * @return conexión abierta a MySQL
     * @throws SQLException si falta la configuración o no se puede conectar
     */
    public static Connection obtenerConexion() throws SQLException {
        Properties config = cargarConfiguracion();
        return DriverManager.getConnection(
                config.getProperty("url"),
                config.getProperty("usuario"),
                config.getProperty("contrasena"));
    }

    /**
     * Lee el archivo db.properties desde el classpath.
     *
     * @return propiedades con url, usuario y contrasena
     * @throws SQLException si el archivo no existe o no se puede leer
     */
    private static Properties cargarConfiguracion() throws SQLException {
        Properties config = new Properties();
        try (InputStream entrada = ConexionBD.class.getResourceAsStream(ARCHIVO_CONFIG)) {
            if (entrada == null) {
                throw new SQLException("No se encontró db.properties en src/main/resources");
            }
            config.load(entrada);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer db.properties", e);
        }
        return config;
    }
}