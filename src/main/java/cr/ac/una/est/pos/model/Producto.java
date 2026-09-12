package cr.ac.una.est.pos.model;

/**
 * Representa un producto del catálogo de la soda o pulpería.
 * Contiene la información básica que se maneja en el inventario
 * y que se usa al armar una orden.
 */
public class Producto {

    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int cantidadInventario;

    /**
     * Crea un nuevo producto con todos sus datos.
     *
     * @param codigo código único que identifica el producto
     * @param nombre nombre del producto
     * @param categoria categoría a la que pertenece (ej. Bebidas, Snacks)
     * @param precio precio unitario de venta
     * @param cantidadInventario cantidad disponible en inventario
     */
    public Producto(String codigo, String nombre, String categoria, double precio, int cantidadInventario) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        setPrecio(precio);
        setCantidadInventario(cantidadInventario);
    }

    /**
     * Obtiene el código del producto.
     *
     * @return el código del producto
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Cambia el código del producto.
     *
     * @param codigo el nuevo código
     * @return no retorna nada
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return el nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre del producto.
     *
     * @param nombre el nuevo nombre
     * @return no retorna nada
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la categoría del producto.
     *
     * @return la categoría del producto
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Cambia la categoría del producto.
     *
     * @param categoria la nueva categoría
     * @return no retorna nada
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el precio unitario del producto.
     *
     * @return el precio del producto
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Cambia el precio del producto. No permite precios negativos.
     *
     * @param precio el nuevo precio
     * @return no retorna nada
     */
    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }

    /**
     * Obtiene la cantidad disponible en inventario.
     *
     * @return la cantidad en inventario
     */
    public int getCantidadInventario() {
        return cantidadInventario;
    }

    /**
     * Cambia la cantidad disponible en inventario. No permite
     * cantidades negativas.
     *
     * @param cantidadInventario la nueva cantidad
     * @return no retorna nada
     */
    public void setCantidadInventario(int cantidadInventario) {
        if (cantidadInventario < 0) {
            throw new IllegalArgumentException("La cantidad en inventario no puede ser negativa");
        }
        this.cantidadInventario = cantidadInventario;
    }

    /**
     * Representación en texto del producto, útil para mostrarlo
     * en listas o para depuración.
     *
     * @return una cadena con los datos principales del producto
     */
    @Override
    public String toString() {
        return nombre + " (" + codigo + ") - ₡" + precio + " - Stock: " + cantidadInventario;
    }
}