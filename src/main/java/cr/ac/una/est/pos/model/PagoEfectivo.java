package cr.ac.una.est.pos.model;

/**
 * Pago en efectivo. A diferencia de los otros métodos, necesita
 * saber cuánto dinero entregó el cliente para poder calcular el vuelto.
 */
public class PagoEfectivo extends Pago {

    private double montoRecibido;

    /**
     * Crea un pago en efectivo, validando que el dinero recibido
     * alcance para cubrir el monto total.
     *
     * @param monto el monto total a cobrar
     * @param montoRecibido el dinero que entregó el cliente
     */
    public PagoEfectivo(double monto, double montoRecibido) {
        super(monto);
        if (montoRecibido < monto) {
            throw new IllegalArgumentException("El monto recibido es menor al total a pagar.");
        }
        this.montoRecibido = montoRecibido;
    }

    /**
     * Calcula el vuelto a entregar.
     *
     * @return la diferencia entre lo recibido y el monto total
     */
    public double calcularVuelto() {
        return montoRecibido - monto;
    }

    /**
     * Obtiene el monto que entregó el cliente.
     *
     * @return el monto recibido
     */
    public double getMontoRecibido() {
        return montoRecibido;
    }

    /**
     * Genera el resumen de este pago, incluyendo el vuelto calculado.
     *
     * @return el resumen en texto
     */
    @Override
    public String generarResumen() {
        return String.format("Pago en Efectivo - Recibido: ₡%.2f - Vuelto: ₡%.2f", montoRecibido, calcularVuelto());
    }
}
