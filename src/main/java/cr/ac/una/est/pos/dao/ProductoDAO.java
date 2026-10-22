package cr.ac.una.est.pos.dao;

import cr.ac.una.est.pos.model.Producto;
import cr.ac.una.est.pos.model.TipoNegocio;

import java.util.ArrayList;

/**
 * Contrato de acceso a datos para los productos del sistema.
 */
public interface ProductoDAO {

    /**
     * Obtiene los productos del catálogo de un negocio.
     *
     * @param negocio negocio (SODA o PULPERIA) cuyo catálogo se quiere
     * @return lista de productos; vacía si el negocio no tiene ninguno
     */
    ArrayList<Producto> listarPorNegocio(TipoNegocio negocio);

    /**
     * Busca un producto por su código dentro del catálogo de un negocio.
     *
     * @param codigo código del producto
     * @param negocio negocio en cuyo catálogo se busca
     * @return el producto, o null si no existe en ese negocio
     */
    Producto buscarPorCodigo(String codigo, TipoNegocio negocio);

    /**
     * Guarda un producto nuevo en el catálogo de un negocio.
     *
     * @param producto producto a guardar
     * @param negocio negocio al que pertenece
     */
    void insertar(Producto producto, TipoNegocio negocio);

    /**
     * Actualiza nombre, categoría, precio e inventario de un producto existente.
     *
     * @param producto producto con los datos nuevos (se identifica por su código)
     */
    void actualizar(Producto producto);

    /**
     * Cambia solo la cantidad en inventario de un producto.
     *
     * @param codigo código del producto
     * @param nuevaCantidad inventario resultante
     */
    void actualizarInventario(String codigo, int nuevaCantidad);

    /**
     * Elimina un producto.
     *
     * @param codigo código del producto a eliminar
     */
    void eliminar(String codigo);
}