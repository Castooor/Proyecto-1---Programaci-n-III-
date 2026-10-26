package cr.ac.una.est.pos.model;

/**
 * Registro de la bitácora del sistema: quién hizo qué y cuándo.
 * Es un objeto de solo lectura pensado para mostrarse en una tabla.
 */
public class LogAccion {

    private final String fechaHora;
    private final String usuario;
    private final String negocio;
    private final String accion;
    private final String detalle;

    /**
     * Crea un registro de bitácora.
     *
     * @param fechaHora fecha y hora ya formateadas para mostrar
     * @param usuario nombre del usuario que realizó la acción
     * @param negocio negocio donde ocurrió (vacío si fue una acción general)
     * @param accion tipo de acción: LOGIN, LOGOUT, FACTURA, etc.
     * @param detalle descripción breve de la acción
     */
    public LogAccion(String fechaHora, String usuario, String negocio,
                     String accion, String detalle) {
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.negocio = negocio;
        this.accion = accion;
        this.detalle = detalle;
    }

    /** @return fecha y hora de la acción */
    public String getFechaHora() { return fechaHora; }

    /** @return usuario que realizó la acción */
    public String getUsuario() { return usuario; }

    /** @return negocio asociado (puede estar vacío) */
    public String getNegocio() { return negocio; }

    /** @return tipo de acción */
    public String getAccion() { return accion; }

    /** @return detalle de la acción */
    public String getDetalle() { return detalle; }
}