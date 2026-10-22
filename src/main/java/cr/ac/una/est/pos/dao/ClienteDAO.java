package cr.ac.una.est.pos.dao;

import cr.ac.una.est.pos.model.Cliente;

import java.util.ArrayList;

/**
 * Contrato de acceso a datos para los clientes del sistema.
 */
public interface ClienteDAO {

    /**
     * Obtiene todos los clientes guardados.
     *
     * @return lista de clientes, ordenada por nombre
     */
    ArrayList<Cliente> listarTodos();

    /**
     * Busca un cliente por su cédula.
     *
     * @param cedula cédula del cliente
     * @return el cliente, o null si no existe
     */
    Cliente buscarPorCedula(String cedula);

    /**
     * Guarda un cliente nuevo.
     *
     * @param cliente cliente a guardar
     */
    void insertar(Cliente cliente);

    /**
     * Actualiza el nombre y el teléfono de un cliente existente.
     *
     * @param cliente cliente con los datos nuevos (se identifica por su cédula)
     */
    void actualizar(Cliente cliente);

    /**
     * Elimina un cliente.
     *
     * @param cedula cédula del cliente a eliminar
     */
    void eliminar(String cedula);
}
