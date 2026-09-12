package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.*;
import cr.ac.una.est.pos.service.ClienteService;
import cr.ac.una.est.pos.service.OrdenService;
import cr.ac.una.est.pos.service.ProductoService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la pantalla de Toma de Pedidos. Permite elegir un
 * cliente, el tipo de orden (local o Express), agregar productos del
 * catálogo al carrito, y ver el subtotal en vivo.
 */
public class OrdenController {

    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private RadioButton rbLocal;
    @FXML private RadioButton rbExpress;
    @FXML private Label lblMesa;
    @FXML private TextField txtMesa;
    @FXML private Label lblDireccion;
    @FXML private TextField txtDireccion;

    @FXML private TableView<Producto> tablaCatalogo;
    @FXML private TableColumn<Producto, String> colCatCodigo;
    @FXML private TableColumn<Producto, String> colCatNombre;
    @FXML private TableColumn<Producto, Double> colCatPrecio;
    @FXML private TableColumn<Producto, Integer> colCatInventario;
    @FXML private TextField txtCantidad;

    @FXML private TableView<ItemOrden> tablaCarrito;
    @FXML private TableColumn<ItemOrden, String> colCarProducto;
    @FXML private TableColumn<ItemOrden, Integer> colCarCantidad;
    @FXML private TableColumn<ItemOrden, Double> colCarSubtotal;
    @FXML private Label lblSubtotal;

    private ProductoService productoService;
    private ClienteService clienteService;
    private OrdenService ordenService;

    private ObservableList<Producto> listaCatalogo;
    private ObservableList<ItemOrden> listaCarrito;
    private Orden ordenActual;

    private MainController mainController;

