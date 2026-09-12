package cr.ac.una.est.pos.model;

/**
 * Representa el pago de una orden. Es una clase abstracta porque
 * el comportamiento específico (cómo se describe en el resumen,
 * y en algunos casos cómo se calcula el vuelto) depende del
 * método de pago elegido.
 */
public abstract class Pago implements Resumible {

    protected double monto;

    /**
     * Crea un nuevo pago por el monto indicado.
     *
     * @param monto el monto total a cobrar
     */
    public Pago(double monto) {
        this.monto = monto;
    }

    /**
     * Obtiene el monto total de este pago.
     *
     * @return el monto
     */
    public double getMonto() {
        return monto;
    }
}
