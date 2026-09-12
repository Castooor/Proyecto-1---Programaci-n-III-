package cr.ac.una.est.pos.model;

/**
 * Pago mediante SINPE Móvil. Al igual que Tarjeta, no necesita
 * datos adicionales al monto.
 */
public class PagoSinpe extends Pago {

    /**
     * Crea un pago por SINPE por el monto indicado.
     *
     * @param monto el monto total a cobrar
     */
    public PagoSinpe(double monto) {
        super(monto);
    }

    /**
     * Genera el resumen de este pago.
     *
     * @return el resumen en texto
     */
    @Override
    public String generarResumen() {
        return String.format("Pago con SINPE Móvil - Monto: ₡%.2f", monto);
    }
}
