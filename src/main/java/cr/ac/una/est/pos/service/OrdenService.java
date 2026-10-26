package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.dao.OrdenDAO;
import cr.ac.una.est.pos.model.Cliente;
import cr.ac.una.est.pos.model.ItemOrden;
import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.model.OrdenExpress;
import cr.ac.una.est.pos.model.OrdenLocal;
import cr.ac.una.est.pos.model.OrdenMostrador;
import cr.ac.una.est.pos.model.Pago;
import cr.ac.una.est.pos.model.Producto;
import cr.ac.una.est.pos.model.TipoNegocio;

import java.util.List;

/**
 * Lógica de las órdenes de un negocio (Soda o Pulpería): armar el carrito
 * y guardar el pedido facturado mediante un OrdenDAO.
 */
public class OrdenService {

    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final OrdenDAO ordenDAO;
    private final TipoNegocio negocio;

    /**
     * Crea el servicio de órdenes de un negocio.
     *
     * @param productoService servicio de productos del mismo negocio
     * @param clienteService servicio de clientes (compartido)
     * @param ordenDAO acceso a datos de pedidos
     * @param negocio negocio al que pertenecen los pedidos de este servicio
     */
    public OrdenService(ProductoService productoService, ClienteService clienteService,
                        OrdenDAO ordenDAO, TipoNegocio negocio) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.ordenDAO = ordenDAO;
        this.negocio = negocio;
    }

    /**
     * Crea una orden para consumir en una mesa.
     *
     * @param cliente cliente de la orden
     * @param numeroMesa número de mesa
     * @return la orden creada
     */
    public Orden crearOrdenLocal(Cliente cliente, int numeroMesa) {
        return new OrdenLocal(cliente, numeroMesa);
    }

    /**
     * Crea una orden express (a domicilio).
     *
     * @param cliente cliente de la orden
     * @param direccion dirección de entrega
     * @return la orden creada
     */
    public Orden crearOrdenExpress(Cliente cliente, String direccion) {
        return new OrdenExpress(cliente, direccion);
    }

    /**
     * Crea una orden de mostrador.
     *
     * @param cliente cliente de la orden
     * @return la orden creada
     */
    public Orden crearOrdenMostrador(Cliente cliente) {
        return new OrdenMostrador(cliente);
    }

    /**
     * Agrega un producto al carrito validando cantidad e inventario.
     *
     * @param orden orden en construcción
     * @param producto producto a agregar
     * @param cantidad unidades a agregar
     * @throws IllegalArgumentException si la cantidad es inválida o excede el inventario
     */
    public void agregarProducto(Orden orden, Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        int cantidadYaEnCarrito = cantidadEnCarrito(orden, producto);
        if (cantidadYaEnCarrito + cantidad > producto.getCantidadInventario()) {
            throw new IllegalArgumentException(
                    "No hay suficiente inventario de " + producto.getNombre()
                            + ". Disponible: " + producto.getCantidadInventario());
        }
        ItemOrden itemExistente = buscarItem(orden, producto);
        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
        } else {
            orden.agregarProducto(producto, cantidad);
        }
    }

    /**
     * Quita un producto del carrito.
     *
     * @param orden orden en construcción
     * @param producto producto a quitar
     */
    public void quitarProducto(Orden orden, Producto producto) {
        ItemOrden item = buscarItem(orden, producto);
        if (item != null) {
            orden.getItems().remove(item);
        }
    }

    /**
     * Guarda el pedido en la base de datos junto con su pago y el descuento
     * de inventario (una sola transacción). Se llama al confirmar el pago.
     *
     * @param orden orden a guardar; recibe el id generado por la BD
     * @param pago pago con el que se cobró
     * @throws cr.ac.una.est.pos.dao.DAOException si falla la BD o no hay inventario
     */
    public void finalizarPedido(Orden orden, Pago pago) {
        ordenDAO.insertar(orden, negocio, pago);
        // La BD ya descontó el inventario; sincronizamos los objetos en memoria
        for (ItemOrden item : orden.getItems()) {
            Producto p = item.getProducto();
            p.setCantidadInventario(p.getCantidadInventario() - item.getCantidad());
        }
    }

    /**
     * Lista todos los pedidos guardados en la base de datos.
     *
     * @return lista de pedidos, del más reciente al más antiguo
     */
    public List<Orden> listarPedidos() {
        return ordenDAO.listarTodas();
    }

    /**
     * Cuenta cuántas unidades de un producto hay ya en el carrito.
     *
     * @param orden orden en construcción
     * @param producto producto a buscar
     * @return cantidad en el carrito (0 si no está)
     */
    private int cantidadEnCarrito(Orden orden, Producto producto) {
        ItemOrden item = buscarItem(orden, producto);
        return item == null ? 0 : item.getCantidad();
    }

    /**
     * Busca el ítem de un producto dentro de la orden.
     *
     * @param orden orden en construcción
     * @param producto producto a buscar
     * @return el ítem, o null si no está en la orden
     */
    private ItemOrden buscarItem(Orden orden, Producto producto) {
        for (ItemOrden item : orden.getItems()) {
            if (item.getProducto().getCodigo().equals(producto.getCodigo())) {
                return item;
            }
        }
        return null;
    }
}