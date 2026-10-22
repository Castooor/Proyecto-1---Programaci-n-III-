package cr.ac.una.est.pos.dao.jdbc;

import cr.ac.una.est.pos.dao.ClienteDAO;
import cr.ac.una.est.pos.dao.DAOException;
import cr.ac.una.est.pos.model.Cliente;
import cr.ac.una.est.pos.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

/**
 * Implementación JDBC de ClienteDAO: guarda y lee los clientes en MySQL.
 * Todas las consultas usan PreparedStatement para evitar inyección SQL.
 */
public class ClienteDAOJdbc implements ClienteDAO {

    /**
     * Obtiene todos los clientes guardados.
     *
     * @return lista de clientes, ordenada por nombre
     * @throws DAOException si falla la consulta a la base de datos
     */
    @Override
    public ArrayList<Cliente> listarTodos() {
        String sql = "SELECT cedula, nombre, telefono FROM cliente ORDER BY nombre";
        ArrayList<Cliente> clientes = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(crearCliente(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo cargar la lista de clientes", e);
        }
        return clientes;
    }

    /**
     * Busca un cliente por su cédula.
     *
     * @param cedula cédula del cliente
     * @return el cliente, o null si no existe
     * @throws DAOException si falla la consulta a la base de datos
     */
    @Override
    public Cliente buscarPorCedula(String cedula) {
        String sql = "SELECT cedula, nombre, telefono FROM cliente WHERE cedula = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearCliente(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo buscar el cliente " + cedula, e);
        }
    }

    /**
     * Guarda un cliente nuevo.
     *
     * @param cliente cliente a guardar
     * @throws DAOException si la cédula ya existe o falla la base de datos
     */
    @Override
    public void insertar(Cliente cliente) {
        String sql = "INSERT INTO cliente (cedula, nombre, telefono) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getCedula());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getTelefono());

            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DAOException("Ya existe un cliente con la cédula " + cliente.getCedula(), e);
        } catch (SQLException e) {
            throw new DAOException("No se pudo guardar el cliente " + cliente.getCedula(), e);
        }
    }

    /**
     * Actualiza el nombre y el teléfono de un cliente existente.
     *
     * @param cliente cliente con los datos nuevos (se identifica por su cédula)
     * @throws DAOException si falla la actualización en la base de datos
     */
    @Override
    public void actualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre = ?, telefono = ? WHERE cedula = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getCedula());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("No se pudo actualizar el cliente " + cliente.getCedula(), e);
        }
    }

    /**
     * Elimina un cliente.
     *
     * @param cedula cédula del cliente a eliminar
     * @throws DAOException si el cliente tiene pedidos registrados o falla la base de datos
     */
    @Override
    public void eliminar(String cedula) {
        String sql = "DELETE FROM cliente WHERE cedula = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);

            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DAOException("No se puede eliminar: el cliente tiene pedidos registrados", e);
        } catch (SQLException e) {
            throw new DAOException("No se pudo eliminar el cliente " + cedula, e);
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Cliente.
     *
     * @param rs resultado de una consulta, posicionado en una fila válida
     * @return cliente con los datos de esa fila
     * @throws SQLException si alguna columna no se puede leer
     */
    private Cliente crearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getString("cedula"),
                rs.getString("nombre"),
                rs.getString("telefono"));
    }
}
