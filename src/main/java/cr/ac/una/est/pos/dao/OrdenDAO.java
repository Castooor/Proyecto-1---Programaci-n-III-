package cr.ac.una.est.pos.dao;

import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.model.Pago;
import cr.ac.una.est.pos.model.TipoNegocio;
import java.util.List;
import java.util.Optional;

public interface OrdenDAO {

    /**
     * Guarda un pedido con su detalle y su pago, y descuenta el inventario,
     * todo en una sola transacción (o se guarda todo o no se guarda nada).
     *
     * @param orden pedido a guardar; recibe el id generado por la BD
     * @param negocio negocio (SODA o PULPERIA) que realiza la venta
     * @param pago pago con el que se cobra el pedido
     * @throws DAOException si falla la BD o no hay inventario suficiente
     */
    void insertar(Orden orden, TipoNegocio negocio, Pago pago) throws DAOException;

    Optional<Orden> buscarPorId(int id) throws DAOException;
    List<Orden> listarTodas() throws DAOException;
    void actualizarEstado(int id, String estado) throws DAOException;
    void eliminar(int id) throws DAOException;
}