    /**
     * Se ejecuta automáticamente apenas JavaFX carga el FXML.
     * Prepara las tablas y el listener que muestra/oculta los
     * campos de mesa o dirección según el tipo de orden elegido.
     * Los servicios se reciben después, mediante setServicios().
     *
     * @return no retorna nada
     */
    @FXML
    public void initialize() {
        listaCatalogo = FXCollections.observableArrayList();
        tablaCatalogo.setItems(listaCatalogo);
        colCatCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCatNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCatPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCatInventario.setCellValueFactory(new PropertyValueFactory<>("cantidadInventario"));

        listaCarrito = FXCollections.observableArrayList();
        tablaCarrito.setItems(listaCarrito);
        colCarProducto.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getProducto().getNombre()));
        colCarCantidad.setCellValueFactory(datos ->
                new SimpleIntegerProperty(datos.getValue().getCantidad()).asObject());
        colCarSubtotal.setCellValueFactory(datos ->
                new SimpleDoubleProperty(datos.getValue().calcularSubtotal()).asObject());

        rbLocal.selectedProperty().addListener((obs, antes, esLocal) -> actualizarCamposSegunTipo(esLocal));
    }

    /**
     * Recibe los tres servicios compartidos (creados en MainController)
     * y llena el ComboBox de clientes y la tabla del catálogo.
     *
     @param productoService el servicio de productos a usar
     @param clienteService el servicio de clientes a usar
     @param ordenService el servicio de órdenes a usar
     @param mainController el controlador principal, para navegación
     @return no retorna nada*/
    public void setServicios(ProductoService productoService, ClienteService clienteService,
                             OrdenService ordenService, MainController mainController) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.ordenService = ordenService;
        this.mainController = mainController;

        listaCatalogo.setAll(productoService.listarTodos());

        ObservableList<Cliente> clientes = FXCollections.observableArrayList();
        clientes.add(Cliente.clienteGenerico());
        clientes.addAll(clienteService.listarTodos());
        cbCliente.setItems(clientes);
        cbCliente.getSelectionModel().selectFirst();
    }

    /**
     * Muestra el campo de número de mesa u ocultarlo y mostrar el
     * de dirección, según el tipo de orden seleccionado.
     *
     * @param esLocal true si el tipo elegido es "Comer en el local"
     * @return no retorna nada
     */
    private void actualizarCamposSegunTipo(boolean esLocal) {
        lblMesa.setVisible(esLocal);
        lblMesa.setManaged(esLocal);
        txtMesa.setVisible(esLocal);
        txtMesa.setManaged(esLocal);

        lblDireccion.setVisible(!esLocal);
        lblDireccion.setManaged(!esLocal);
        txtDireccion.setVisible(!esLocal);
        txtDireccion.setManaged(!esLocal);
    }

    /**
     * Crea una nueva orden (Local o Express, según lo elegido) para
     * el cliente seleccionado, con los datos de mesa o dirección
     * que correspondan. Reinicia el carrito.
     *
     * @return no retorna nada
     */
    @FXML
    public void onIniciarOrden() {
        try {
            Cliente cliente = cbCliente.getValue();
            if (cliente == null) {
                throw new IllegalArgumentException("Debe seleccionar un cliente.");
            }

            if (rbLocal.isSelected()) {
                int mesa = Integer.parseInt(txtMesa.getText());
                if (mesa <= 0) {
                    throw new IllegalArgumentException("El número de mesa debe ser mayor a cero.");
                }
                ordenActual = ordenService.crearOrdenLocal(cliente, mesa);
            } else {
                if (txtDireccion.getText().isBlank()) {
                    throw new IllegalArgumentException("Debe ingresar una dirección.");
                }
                ordenActual = ordenService.crearOrdenExpress(cliente, txtDireccion.getText());
            }

            listaCarrito.clear();
            actualizarSubtotal();
            mostrarInfo("Orden iniciada. Ya puede agregar productos al carrito.");
        } catch (NumberFormatException e) {
            mostrarError("El número de mesa debe ser un número válido.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Agrega el producto seleccionado en el catálogo al carrito de
     * la orden actual, con la cantidad indicada.
     *
     * @return no retorna nada
     */
    @FXML
    public void onAgregarAlCarrito() {
        try {
            if (ordenActual == null) {
                throw new IllegalArgumentException("Primero debe iniciar una orden.");
            }
            Producto seleccionado = tablaCatalogo.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                throw new IllegalArgumentException("Debe seleccionar un producto del catálogo.");
            }
            int cantidad = Integer.parseInt(txtCantidad.getText());

            ordenService.agregarProducto(ordenActual, seleccionado, cantidad);
            listaCarrito.setAll(ordenActual.getItems());
            actualizarSubtotal();
            txtCantidad.clear();
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número válido.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }
    @FXML
    public void onQuitarDelCarrito() {
        ItemOrden seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado != null && ordenActual != null) {
            ordenService.quitarProducto(ordenActual, seleccionado.getProducto());
            listaCarrito.setAll(ordenActual.getItems());
            actualizarSubtotal();
        }
    }
    /**
     * Envía la orden actual a la pantalla de Facturación.
     *
     * @return no retorna nada
     */
    @FXML
    public void onFacturar() {
        if (ordenActual == null) {
            mostrarError("Primero debe iniciar una orden.");
            return;
        }
        if (ordenActual.getItems().isEmpty()) {
            mostrarError("El carrito está vacío. Agregue al menos un producto antes de facturar.");
            return;
        }
        mainController.mostrarFacturacion(ordenActual);
    }

    /**
     * Actualiza la etiqueta del subtotal con el valor actual de la orden.
     *
     * @return no retorna nada
     */
    private void actualizarSubtotal() {
        double subtotal = ordenActual == null ? 0 : ordenActual.calcularSubtotal();
        lblSubtotal.setText(String.format("Subtotal: ₡%.2f", subtotal));
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

    /**
     * Muestra una alerta informativa al usuario.
     *
     * @param mensaje el mensaje a mostrar
     * @return no retorna nada
     */
    private void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje);
        alerta.setTitle("Información");
        alerta.showAndWait();
    }
}
