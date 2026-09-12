package cr.ac.una.est.pos.model;

/**
 * Pago con tarjeta. No necesita datos adicionales al monto —
 * se asume que la transacción se procesa fuera del sistema.
 */
public class PagoTarjeta extends Pago {

    /**
     * Crea un pago con tarjeta por el monto indicado.
     *
     * @param monto el monto total a cobrar
     */
    public PagoTarjeta(double monto) {
        super(monto);
    }

    /**
     * Genera el resumen de este pago.
     *
     * @return el resumen en texto
     */
    @Override
    public String generarResumen() {
        return String.format("Pago con Tarjeta - Monto: ₡%.2f", monto);
    }
}
