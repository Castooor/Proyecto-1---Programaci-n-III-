package cr.ac.una.est.pos.dao;

import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.model.Pago;
import cr.ac.una.est.pos.model.TipoNegocio;
import cr.ac.una.est.pos.model.VentaDia;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

/**
 * Contrato de acceso a datos de los pedidos (tablas pedido, detalle_pedido y pago).
 * El resto de la aplicación depende de esta interfaz y no de SQL.
 */
public interface OrdenDAO {

    /**
     * Lista las ventas (pedido + pago) de un negocio en un día.
     *
     * @param negocio negocio a consultar (SODA o PULPERIA)
     * @param fecha día a consultar
     * @return ventas del día, de la más reciente a la más antigua
     * @throws DAOException si falla la base de datos
     */
    List<VentaDia> listarVentasDelDia(TipoNegocio negocio, LocalDate fecha) throws DAOException;

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

    /**
     * Busca un pedido por su número, con sus productos.
     *
     * @param id número del pedido
     * @return el pedido, o Optional vacío si no existe
     * @throws DAOException si falla la base de datos
     */
    Optional<Orden> buscarPorId(int id) throws DAOException;

    /**
     * Lista todos los pedidos, con sus productos.
     *
     * @return pedidos del más reciente al más antiguo
     * @throws DAOException si falla la base de datos
     */
    List<Orden> listarTodas() throws DAOException;

    /**
     * Reservado para actualizar el estado de un pedido si se amplía la tabla pedido.
     *
     * @param id número del pedido
     * @param estado nuevo estado
     * @throws DAOException si falla la base de datos
     */
    void actualizarEstado(int id, String estado) throws DAOException;

    /**
     * Elimina un pedido (su detalle y su pago se borran en cascada).
     *
     * @param id número del pedido a eliminar
     * @throws DAOException si falla la base de datos
     */
    void eliminar(int id) throws DAOException;
}