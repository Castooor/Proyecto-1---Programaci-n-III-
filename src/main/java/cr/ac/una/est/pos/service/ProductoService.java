package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.model.Producto;
import cr.ac.una.est.pos.util.RepositorioGenerico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ProductoService {

    private final String rutaArchivo;

    private RepositorioGenerico<Producto> repositorio;

    /**
     * Crea el servicio de productos asociado a un archivo CSV específico,
     * lo que permite tener un catálogo separado por negocio.
     *
     * @param rutaArchivo ruta del CSV (ej. "data/productos_soda.csv")
     */
    public ProductoService(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.repositorio = new RepositorioGenerico<>();
        cargarProductos();
    }

    public void crear(Producto producto) {
        if (buscarPorCodigo(producto.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un producto con el código " + producto.getCodigo());
        }
        repositorio.agregar(producto);
        guardarProductos();
    }

    public void descontarInventario(Producto producto, int cantidad) {
        if (cantidad > producto.getCantidadInventario()) {
            throw new IllegalArgumentException(
                    "No hay suficiente inventario de " + producto.getNombre() + " para completar la venta.");
        }
        producto.setCantidadInventario(producto.getCantidadInventario() - cantidad);
        guardarProductos();
    }

    public ArrayList<Producto> listarTodos() {
        return repositorio.listarTodos();
    }

    public Producto buscarPorCodigo(String codigo) {
        for (Producto producto : repositorio.listarTodos()) {
            if (producto.getCodigo().equals(codigo)) {
                return producto;
            }
        }
        return null;
    }

    public void actualizar(String codigo, String nombre, String categoria, double precio, int cantidadInventario) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto == null) {
            throw new IllegalArgumentException("No existe un producto con el código " + codigo);
        }
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setCantidadInventario(cantidadInventario);
        guardarProductos();
    }

    public void eliminar(String codigo) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto == null) {
            throw new IllegalArgumentException("No existe un producto con el código " + codigo);
        }
        repositorio.eliminar(producto);
        guardarProductos();
    }

    /**
     * Lee data/productos_soda.csv linea por linea y arma la lista de productos.
     * Formato de cada linea: codigo;nombre;categoria;precio;cantidadInventario
     * Si el archivo no existe (primera vez), no hace nada.
     */
    private void cargarProductos() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            return;
        }

        try {
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            String linea = lector.readLine();

            while (linea != null) {
                if (!linea.isBlank()) {
                    String[] partes = linea.split(";");
                    if (partes.length == 5) {
                        String codigo = partes[0];
                        String nombre = partes[1];
                        String categoria = partes[2];
                        double precio = Double.parseDouble(partes[3]);
                        int cantidad = Integer.parseInt(partes[4]);
                        repositorio.agregar(new Producto(codigo, nombre, categoria, precio, cantidad));
                    }
                }
                linea = lector.readLine();
            }
            lector.close();

        } catch (IOException e) {
            System.err.println("No se pudo leer " + rutaArchivo);
            e.printStackTrace();
        }
    }

    /**
     * Escribe todos los productos actuales en data/productos_soda.csv,
     * sobrescribiendo el archivo. Se llama despues de cada cambio.
     */
    private void guardarProductos() {
        File archivo = new File(rutaArchivo);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        try {
            BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo));

            for (Producto p : repositorio.listarTodos()) {
                String linea = p.getCodigo() + ";" + p.getNombre() + ";" + p.getCategoria()
                        + ";" + p.getPrecio() + ";" + p.getCantidadInventario();
                escritor.write(linea);
                escritor.newLine();
            }

            escritor.close();

        } catch (IOException e) {
            System.err.println("No se pudo guardar " + rutaArchivo);
            e.printStackTrace();
        }
    }
}
