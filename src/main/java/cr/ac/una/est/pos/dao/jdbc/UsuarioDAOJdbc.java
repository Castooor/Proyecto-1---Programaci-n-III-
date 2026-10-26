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

public class UsuarioDAOJdbc implements UsuarioDAO {

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