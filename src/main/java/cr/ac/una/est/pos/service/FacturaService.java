package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.model.Pago;
import cr.ac.una.est.pos.model.PagoEfectivo;
import cr.ac.una.est.pos.model.PagoSinpe;
import cr.ac.una.est.pos.model.PagoTarjeta;

/**
 * Contiene la lógica de negocio para facturar una orden: calcular
 * el IVA, el total final, y construir el Pago según el método
 * elegido por el cliente.
 */
public class FacturaService {

    private static final double PORCENTAJE_IVA = 0.13;

    /**
     * Calcula el IVA correspondiente al subtotal de la orden.
     *
     * @param orden la orden a facturar
     * @return el monto del IVA
     */
    public double calcularIva(Orden orden) {
        return orden.calcularSubtotal() * PORCENTAJE_IVA;
    }

    /**
     * Calcula el total final a pagar: subtotal + costo adicional
     * (envío, si aplica) + IVA.
     *
     * @param orden la orden a facturar
     * @return el total final
     */
    public double calcularTotalFinal(Orden orden) {
        return orden.calcularTotal() + calcularIva(orden);
    }

    /**
     * Crea un pago en efectivo por el total final de la orden.
     *
     * @param orden la orden a facturar
     * @param montoRecibido el dinero que entregó el cliente
     * @return el Pago creado
     */
    public Pago crearPagoEfectivo(Orden orden, double montoRecibido) {
        return new PagoEfectivo(calcularTotalFinal(orden), montoRecibido);
    }

    /**
     * Crea un pago con tarjeta por el total final de la orden.
     *
     * @param orden la orden a facturar
     * @return el Pago creado
     */
    public Pago crearPagoTarjeta(Orden orden) {
        return new PagoTarjeta(calcularTotalFinal(orden));
    }

    /**
     * Crea un pago con SINPE por el total final de la orden.
     *
     * @param orden la orden a facturar
     * @return el Pago creado
     */
    public Pago crearPagoSinpe(Orden orden) {
        return new PagoSinpe(calcularTotalFinal(orden));
    }
}