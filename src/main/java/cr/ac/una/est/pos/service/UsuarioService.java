package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.dao.UsuarioDAO;
import cr.ac.una.est.pos.model.Usuario;

import java.util.ArrayList;

/**
 * Servicio de gestión y autenticación de usuarios. No conoce SQL:
 * lee y guarda los datos mediante un UsuarioDAO.
 */
public class UsuarioService {

    private final UsuarioDAO dao;

    /**
     * Crea el servicio de usuarios.
     *
     * @param dao objeto de acceso a datos de usuarios
     */
    public UsuarioService(UsuarioDAO dao) {
        this.dao = dao;
    }

    /**
     * Autentica a un usuario según su nombre y contraseña.
     *
     * @param nombre nombre de usuario ingresado
     * @param contrasena contraseña ingresada
     * @return el Usuario si las credenciales son válidas, o null si no
     */
    public Usuario autenticar(String nombre, String contrasena) {
        if (nombre == null || contrasena == null) {
            return null;
        }
        return dao.buscarPorNombre(nombre.trim())
                .filter(u -> u.getContrasena().equals(contrasena))
                .orElse(null);
    }

    /**
     * Registra un nuevo usuario.
     *
     * @param usuario usuario a registrar
     * @throws IllegalArgumentException si el nombre ya existe
     */
    public void registrarUsuario(Usuario usuario) {
        if (buscarPorNombre(usuario.getNombre()) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre: " + usuario.getNombre());
        }
        dao.insertar(usuario);
    }

    /**
     * Busca un usuario por nombre.
     *
     * @param nombre nombre a buscar
     * @return el usuario, o null si no existe
     */
    public Usuario buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre.trim()).orElse(null);
    }

    /**
     * Lista todos los usuarios registrados.
     *
     * @return lista de usuarios
     */
    public ArrayList<Usuario> listarTodos() {
        return new ArrayList<>(dao.listarTodos());
    }
}