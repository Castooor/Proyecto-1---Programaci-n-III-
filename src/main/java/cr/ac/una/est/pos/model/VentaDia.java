package cr.ac.una.est.pos.model;

/**
 * Fila del reporte de ventas del día: un pedido facturado con su pago.
 * Es un objeto de solo lectura pensado para mostrarse en una tabla.
 */
public class VentaDia {

    private final int idPedido;
    private final String hora;
    private final String tipoOrden;
    private final String cliente;
    private final String metodo;
    private final double monto;

    /**
     * Crea una fila del reporte de ventas.
     *
     * @param idPedido número del pedido
     * @param hora hora de la venta, con formato HH:mm
     * @param tipoOrden LOCAL, EXPRESS o MOSTRADOR
     * @param cliente cédula del cliente (N/A si es genérico)
     * @param metodo método de pago: EFECTIVO, TARJETA o SINPE
     * @param monto total cobrado
     */
    public VentaDia(int idPedido, String hora, String tipoOrden,
                    String cliente, String metodo, double monto) {
        this.idPedido = idPedido;
        this.hora = hora;
        this.tipoOrden = tipoOrden;
        this.cliente = cliente;
        this.metodo = metodo;
        this.monto = monto;
    }

    /** @return número del pedido */
    public int getIdPedido() { return idPedido; }

    /** @return hora de la venta (HH:mm) */
    public String getHora() { return hora; }

    /** @return tipo de orden (LOCAL, EXPRESS o MOSTRADOR) */
    public String getTipoOrden() { return tipoOrden; }

    /** @return cédula del cliente */
    public String getCliente() { return cliente; }

    /** @return método de pago */
    public String getMetodo() { return metodo; }

    /** @return monto cobrado */
    public double getMonto() { return monto; }
}