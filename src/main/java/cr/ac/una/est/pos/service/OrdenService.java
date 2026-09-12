package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.model.Cliente;
import cr.ac.una.est.pos.model.ItemOrden;
import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.model.OrdenExpress;
import cr.ac.una.est.pos.model.OrdenLocal;
import cr.ac.una.est.pos.model.Producto;

/**
 * Contiene la lógica de negocio para armar una orden: crearla según
 * su tipo, agregar productos al carrito validando el inventario
 * disponible, y quitar productos del carrito.
 */
public class OrdenService {

    private ProductoService productoService;

    /**
     * Crea el servicio de órdenes, apoyándose en el servicio de
     * productos para poder validar el inventario disponible.
     *
     * @param productoService el servicio de productos a usar para validaciones
     */
    public OrdenService(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Crea una nueva orden para comer en el local.
     *
     * @param cliente el cliente de la orden
     * @param numeroMesa el número de mesa
     * @return la orden creada
     */
    public Orden crearOrdenLocal(Cliente cliente, int numeroMesa) {
        return new OrdenLocal(cliente, numeroMesa);
    }

    /**
     * Crea una nueva orden Express (entrega a domicilio).
     *
     * @param cliente el cliente de la orden
     * @param direccion la dirección de entrega
     * @return la orden creada
     */
    public Orden crearOrdenExpress(Cliente cliente, String direccion) {
        return new OrdenExpress(cliente, direccion);
    }

    /**
     * Agrega un producto a la orden, validando que la cantidad total
     * pedida (lo que ya estaba en el carrito más lo nuevo) no supere
     * el inventario disponible. Si el producto ya estaba en el
     * carrito, simplemente aumenta la cantidad en vez de duplicar la fila.
     *
     * @param orden la orden a la que se agrega el producto
     * @param producto el producto a agregar
     * @param cantidad la cantidad deseada
     * @return no retorna nada
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
     * Quita por completo un producto del carrito de la orden.
     *
     * @param orden la orden de la que se quita el producto
     * @param producto el producto a quitar
     * @return no retorna nada
     */
    public void quitarProducto(Orden orden, Producto producto) {
        ItemOrden item = buscarItem(orden, producto);
        if (item != null) {
            orden.getItems().remove(item);
        }
    }

    /**
     * Busca cuánta cantidad de un producto ya está en el carrito.
     *
     * @param orden la orden donde buscar
     * @param producto el producto a buscar
     * @return la cantidad ya agregada, o 0 si no está en el carrito
     */
    private int cantidadEnCarrito(Orden orden, Producto producto) {
        ItemOrden item = buscarItem(orden, producto);
        return item == null ? 0 : item.getCantidad();
    }

    /**
     * Busca el ítem del carrito correspondiente a un producto.
     *
     * @param orden la orden donde buscar
     * @param producto el producto a buscar
     * @return el ItemOrden encontrado, o null si el producto no está en el carrito
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
