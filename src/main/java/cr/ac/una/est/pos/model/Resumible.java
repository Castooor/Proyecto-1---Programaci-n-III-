package cr.ac.una.est.pos.model;

/**
 * Contrato para cualquier clase que pueda generar un resumen en
 * texto de sus datos, útil para mostrarlo en la factura final.
 * Lo implementan Orden y Pago — dos jerarquías completamente
 * distintas y sin relación de herencia entre sí, pero que
 * comparten esta misma capacidad.
 */
public interface Resumible {

    /**
     * Genera un resumen en texto de los datos relevantes del objeto.
     *
     * @return el resumen en texto
     */
    String generarResumen();
}
