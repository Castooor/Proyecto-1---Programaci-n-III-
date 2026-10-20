package cr.ac.una.est.pos.model;

/**
 * Orden para venta directa en mostrador / caja (típica de pulpería,
 * abastecedora o pedidos para llevar rápidos).
 * No requiere número de mesa ni tiene costo adicional de envío.
 */
public class OrdenMostrador extends Orden {

    /**
     * Crea una nueva orden de mostrador para un cliente.
     *
     * @param cliente el cliente al que pertenece la orden
     */
    public OrdenMostrador(Cliente cliente) {
        super(cliente);
    }

    /**
     * Las ventas de mostrador no tienen ningún recargo o costo adicional.
     *
     * @return siempre 0
     */
    @Override
    public double calcularCostoAdicional() {
        return 0;
    }

    /**
     * Genera el resumen en texto de esta orden de mostrador.
     *
     * @return el resumen legible
     */
    @Override
    public String generarResumen() {
        return "Orden Mostrador - Venta directa en caja - Cliente: " + cliente.getNombre();
    }
}
