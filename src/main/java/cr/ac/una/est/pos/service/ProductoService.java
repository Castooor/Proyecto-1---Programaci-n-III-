package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.dao.ProductoDAO;
import cr.ac.una.est.pos.model.Producto;
import cr.ac.una.est.pos.model.TipoNegocio;

import java.util.ArrayList;

/**
 * Lógica de los productos de un negocio (Soda o Pulpería).
 * No conoce SQL: guarda y lee los datos mediante un ProductoDAO.
 */
public class ProductoService {

    private final ProductoDAO dao;
    private final TipoNegocio negocio;

    /**
     * Crea el servicio de productos de un negocio.
     *
     * @param dao objeto de acceso a datos que guarda los productos
     * @param negocio negocio cuyo catálogo administra este servicio
     */
    public ProductoService(ProductoDAO dao, TipoNegocio negocio) {
        this.dao = dao;
        this.negocio = negocio;
    }

    /**
     * Agrega un producto nuevo al catálogo del negocio.
     *
     * @param producto producto a agregar
     * @throws IllegalArgumentException si ya existe un producto con ese código
     */
    public void crear(Producto producto) {
        if (buscarPorCodigo(producto.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un producto con el código " + producto.getCodigo());
        }
        dao.insertar(producto, negocio);
    }

    /**
     * Descuenta unidades del inventario de un producto vendido.
     *
     * @param producto producto vendido
     * @param cantidad unidades vendidas
     * @throws IllegalArgumentException si no hay suficiente inventario
     */
    public void descontarInventario(Producto producto, int cantidad) {
        if (cantidad > producto.getCantidadInventario()) {
            throw new IllegalArgumentException(
                    "No hay suficiente inventario de " + producto.getNombre() + " para completar la venta.");
        }
        int nuevaCantidad = producto.getCantidadInventario() - cantidad;
        dao.actualizarInventario(producto.getCodigo(), nuevaCantidad);   // primero la base de datos
        producto.setCantidadInventario(nuevaCantidad);                   // luego el objeto en memoria
    }

    /**
     * Lista los productos del catálogo del negocio.
     *
     * @return productos del negocio
     */
    public ArrayList<Producto> listarTodos() {
        return dao.listarPorNegocio(negocio);
    }

    /**
     * Busca un producto por código en el catálogo del negocio.
     *
     * @param codigo código a buscar
     * @return el producto, o null si no existe en este negocio
     */
    public Producto buscarPorCodigo(String codigo) {
        return dao.buscarPorCodigo(codigo, negocio);
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param codigo código del producto a actualizar
     * @param nombre nombre nuevo
     * @param categoria categoría nueva
     * @param precio precio nuevo
     * @param cantidadInventario inventario nuevo
     * @throws IllegalArgumentException si el producto no existe en este negocio
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
        dao.actualizar(producto);
    }

    /**
     * Elimina un producto del catálogo.
     *
     * @param codigo código del producto a eliminar
     * @throws IllegalArgumentException si el producto no existe en este negocio
     */
    public void eliminar(String codigo) {
        if (buscarPorCodigo(codigo) == null) {
            throw new IllegalArgumentException("No existe un producto con el código " + codigo);
        }
        dao.eliminar(codigo);
    }
}