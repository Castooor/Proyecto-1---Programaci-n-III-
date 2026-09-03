package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.model.Cliente;
import java.util.ArrayList;

/**
 * Contiene la lógica de negocio para administrar los clientes:
 * crear, consultar, actualizar y eliminar. Los clientes se
 * guardan en memoria, en una lista.
 */
public class ClienteService {

    private ArrayList<Cliente> clientes;

    /**
     * Crea el servicio con la lista de clientes vacía.
     */
    public ClienteService() {
        this.clientes = new ArrayList<>();
    }

    /**
     * Agrega un nuevo cliente. No permite cédulas repetidas.
     *
     * @param cliente el cliente a agregar
     * @return no retorna nada
     */
    public void crear(Cliente cliente) {
        if (buscarPorCedula(cliente.getCedula()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con la cédula " + cliente.getCedula());
        }
        clientes.add(cliente);
    }

    /**
     * Obtiene la lista completa de clientes registrados.
     *
     * @return la lista de todos los clientes
     */
    public ArrayList<Cliente> listarTodos() {
        return clientes;
    }

    /**
     * Busca un cliente por su cédula.
     *
     * @param cedula la cédula a buscar
     * @return el cliente encontrado, o null si no existe ninguno con esa cédula
     */
    public Cliente buscarPorCedula(String cedula) {
        for (Cliente cliente : clientes) {
            if (cliente.getCedula().equals(cedula)) {
                return cliente;
            }
        }
        return null;
    }

    /**
     * Actualiza los datos de un cliente existente, identificándolo
     * por su cédula.
     *
     * @param cedula cédula del cliente a actualizar
     * @param nombre nuevo nombre
     * @param telefono nuevo teléfono
     * @return no retorna nada
     */
    public void actualizar(String cedula, String nombre, String telefono) {
        Cliente cliente = buscarPorCedula(cedula);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con la cédula " + cedula);
        }
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
    }

    /**
     * Elimina un cliente, identificándolo por su cédula.
     *
     * @param cedula cédula del cliente a eliminar
     * @return no retorna nada
     */
    public void eliminar(String cedula) {
        Cliente cliente = buscarPorCedula(cedula);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con la cédula " + cedula);
        }
        clientes.remove(cliente);
    }
}