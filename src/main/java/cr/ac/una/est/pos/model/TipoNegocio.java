package cr.ac.una.est.pos.model;

/**
 * Representa el tipo de negocio al que pertenece un usuario o entorno POS.
 * Permite diferenciar entre la operación de una Soda (con mesas)
 * y una Pulpería / Abastecedora (con venta en mostrador).
 */
public enum TipoNegocio {
    SODA("Soda"),
    PULPERIA("Pulpería"),
    ADMIN("Administrador");

    private final String descripcion;

    TipoNegocio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
