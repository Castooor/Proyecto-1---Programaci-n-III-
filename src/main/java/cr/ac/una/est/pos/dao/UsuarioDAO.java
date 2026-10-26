package cr.ac.una.est.pos.dao;

import cr.ac.una.est.pos.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos de los usuarios (tabla usuario).
 * El resto de la aplicación depende de esta interfaz y no de SQL.
 */
public interface UsuarioDAO {

    /**
     * Guarda un usuario nuevo.
     *
     * @param usuario usuario a guardar (si no tiene tipo de negocio se guarda como SODA)
     * @throws DAOException si falla la base de datos o el nombre ya existe
     */
    void insertar(Usuario usuario) throws DAOException;

    /**
     * Busca un usuario por su nombre.
     *
     * @param nombre nombre de usuario a buscar
     * @return el usuario, o Optional vacío si no existe
     * @throws DAOException si falla la base de datos
     */
    Optional<Usuario> buscarPorNombre(String nombre) throws DAOException;

    /**
     * Lista todos los usuarios registrados.
     *
     * @return lista de usuarios (vacía si no hay ninguno)
     * @throws DAOException si falla la base de datos
     */
    List<Usuario> listarTodos() throws DAOException;

    /**
     * Actualiza la contraseña y el tipo de negocio de un usuario existente.
     *
     * @param usuario usuario con los datos nuevos; se identifica por su nombre
     * @throws DAOException si falla la base de datos
     */
    void actualizar(Usuario usuario) throws DAOException;

    /**
     * Elimina un usuario.
     *
     * @param nombre nombre del usuario a eliminar
     * @throws DAOException si falla la base de datos
     */
    void eliminar(String nombre) throws DAOException;
}