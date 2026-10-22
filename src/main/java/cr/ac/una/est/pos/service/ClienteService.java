package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.dao.ClienteDAO;
import cr.ac.una.est.pos.model.Cliente;

import java.util.ArrayList;

/**
 * Lógica de negocio para administrar los clientes: crear, consultar,
 * actualizar y eliminar. No conoce SQL: guarda y lee los datos
 * mediante un ClienteDAO.
 */
public class ClienteService {

    private final ClienteDAO dao;

    /**
     * Crea el servicio de clientes.
     *
     * @param dao objeto de acceso a datos que guarda los clientes
     */
    public ClienteService(ClienteDAO dao) {
        this.dao = dao;
    }

    /**
     * Agrega un nuevo cliente. No permite cédulas repetidas.
     *
     * @param cliente cliente a agregar
     * @throws IllegalArgumentException si ya existe un cliente con esa cédula
     */
    public void crear(Cliente cliente) {
        if (buscarPorCedula(cliente.getCedula()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con la cédula " + cliente.getCedula());
        }
        dao.insertar(cliente);
    }

    /**
     * Obtiene los clientes registrados, sin incluir al cliente genérico
     * (que solo se usa internamente para ventas sin cliente).
     *
     * @return lista de clientes visibles para el usuario
     */
    public ArrayList<Cliente> listarTodos() {
        ArrayList<Cliente> visibles = new ArrayList<>();
        for (Cliente cliente : dao.listarTodos()) {
            if (!cliente.esGenerico()) {
                visibles.add(cliente);
            }
        }
        return visibles;
    }

    /**
     * Busca un cliente por su cédula.
     *
     * @param cedula cédula a buscar
     * @return el cliente, o null si no existe
     */
    public Cliente buscarPorCedula(String cedula) {
        return dao.buscarPorCedula(cedula);
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cedula cédula del cliente a actualizar
     * @param nombre nombre nuevo
     * @param telefono teléfono nuevo
     * @throws IllegalArgumentException si no existe o es el cliente genérico
     */
    public void actualizar(String cedula, String nombre, String telefono) {
        Cliente cliente = buscarEditable(cedula);
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        dao.actualizar(cliente);
    }

    /**
     * Elimina un cliente.
     *
     * @param cedula cédula del cliente a eliminar
     * @throws IllegalArgumentException si no existe o es el cliente genérico
     */
    public void eliminar(String cedula) {
        buscarEditable(cedula);
        dao.eliminar(cedula);
    }

    /**
     * Busca un cliente que se pueda modificar o eliminar.
     *
     * @param cedula cédula a buscar
     * @return el cliente encontrado
     * @throws IllegalArgumentException si no existe o es el cliente genérico
     */
    private Cliente buscarEditable(String cedula) {
        Cliente cliente = buscarPorCedula(cedula);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con la cédula " + cedula);
        }
        if (cliente.esGenerico()) {
            throw new IllegalArgumentException("El cliente genérico no se puede modificar ni eliminar.");
        }
        return cliente;
    }
}