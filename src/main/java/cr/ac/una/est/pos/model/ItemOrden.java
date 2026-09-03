package cr.ac.una.est.pos.model;

/**
 * Representa una línea dentro de una orden: un producto específico
 * junto con la cantidad que el cliente está pidiendo de él.
 */
public class ItemOrden {

    private Producto producto;
    private int cantidad;

    /**
     * Crea un nuevo ítem de orden.
     *
     * @param producto el producto que se está agregando
     * @param cantidad cuántas unidades se están pidiendo
     */
    public ItemOrden(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el producto de este ítem.
     *
     * @return el producto
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Obtiene la cantidad pedida de este producto.
     *
     * @return la cantidad
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Cambia la cantidad pedida de este producto.
     *
     * @param cantidad la nueva cantidad
     * @return no retorna nada
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Calcula el subtotal de este ítem (precio del producto por cantidad).
     *
     * @return el subtotal de este ítem
     */
    public double calcularSubtotal() {
        return producto.getPrecio() * cantidad;
    }
}