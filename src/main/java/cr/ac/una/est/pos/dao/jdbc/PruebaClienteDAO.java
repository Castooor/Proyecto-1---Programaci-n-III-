package cr.ac.una.est.pos.dao.jdbc;

import cr.ac.una.est.pos.dao.ClienteDAO;
import cr.ac.una.est.pos.model.Cliente;

/** Clase temporal para probar el DAO. Borrar después de probar. */
public class PruebaClienteDAO {

    public static void main(String[] args) {
        ClienteDAO dao = new ClienteDAOJdbc();

        System.out.println("Todos: " + dao.listarTodos());   // incluye el genérico y Juan Murillo

        dao.insertar(new Cliente("11111111", "Ana Prueba", "88887777"));
        System.out.println("Insertado:   " + dao.buscarPorCedula("11111111"));

        dao.actualizar(new Cliente("11111111", "Ana Prueba 2", "88880000"));
        System.out.println("Actualizado: " + dao.buscarPorCedula("11111111"));

        try {
            dao.insertar(new Cliente("11111111", "Repetida", "1"));
            System.out.println("ERROR: debió fallar por cédula repetida");
        } catch (RuntimeException e) {
            System.out.println("Repetida OK: " + e.getMessage());
        }

        dao.eliminar("11111111");
        System.out.println("Eliminado:   " + dao.buscarPorCedula("11111111"));   // null
    }
}
