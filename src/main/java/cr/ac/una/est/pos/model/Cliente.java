package cr.ac.una.est.pos.model;

/**
 * Representa un cliente que realiza un pedido en el sistema.
 * Incluye soporte para un cliente genérico, usado cuando no
 * se quiere registrar los datos de una persona específica.
 */
public class Cliente {

    private String cedula;
    private String nombre;
    private String telefono;

    /**
     * Crea un nuevo cliente con sus datos básicos.
     *
     * @param cedula identificación del cliente
     * @param nombre nombre completo del cliente
     * @param telefono número de teléfono de contacto
     */
    public Cliente(String cedula, String nombre, String telefono) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    /**
     * Crea el cliente genérico, usado cuando el pedido no
     * necesita asociarse a una persona en particular.
     *
     * @return un Cliente con datos genéricos predefinidos
     */
    public static Cliente clienteGenerico() {
        return new Cliente("N/A", "Cliente Genérico", "N/A");
    }

    /**
     * Indica si este cliente es el cliente genérico.
     *
     * @return true si es el cliente genérico, false si es una persona registrada
     */
    public boolean esGenerico() {
        return "Cliente Genérico".equals(nombre);
    }

    /**
     * Obtiene la cédula del cliente.
     *
     * @return la cédula del cliente
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Cambia la cédula del cliente.
     *
     * @param cedula la nueva cédula
     * @return no retorna nada
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * Obtiene el nombre del cliente.
     *
     * @return el nombre del cliente
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre del cliente.
     *
     * @param nombre el nuevo nombre
     * @return no retorna nada
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el teléfono del cliente.
     *
     * @return el teléfono del cliente
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Cambia el teléfono del cliente.
     *
     * @param telefono el nuevo teléfono
     * @return no retorna nada
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Representación en texto del cliente, útil para mostrarlo
     * en listas o ComboBox de selección.
     *
     * @return una cadena con el nombre del cliente
     */
    @Override
    public String toString() {
        return nombre;
    }
}