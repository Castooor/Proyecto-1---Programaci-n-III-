package cr.ac.una.est.pos.model;

import java.util.ArrayList;

/**
 * Representa una orden de pedido en el sistema. Es una clase abstracta
 * porque toda orden real es, en la práctica, una OrdenLocal o una
 * OrdenExpress — Orden solo agrupa lo que ambas tienen en común.
 */
public abstract class Orden {

    protected Cliente cliente;
    protected ArrayList<ItemOrden> items;

    /**
     * Crea una nueva orden vacía asociada a un cliente.
     *
     * @param cliente el cliente al que pertenece la orden
     */
    public Orden(Cliente cliente) {
        this.cliente = cliente;
        this.items = new ArrayList<>();
    }

    /**
     * Agrega un producto a la orden con la cantidad indicada.
     *
     * @param producto el producto a agregar
     * @param cantidad la cantidad deseada
     * @return no retorna nada
     */
    public void agregarProducto(Producto producto, int cantidad) {
        items.add(new ItemOrden(producto, cantidad));
    }

    /**
     * Calcula el subtotal de la orden, sumando el subtotal de cada ítem.
     *
     * @return el subtotal de la orden, sin impuestos ni cargos adicionales
     */
    public double calcularSubtotal() {
        double subtotal = 0;
        for (ItemOrden item : items) {
            subtotal += item.calcularSubtotal();
        }
        return subtotal;
    }

    /**
     * Calcula cualquier costo adicional propio del tipo de orden
     * (por ejemplo, el costo de envío en una orden Express).
     * Cada subclase decide cómo calcularlo.
     *
     * @return el costo adicional de esta orden
     */
    public abstract double calcularCostoAdicional();

    /**
     * Calcula el total de la orden, incluyendo el costo adicional
     * propio del tipo de orden (pero sin el IVA, que se calcula
     * en la etapa de facturación).
     *
     * @return el total de la orden
     */
    public double calcularTotal() {
        return calcularSubtotal() + calcularCostoAdicional();
    }

    /**
     * Obtiene la lista de ítems de la orden.
     *
     * @return la lista de ítems
     */
    public ArrayList<ItemOrden> getItems() {
        return items;
    }

    /**
     * Obtiene el cliente asociado a la orden.
     *
     * @return el cliente
     */
    public Cliente getCliente() {
        return cliente;
    }
}