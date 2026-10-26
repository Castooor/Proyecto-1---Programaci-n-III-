package cr.ac.una.est.pos.dao.jdbc;

import cr.ac.una.est.pos.dao.DAOException;
import cr.ac.una.est.pos.dao.UsuarioDAO;
import cr.ac.una.est.pos.model.TipoNegocio;
import cr.ac.una.est.pos.model.Usuario;
import cr.ac.una.est.pos.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC (MySQL) de UsuarioDAO.
 * Todas las consultas con datos variables usan PreparedStatement.
 */
public class UsuarioDAOJdbc implements UsuarioDAO {

    /**
     * Inserta un usuario en la tabla usuario.
     *
     * @param usuario usuario a guardar (si no tiene tipo de negocio se guarda como SODA)
     * @throws DAOException si falla la base de datos o el nombre ya existe
     */
    @Override
    public void insertar(Usuario usuario) throws DAOException {
        String sql = "INSERT INTO usuario (nombre, contrasena, tipo) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getContrasena());
            stmt.setString(3, usuario.getTipoNegocio() != null ? usuario.getTipoNegocio().name() : "SODA");

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al insertar usuario", e);
        }
    }

    /**
     * Busca un usuario por nombre con una consulta parametrizada.
     *
     * @param nombre nombre de usuario a buscar
     * @return el usuario, o Optional vacío si no existe
     * @throws DAOException si falla la base de datos
     */
    @Override
    public Optional<Usuario> buscarPorNombre(String nombre) throws DAOException {
        String sql = "SELECT * FROM usuario WHERE nombre = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar usuario por nombre", e);
        }
        return Optional.empty();
    }

    /**
     * Lee todos los usuarios de la tabla usuario.
     *
     * @return lista de usuarios (vacía si no hay ninguno)
     * @throws DAOException si falla la base de datos
     */
    @Override
    public List<Usuario> listarTodos() throws DAOException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar usuarios", e);
        }
        return lista;
    }

    /**
     * Actualiza la contraseña y el tipo de negocio de un usuario, identificado por su nombre.
     *
     * @param usuario usuario con los datos nuevos
     * @throws DAOException si falla la base de datos
     */
    @Override
    public void actualizar(Usuario usuario) throws DAOException {
        String sql = "UPDATE usuario SET contrasena = ?, tipo = ? WHERE nombre = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getContrasena());
            stmt.setString(2, usuario.getTipoNegocio() != null ? usuario.getTipoNegocio().name() : "SODA");
            stmt.setString(3, usuario.getNombre());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar usuario", e);
        }
    }

    /**
     * Elimina un usuario por su nombre.
     *
     * @param nombre nombre del usuario a eliminar
     * @throws DAOException si falla la base de datos
     */
    @Override
    public void eliminar(String nombre) throws DAOException {
        String sql = "DELETE FROM usuario WHERE nombre = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar usuario", e);
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un Usuario.
     * Si el tipo guardado no es válido, el tipo de negocio queda en null.
     *
     * @param rs resultado de la consulta, posicionado en una fila
     * @return el usuario construido con los datos de la fila
     * @throws SQLException si no se puede leer alguna columna
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setNombre(rs.getString("nombre"));
        u.setContrasena(rs.getString("contrasena"));

        String tipoStr = rs.getString("tipo");
        if (tipoStr != null) {
            try {
                u.setTipoNegocio(TipoNegocio.valueOf(tipoStr));
            } catch (IllegalArgumentException e) {
                u.setTipoNegocio(null);
            }
        }
        return u;
    }
}