package cr.ac.una.est.pos.dao.jdbc;

import cr.ac.una.est.pos.dao.DAOException;
import cr.ac.una.est.pos.dao.OrdenDAO;
import cr.ac.una.est.pos.model.*;
import cr.ac.una.est.pos.util.ConexionBD;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdenDAOJdbc implements OrdenDAO {

    @Override
    public void insertar(Orden orden, TipoNegocio negocio, Pago pago) throws DAOException {
        String sqlPedido = "INSERT INTO pedido (id_negocio, cedula_cliente, tipo_orden, numero_mesa, direccion, fecha_hora) "
                + "VALUES ((SELECT id_negocio FROM negocio WHERE nombre = ?), ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_pedido (id_pedido, codigo_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        String sqlInventario = "UPDATE producto SET inventario = inventario - ? WHERE codigo = ? AND inventario >= ?";
        String sqlPago = "INSERT INTO pago (id_pedido, metodo, monto) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Pedido (con los datos reales de la orden)
            try (PreparedStatement ps = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                String tipoOrden = "MOSTRADOR";
                Integer mesa = null;
                String direccion = null;

                if (orden instanceof OrdenLocal local) {
                    tipoOrden = "LOCAL";
                    mesa = local.getNumeroMesa();
                } else if (orden instanceof OrdenExpress express) {
                    tipoOrden = "EXPRESS";
                    direccion = express.getDireccion();
                }

                ps.setString(1, negocio.name());
                ps.setString(2, orden.getCliente() != null ? orden.getCliente().getCedula() : "N/A");
                ps.setString(3, tipoOrden);
                if (mesa != null) ps.setInt(4, mesa); else ps.setNull(4, Types.INTEGER);
                ps.setString(5, direccion);
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo obtener el ID del pedido.");
                    }
                    orden.setId(rs.getInt(1));
                }
            }

            // 2. Detalle + descuento de inventario (falla si no hay stock)
            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                 PreparedStatement psInv = conn.prepareStatement(sqlInventario)) {
                for (ItemOrden item : orden.getItems()) {
                    Producto p = item.getProducto();

                    psInv.setInt(1, item.getCantidad());
                    psInv.setString(2, p.getCodigo());
                    psInv.setInt(3, item.getCantidad());
                    if (psInv.executeUpdate() == 0) {
                        throw new DAOException("No hay suficiente inventario de " + p.getNombre(), null);
                    }

                    psDetalle.setInt(1, orden.getId());
                    psDetalle.setString(2, p.getCodigo());
                    psDetalle.setInt(3, item.getCantidad());
                    psDetalle.setDouble(4, p.getPrecio());
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();
            }

            // 3. Pago
            try (PreparedStatement ps = conn.prepareStatement(sqlPago)) {
                String metodo = pago instanceof PagoTarjeta ? "TARJETA"
                        : pago instanceof PagoSinpe ? "SINPE" : "EFECTIVO";
                ps.setInt(1, orden.getId());
                ps.setString(2, metodo);
                ps.setDouble(3, pago.getMonto());
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException | RuntimeException e) {
            rollbackSeguro(conn);
            orden.setId(0);
            if (e instanceof DAOException de) {
                throw de;
            }
            throw new DAOException("Error al guardar el pedido", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Deshace la transacción en curso sin lanzar errores.
     *
     * @param conn conexión con la transacción abierta (puede ser null)
     */
    private void rollbackSeguro(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Optional<Orden> buscarPorId(int id) throws DAOException {
        String sql = "SELECT * FROM pedido WHERE id_pedido = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Orden orden = mapearOrden(rs);
                    cargarDetallesEnOrden(orden, conn);
                    return Optional.of(orden);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar pedido por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Orden> listarTodas() throws DAOException {
        List<Orden> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM pedido ORDER BY fecha_hora DESC";
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Orden orden = mapearOrden(rs);
                cargarDetallesEnOrden(orden, conn);
                ordenes.add(orden);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar pedidos", e);
        }
        return ordenes;
    }

    @Override
    public void actualizarEstado(int id, String estado) throws DAOException {
        // Reservado para actualizar estado en caso de ampliar tabla pedido
    }

    @Override
    public void eliminar(int id) throws DAOException {
        String sql = "DELETE FROM pedido WHERE id_pedido = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar el pedido", e);
        }
    }

    private Orden mapearOrden(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo_orden");
        String cedulaCliente = rs.getString("cedula_cliente");

        Cliente cliente = new Cliente(cedulaCliente, "Cliente Genérico", "N/A");

        Orden orden;
        if ("EXPRESS".equalsIgnoreCase(tipo)) {
            String dir = rs.getString("direccion");
            orden = new OrdenExpress(cliente, dir != null ? dir : "Dirección Pendiente");
        } else if ("LOCAL".equalsIgnoreCase(tipo)) {
            int mesa = rs.getInt("numero_mesa");
            orden = new OrdenLocal(cliente, mesa > 0 ? mesa : 1);
        } else {
            orden = new OrdenMostrador(cliente);
        }

        orden.setId(rs.getInt("id_pedido"));
        return orden;
    }

    private void cargarDetallesEnOrden(Orden orden, Connection conn) throws SQLException {
        String sql = "SELECT d.*, p.nombre FROM detalle_pedido d " +
                "JOIN producto p ON d.codigo_producto = p.codigo WHERE d.id_pedido = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orden.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String codigo = rs.getString("codigo_producto");
                    String nombre = rs.getString("nombre");
                    double precio = rs.getDouble("precio_unitario");
                    int cantidad = rs.getInt("cantidad");

                    Producto p = new Producto(codigo, nombre, "", precio, 1);
                    orden.agregarProducto(p, cantidad);
                }
            }
        }
    }
}