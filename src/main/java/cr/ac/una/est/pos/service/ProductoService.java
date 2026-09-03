package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.model.Producto;
import java.util.ArrayList;

/**
 * Contiene la lógica de negocio para administrar el catálogo
 * de productos: crear, consultar, actualizar y eliminar.
 * Por ahora los productos se guardan en memoria, en una lista.
 */
public class ProductoService {

    private ArrayList<Producto> productos;

    /**
     * Crea el servicio con la lista de productos vacía.
     */
    public ProductoService() {
        this.productos = new ArrayList<>();
    }

    /**
     * Agrega un nuevo producto al catálogo. No permite códigos
     * repetidos, para evitar productos duplicados.
     *
     * @param producto el producto a agregar
     * @return no retorna nada
     */
    public void crear(Producto producto) {
        if (buscarPorCodigo(producto.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un producto con el código " + producto.getCodigo());
        }
        productos.add(producto);
    }

    /**
     * Obtiene la lista completa de productos del catálogo.
     *
     * @return la lista de todos los productos
     */
    public ArrayList<Producto> listarTodos() {
        return productos;
    }

    /**
     * Busca un producto por su código.
     *
     * @param codigo el código a buscar
     * @return el producto encontrado, o null si no existe ninguno con ese código
     */
    public Producto buscarPorCodigo(String codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo().equals(codigo)) {
                return producto;
            }
        }
        return null;
    }

    /**
     * Actualiza los datos de un producto existente, identificándolo
     * por su código.
     *
     * @param codigo código del producto a actualizar
     * @param nombre nuevo nombre
     * @param categoria nueva categoría
     * @param precio nuevo precio
     * @param cantidadInventario nueva cantidad en inventario
     * @return no retorna nada
     */
    public void actualizar(String codigo, String nombre, String categoria, double precio, int cantidadInventario) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto == null) {
            throw new IllegalArgumentException("No existe un producto con el código " + codigo);
        }
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setCantidadInventario(cantidadInventario);
    }

    /**
     * Elimina un producto del catálogo, identificándolo por su código.
     *
     * @param codigo código del producto a eliminar
     * @return no retorna nada
     */
    public void eliminar(String codigo) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto == null) {
            throw new IllegalArgumentException("No existe un producto con el código " + codigo);
        }
        productos.remove(producto);
    }
}
