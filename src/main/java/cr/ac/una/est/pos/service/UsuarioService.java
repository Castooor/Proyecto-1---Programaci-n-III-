package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.model.TipoNegocio;
import cr.ac.una.est.pos.model.Usuario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servicio encargado de la gestión y autenticación de usuarios.
 * Lee y persiste usuarios en el archivo data/usuarios.csv.
 * Si el archivo no existe, carga usuarios predeterminados para
 * Soda y Pulpería.
 */
public class UsuarioService {

    private static final String RUTA_ARCHIVO = "data/usuarios.csv";

    private final ArrayList<Usuario> usuarios;

    public UsuarioService() {
        this.usuarios = new ArrayList<>();
        cargarUsuarios();

        // Si no se cargó ningún usuario (archivo vacío o inexistente), registrar los usuarios por defecto
        if (usuarios.isEmpty()) {
            usuarios.add(new Usuario("admin", "admin123", TipoNegocio.SODA));
            usuarios.add(new Usuario("pulperia", "admin123", TipoNegocio.PULPERIA));
            usuarios.add(new Usuario("admin", "admin123", TipoNegocio.ADMIN));
            guardarUsuarios();
        }
    }

    /**
     * Autentica a un usuario según su nombre y contraseña.
     *
     * @param nombre nombre de usuario ingresado
     * @param contrasena contraseña ingresada
     * @return el objeto Usuario con su TipoNegocio si coincide, o null si es inválido
     */
    public Usuario autenticar(String nombre, String contrasena) {
        if (nombre == null || contrasena == null) {
            return null;
        }
        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(nombre.trim()) && u.getContrasena().equals(contrasena)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param usuario el usuario a registrar
     */
    public void registrarUsuario(Usuario usuario) {
        if (buscarPorNombre(usuario.getNombre()) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre: " + usuario.getNombre());
        }
        usuarios.add(usuario);
        guardarUsuarios();
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombre el nombre a buscar
     * @return el usuario encontrado o null
     */
    public Usuario buscarPorNombre(String nombre) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(nombre.trim())) {
                return u;
            }
        }
        return null;
    }

    /**
     * Devuelve la lista completa de usuarios registrados.
     */
    public ArrayList<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    /**
     * Guarda la lista de usuarios en data/usuarios.csv.
     * Formato: nombre;contrasena;tipoNegocio
     */
    private void guardarUsuarios() {
        File archivo = new File(RUTA_ARCHIVO);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo))) {
            for (Usuario u : usuarios) {
                String linea = u.getNombre() + ";" + u.getContrasena() + ";" + u.getTipoNegocio().name();
                escritor.write(linea);
                escritor.newLine();
            }
        } catch (IOException e) {
            System.err.println("No se pudo guardar " + RUTA_ARCHIVO);
            e.printStackTrace();
        }
    }

    /**
     * Carga los usuarios desde data/usuarios.csv si existe.
     */
    private void cargarUsuarios() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return;
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea = lector.readLine();
            while (linea != null) {
                if (!linea.isBlank()) {
                    String[] partes = linea.split(";");
                    if (partes.length == 3) {
                        String nombre = partes[0].trim();
                        String pass = partes[1];
                        TipoNegocio tipo = TipoNegocio.valueOf(partes[2].trim());
                        usuarios.add(new Usuario(nombre, pass, tipo));
                    }
                }
                linea = lector.readLine();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar " + RUTA_ARCHIVO + ": " + e.getMessage());
        }
    }
}
