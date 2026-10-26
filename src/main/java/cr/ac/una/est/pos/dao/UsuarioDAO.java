package cr.ac.una.est.pos.dao;

import cr.ac.una.est.pos.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {
    void insertar(Usuario usuario) throws DAOException;
    Optional<Usuario> buscarPorNombre(String nombre) throws DAOException;
    List<Usuario> listarTodos() throws DAOException;
    void actualizar(Usuario usuario) throws DAOException;
    void eliminar(String nombre) throws DAOException;
}