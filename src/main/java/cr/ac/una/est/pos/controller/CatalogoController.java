package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.Producto;
import cr.ac.una.est.pos.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la pantalla de Catálogo de Productos. Conecta los
 * eventos de los botones y la tabla con la lógica de negocio que
 * vive en ProductoService.
 */
public class CatalogoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtInventario;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colInventario;

    private ProductoService productoService;
    private ObservableList<Producto> listaObservable;

    /**
     * Se ejecuta automáticamente apenas JavaFX carga el FXML.
     * Aquí conectamos las columnas de la tabla con los atributos
     * de Producto y agregamos el listener que llena el formulario
     * al seleccionar una fila. El servicio se recibe después,
     * mediante setProductoService().
     *
     * @return no retorna nada
     */
    @FXML
    public void initialize() {
        listaObservable = FXCollections.observableArrayList();
        tablaProductos.setItems(listaObservable);

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colInventario.setCellValueFactory(new PropertyValueFactory<>("cantidadInventario"));

        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                cargarFormulario(seleccionado);
            }
        });
    }

    /**
     * Recibe el servicio de productos compartido (creado una sola
     * vez en MainController) y refresca la tabla con sus datos.
     *
     * @param productoService el servicio de productos a usar
     * @return no retorna nada
     */
    public void setProductoService(ProductoService productoService) {
        this.productoService = productoService;
        refrescarTabla();
    }

    /**
     * Lee los campos del formulario, valida los datos y agrega
     * un nuevo producto al catálogo.
     *
     * @return no retorna nada
     */
    @FXML
    public void onAgregar() {
        try {
            Producto producto = leerFormulario();
            productoService.crear(producto);
            refrescarTabla();
            onLimpiar();
        } catch (NumberFormatException e) {
            mostrarError("Precio e inventario deben ser números válidos.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Actualiza el producto seleccionado en la tabla con los
     * valores actuales del formulario.
     *
     * @return no retorna nada
     */
    @FXML
    public void onActualizar() {
        try {
            String codigo = txtCodigo.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int inventario = Integer.parseInt(txtInventario.getText());
            productoService.actualizar(codigo, txtNombre.getText(), txtCategoria.getText(), precio, inventario);
            refrescarTabla();
            onLimpiar();
        } catch (NumberFormatException e) {
            mostrarError("Precio e inventario deben ser números válidos.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Elimina el producto cuyo código está en el formulario.
     *
     * @return no retorna nada
     */
    @FXML
    public void onEliminar() {
        try {
            productoService.eliminar(txtCodigo.getText());
            refrescarTabla();
            onLimpiar();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Limpia todos los campos del formulario.
     *
     * @return no retorna nada
     */
    @FXML
    public void onLimpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtCategoria.clear();
        txtPrecio.clear();
        txtInventario.clear();
        tablaProductos.getSelectionModel().clearSelection();
    }

    /**
     * Construye un objeto Producto a partir de lo escrito en el
     * formulario, validando que ningún campo obligatorio esté vacío.
     *
     * @return el producto armado con los datos del formulario
     */
    private Producto leerFormulario() {
        if (txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()) {
            throw new IllegalArgumentException("Código y nombre son obligatorios.");
        }
        double precio = Double.parseDouble(txtPrecio.getText());
        int inventario = Integer.parseInt(txtInventario.getText());
        return new Producto(txtCodigo.getText(), txtNombre.getText(), txtCategoria.getText(), precio, inventario);
    }

    /**
     * Llena el formulario con los datos de un producto, usado
     * cuando el usuario selecciona una fila de la tabla.
     *
     * @param producto el producto cuyos datos se van a mostrar
     * @return no retorna nada
     */
    private void cargarFormulario(Producto producto) {
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtCategoria.setText(producto.getCategoria());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtInventario.setText(String.valueOf(producto.getCantidadInventario()));
    }

    /**
     * Refresca la tabla para que muestre el estado actual del catálogo.
     *
     * @return no retorna nada
     */
    private void refrescarTabla() {
        listaObservable.setAll(productoService.listarTodos());
    }

    /**
     * Muestra una alerta de error al usuario.
     *
     * @param mensaje el mensaje a mostrar
     * @return no retorna nada
     */
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setTitle("Error");
        alerta.showAndWait();
    }
}
