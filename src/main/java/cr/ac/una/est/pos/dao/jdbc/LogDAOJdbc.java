package cr.ac.una.est.pos.dao.jdbc;

import cr.ac.una.est.pos.dao.DAOException;
import cr.ac.una.est.pos.dao.LogDAO;
import cr.ac.una.est.pos.model.LogAccion;
import cr.ac.una.est.pos.model.TipoNegocio;
import cr.ac.una.est.pos.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC (MySQL) de la bitácora del sistema.
 */
public class LogDAOJdbc implements LogDAO {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Inserta una fila en log_accion. Si el negocio no existe en la tabla
     * negocio (por ejemplo ADMIN), id_negocio queda en NULL.
     *
     * @param usuario nombre del usuario que realizó la acción
     * @param negocio negocio donde ocurrió, o null
     * @param accion tipo de acción
     * @param detalle descripción breve
     * @throws DAOException si falla la base de datos
     */
    @Override
    public void registrar(String usuario, TipoNegocio negocio, String accion, String detalle) throws DAOException {
        String sql = "INSERT INTO log_accion (usuario, id_negocio, accion, detalle) "
                + "VALUES (?, (SELECT id_negocio FROM negocio WHERE nombre = ?), ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, negocio != null ? negocio.name() : null);
            ps.setString(3, accion);
            ps.setString(4, detalle);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("No se pudo registrar la acción en la bitácora", e);
        }
    }

    /**
     * Trae las últimas acciones registradas, con el nombre del negocio.
     *
     * @param limite cantidad máxima de registros
     * @return lista de acciones, de la más reciente a la más antigua
     * @throws DAOException si falla la base de datos
     */
    @Override
    public List<LogAccion> listarRecientes(int limite) throws DAOException {
        String sql = "SELECT l.fecha_hora, l.usuario, n.nombre AS negocio, l.accion, l.detalle "
                + "FROM log_accion l LEFT JOIN negocio n ON n.id_negocio = l.id_negocio "
                + "ORDER BY l.fecha_hora DESC, l.id_log DESC LIMIT ?";
        List<LogAccion> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String negocio = rs.getString("negocio");
                    String detalle = rs.getString("detalle");
                    lista.add(new LogAccion(
                            rs.getTimestamp("fecha_hora").toLocalDateTime().format(FORMATO),
                            rs.getString("usuario"),
                            negocio != null ? negocio : "",
                            rs.getString("accion"),
                            detalle != null ? detalle : ""));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo leer la bitácora", e);
        }
        return lista;
    }
}