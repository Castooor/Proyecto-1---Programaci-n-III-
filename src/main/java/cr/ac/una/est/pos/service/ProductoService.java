package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.model.Producto;
import cr.ac.una.est.pos.util.RepositorioGenerico;
import java.util.ArrayList;

/**
 * Contiene la lógica de negocio para administrar el catálogo
 * de productos: crear, consultar, actualizar y eliminar.
 * Por ahora los productos se guardan en memoria, en un
 * RepositorioGenerico.
 */
public class ProductoService {

    private RepositorioGenerico<Producto> repositorio;

    /**
     * Crea el servicio con el repositorio de productos vacío.
     */
    public ProductoService() {
        this.repositorio = new RepositorioGenerico<>();
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
        repositorio.agregar(producto);
    }

    /**
     * Descuenta del inventario la cantidad vendida de un producto,
     * usado al confirmar una factura. Valida que no se descuente
     * más de lo que hay disponible (por seguridad, aunque ya se
     * validó antes al armar el carrito).
     *
     * @param producto el producto cuyo inventario se va a descontar
     * @param cantidad la cantidad vendida
     * @return no retorna nada
     */
    public void descontarInventario(Producto producto, int cantidad) {
        if (cantidad > producto.getCantidadInventario()) {
            throw new IllegalArgumentException(
                    "No hay suficiente inventario de " + producto.getNombre() + " para completar la venta.");
        }
        producto.setCantidadInventario(producto.getCantidadInventario() - cantidad);
    }

    /**
     * Obtiene la lista completa de productos del catálogo.
     *
     * @return la lista de todos los productos
     */
    public ArrayList<Producto> listarTodos() {
        return repositorio.listarTodos();
    }

    /**
     * Busca un producto por su código.
     *
     * @param codigo el código a buscar
     * @return el producto encontrado, o null si no existe ninguno con ese código
     */
    public Producto buscarPorCodigo(String codigo) {
        for (Producto producto : repositorio.listarTodos()) {
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
        repositorio.eliminar(producto);
    }
}