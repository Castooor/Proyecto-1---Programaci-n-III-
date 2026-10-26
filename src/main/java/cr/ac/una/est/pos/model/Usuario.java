package cr.ac.una.est.pos.model;

/**
 * Representa un usuario del sistema POS.
 * Contiene su nombre de usuario, contraseña y el tipo de negocio
 * (Soda o Pulpería) al que está asignado.
 */
public class Usuario {

    /**
     * Constructor vacío. Crea un usuario sin datos para llenarlo después con los setters
     * (UsuarioDAOJdbc lo usa al leer una fila de la tabla usuario).
     */
    public Usuario() {
    }

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

    /**
     * Devuelve el nombre de usuario.
     *
     * @return el nombre de usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre de usuario.
     *
     * @param nombre el nuevo nombre de usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la contraseña del usuario.
     *
     * @return la contraseña
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Cambia la contraseña del usuario.
     *
     * @param contrasena la nueva contraseña
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Devuelve el tipo de negocio asignado al usuario.
     *
     * @return SODA, PULPERIA o ADMIN
     */
    public TipoNegocio getTipoNegocio() {
        return tipoNegocio;
    }

    /**
     * Cambia el tipo de negocio asignado al usuario.
     *
     * @param tipoNegocio el nuevo tipo de negocio
     */
    public void setTipoNegocio(TipoNegocio tipoNegocio) {
        this.tipoNegocio = tipoNegocio;
    }

    /**
     * Devuelve una descripción legible del usuario, por ejemplo "soda (Soda)".
     *
     * @return el nombre seguido del tipo de negocio entre paréntesis
     */
    @Override
    public String toString() {
        return nombre + " (" + (tipoNegocio != null ? tipoNegocio.getDescripcion() : "Sin asignar") + ")";
    }
}
