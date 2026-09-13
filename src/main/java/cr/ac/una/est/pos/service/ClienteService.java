package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.model.Cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Contiene la lógica de negocio para administrar los clientes:
 * crear, consultar, actualizar y eliminar. Los clientes se guardan
 * en memoria en un ArrayList, y además se persisten en el archivo
 * data/clientes.csv para que no se pierdan al cerrar el programa.
 */
public class ClienteService {

    private static final String RUTA_ARCHIVO = "data/clientes.csv";

    private ArrayList<Cliente> clientes;

    /**
     * Crea el servicio y trata de cargar los clientes guardados
     * previamente en data/clientes.csv.
     */
    public ClienteService() {
        this.clientes = new ArrayList<>();
        cargarClientes();
    }

    /**
     * Agrega un nuevo cliente. No permite cédulas repetidas.
     */
    public void crear(Cliente cliente) {
        if (buscarPorCedula(cliente.getCedula()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con la cédula " + cliente.getCedula());
        }
        clientes.add(cliente);
        guardarClientes();
    }

    /**
     * Obtiene la lista completa de clientes registrados.
     */
    public ArrayList<Cliente> listarTodos() {
        return clientes;
    }

    /**
     * Busca un cliente por su cédula.
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
     */
    public void actualizar(String cedula, String nombre, String telefono) {
        Cliente cliente = buscarPorCedula(cedula);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con la cédula " + cedula);
        }
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        guardarClientes();
    }

    /**
     * Elimina un cliente, identificándolo por su cédula.
     */
    public void eliminar(String cedula) {
        Cliente cliente = buscarPorCedula(cedula);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con la cédula " + cedula);
        }
        clientes.remove(cliente);
        guardarClientes();
    }

    /**
     * Lee data/clientes.csv linea por linea y arma la lista de clientes.
     * Formato de cada linea: cedula;nombre;telefono
     * Si el archivo no existe todavia (primera vez que corre el programa),
     * simplemente no hace nada y la lista queda vacia.
     */
    private void cargarClientes() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return;
        }

        try {
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            String linea = lector.readLine();

            while (linea != null) {
                if (!linea.isBlank()) {
                    String[] partes = linea.split(";");
                    if (partes.length == 3) {
                        String cedula = partes[0];
                        String nombre = partes[1];
                        String telefono = partes[2];
                        clientes.add(new Cliente(cedula, nombre, telefono));
                    }
                }
                linea = lector.readLine();
            }
            lector.close();

        } catch (IOException e) {
            System.err.println("No se pudo leer " + RUTA_ARCHIVO);
            e.printStackTrace();
        }
    }

    /**
     * Escribe todos los clientes actuales en data/clientes.csv,
     * sobrescribiendo el archivo completo. Se llama despues de
     * crear, actualizar o eliminar un cliente.
     */
    private void guardarClientes() {
        File archivo = new File(RUTA_ARCHIVO);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        try {
            BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo));

            for (Cliente c : clientes) {
                String linea = c.getCedula() + ";" + c.getNombre() + ";" + c.getTelefono();
                escritor.write(linea);
                escritor.newLine();
            }

            escritor.close();

        } catch (IOException e) {
            System.err.println("No se pudo guardar " + RUTA_ARCHIVO);
            e.printStackTrace();
        }
    }
}