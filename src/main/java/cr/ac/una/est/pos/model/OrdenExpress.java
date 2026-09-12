package cr.ac.una.est.pos.model;

/**
 * Orden para entrega a domicilio. Requiere una dirección y
 * agrega automáticamente un costo fijo de envío.
 */
public class OrdenExpress extends Orden {

    private static final double COSTO_ENVIO = 1500.0;

    private String direccion;

    /**
     * Crea una nueva orden Express con entrega a domicilio.
     *
     * @param cliente el cliente al que pertenece la orden
     * @param direccion la dirección de entrega
     */
    public OrdenExpress(Cliente cliente, String direccion) {
        super(cliente);
        this.direccion = direccion;
    }

    /**
     * Las órdenes Express siempre suman el costo fijo de envío.
     *
     * @return el costo de envío
     */
    @Override
    public double calcularCostoAdicional() {
        return COSTO_ENVIO;
    }
    /**
     * Genera el resumen de esta orden Express, incluyendo la dirección.
     *
     * @return el resumen en texto
     */
    @Override
    public String generarResumen() {
        return "Orden Express - Entregar en: " + direccion + " - Cliente: " + cliente.getNombre();
    }


    /**
     * Obtiene la dirección de entrega de esta orden.
     *
     * @return la dirección
     */
    public String getDireccion() {
        return direccion;
    }
}