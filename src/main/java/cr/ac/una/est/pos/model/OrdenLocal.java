package cr.ac.una.est.pos.model;

/**
 * Orden para consumo en el local. No tiene costo adicional,
 * pero requiere un número de mesa.
 */
public class OrdenLocal extends Orden {

    private int numeroMesa;

    /**
     * Crea una nueva orden para comer en el local.
     *
     * @param cliente el cliente al que pertenece la orden
     * @param numeroMesa el número de mesa donde se sienta el cliente
     */
    public OrdenLocal(Cliente cliente, int numeroMesa) {
        super(cliente);
        this.numeroMesa = numeroMesa;
    }

    /**
     * Las órdenes para comer en el local no tienen costo adicional.
     *
     * @return siempre 0
     */
    @Override
    public double calcularCostoAdicional() {
        return 0;
    }
    /**
     * Genera el resumen de esta orden local, incluyendo el número de mesa.
     *
     * @return el resumen en texto
     */
    @Override
    public String generarResumen() {
        return "Orden Local - Mesa " + numeroMesa + " - Cliente: " + cliente.getNombre();
    }


    /**
     * Obtiene el número de mesa de esta orden.
     *
     * @return el número de mesa
     */
    public int getNumeroMesa() {
        return numeroMesa;
    }

}
