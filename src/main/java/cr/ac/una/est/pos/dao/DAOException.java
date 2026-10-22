package cr.ac.una.est.pos.dao;

/**
 * Error ocurrido al acceder a la base de datos. Envuelve la excepción SQL
 * original para que las capas superiores no dependan de java.sql.
 */
public class DAOException extends RuntimeException {

    /**
     * @param mensaje descripción del error, pensada para mostrarse al usuario
     * @param causa excepción original lanzada por JDBC
     */
    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}