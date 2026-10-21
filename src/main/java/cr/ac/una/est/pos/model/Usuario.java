package cr.ac.una.est.pos.model;

/**
 * Representa un usuario del sistema POS.
 * Contiene su nombre de usuario, contraseña y el tipo de negocio
 * (Soda o Pulpería) al que está asignado.
 */
public class Usuario {

    private String nombre;
    private String contrasena;
    private TipoNegocio tipoNegocio;

    /**
     * Constructor con todos los atributos del usuario.
     *
     * @param nombre el identificador o nombre de usuario
     * @param contrasena la contraseña del usuario
     * @param tipoNegocio el tipo de negocio asociado (SODA o PULPERIA)
     */
    public Usuario(String nombre, String contrasena, TipoNegocio tipoNegocio) {
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.tipoNegocio = tipoNegocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public TipoNegocio getTipoNegocio() {
        return tipoNegocio;
    }

    public void setTipoNegocio(TipoNegocio tipoNegocio) {
        this.tipoNegocio = tipoNegocio;
    }

    @Override
    public String toString() {
        return nombre + " (" + (tipoNegocio != null ? tipoNegocio.getDescripcion() : "Sin asignar") + ")";
    }
}
