package cr.ac.una.est.pos.dao.jdbc;

import cr.ac.una.est.pos.dao.DAOException;
import cr.ac.una.est.pos.dao.ProductoDAO;
import cr.ac.una.est.pos.model.Producto;
import cr.ac.una.est.pos.model.TipoNegocio;
import cr.ac.una.est.pos.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

/**
 * Implementación JDBC de ProductoDAO: guarda y lee los productos en MySQL.
 * Todas las consultas usan PreparedStatement para evitar inyección SQL.
 */
public class ProductoDAOJdbc implements ProductoDAO {

    /**
     * Obtiene los productos del catálogo de un negocio.
     *
     * @param negocio negocio (SODA o PULPERIA) cuyo catálogo se quiere
     * @return lista de productos; vacía si el negocio no tiene ninguno
     * @throws DAOException si falla la consulta a la base de datos
     */
    @Override
    public ArrayList<Producto> listarPorNegocio(TipoNegocio negocio) {
        String sql = """
                SELECT p.codigo, p.nombre, p.categoria, p.precio, p.inventario
                FROM producto p
                JOIN negocio n ON p.id_negocio = n.id_negocio
                WHERE n.nombre = ?
                ORDER BY p.nombre
                """;
        ArrayList<Producto> productos = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, negocio.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(crearProducto(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo cargar el catálogo de " + negocio.getDescripcion(), e);
        }
        return productos;
    }

    /**
     * Busca un producto por su código.
     *
     * @param codigo código del producto
     * @return el producto, o null si no existe
     * @throws DAOException si falla la consulta a la base de datos
     */
    @Override
    public Producto buscarPorCodigo(String codigo) {
        String sql = """
                SELECT codigo, nombre, categoria, precio, inventario
                FROM producto
                WHERE codigo = ?
                """;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearProducto(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo buscar el producto " + codigo, e);
        }
    }

    /**
     * Guarda un producto nuevo en el catálogo de un negocio.
     *
     * @param producto producto a guardar
     * @param negocio negocio al que pertenece
     * @throws DAOException si el código ya existe o falla la base de datos
     */
    @Override
    public void insertar(Producto producto, TipoNegocio negocio) {
        String sql = """
                INSERT INTO producto (codigo, nombre, categoria, precio, inventario, id_negocio)
                VALUES (?, ?, ?, ?, ?, (SELECT id_negocio FROM negocio WHERE nombre = ?))
                """;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecio());
            ps.setInt(5, producto.getCantidadInventario());
            ps.setString(6, negocio.name());

            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DAOException("Ya existe un producto con el código " + producto.getCodigo(), e);
        } catch (SQLException e) {
            throw new DAOException("No se pudo guardar el producto " + producto.getCodigo(), e);
        }
    }

    /**
     * Actualiza nombre, categoría, precio e inventario de un producto existente.
     *
     * @param producto producto con los datos nuevos (se identifica por su código)
     * @throws DAOException si falla la actualización en la base de datos
     */
    @Override
    public void actualizar(Producto producto) {
        String sql = """
                UPDATE producto
                SET nombre = ?, categoria = ?, precio = ?, inventario = ?
                WHERE codigo = ?
                """;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getCategoria());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getCantidadInventario());
            ps.setString(5, producto.getCodigo());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("No se pudo actualizar el producto " + producto.getCodigo(), e);
        }
    }

    /**
     * Cambia solo la cantidad en inventario de un producto.
     *
     * @param codigo código del producto
     * @param nuevaCantidad inventario resultante
     * @throws DAOException si falla la actualización en la base de datos
     */
    @Override
    public void actualizarInventario(String codigo, int nuevaCantidad) {
        String sql = "UPDATE producto SET inventario = ? WHERE codigo = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevaCantidad);
            ps.setString(2, codigo);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("No se pudo actualizar el inventario de " + codigo, e);
        }
    }

    /**
     * Elimina un producto.
     *
     * @param codigo código del producto a eliminar
     * @throws DAOException si el producto tiene ventas registradas o falla la base de datos
     */
    @Override
    public void eliminar(String codigo) {
        String sql = "DELETE FROM producto WHERE codigo = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DAOException("No se puede eliminar: el producto tiene ventas registradas", e);
        } catch (SQLException e) {
            throw new DAOException("No se pudo eliminar el producto " + codigo, e);
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Producto.
     *
     * @param rs resultado de una consulta, posicionado en una fila válida
     * @return producto con los datos de esa fila
     * @throws SQLException si alguna columna no se puede leer
     */
    private Producto crearProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getDouble("precio"),
                rs.getInt("inventario"));
    }
}