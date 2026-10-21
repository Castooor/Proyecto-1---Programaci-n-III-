package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.Cliente;
import cr.ac.una.est.pos.model.ItemOrden;
import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.model.Producto;
import cr.ac.una.est.pos.service.ClienteService;
import cr.ac.una.est.pos.service.OrdenService;
import cr.ac.una.est.pos.service.ProductoService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador base abstracto para las pantallas de toma de órdenes.
 * Contiene la lógica común de visualización del catálogo, manejo del
 * carrito de compras, actualización de subtotales y facturación.
 * Las clases hijas (OrdenSodaController y OrdenPulperiaController)
 * definen los campos específicos y la creación de órdenes según el tipo de negocio.
 */
public abstract class BaseOrdenController {

    @FXML protected ComboBox<Cliente> cbCliente;

    @FXML protected TableView<Producto> tablaCatalogo;
    @FXML protected TableColumn<Producto, String> colCatCodigo;
    @FXML protected TableColumn<Producto, String> colCatNombre;
    @FXML protected TableColumn<Producto, Double> colCatPrecio;
    @FXML protected TableColumn<Producto, Integer> colCatInventario;
    @FXML protected TextField txtCantidad;

    @FXML protected TableView<ItemOrden> tablaCarrito;
    @FXML protected TableColumn<ItemOrden, String> colCarProducto;
    @FXML protected TableColumn<ItemOrden, Integer> colCarCantidad;
    @FXML protected TableColumn<ItemOrden, Double> colCarSubtotal;
    @FXML protected Label lblSubtotal;

    protected ProductoService productoService;
    protected ClienteService clienteService;
    protected OrdenService ordenService;
    protected MainController mainController;

    protected ObservableList<Producto> listaCatalogo;
    protected ObservableList<ItemOrden> listaCarrito;
    protected Orden ordenActual;

    /**
     * Inicializa las tablas del catálogo y del carrito.
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

        inicializarControlesEspecificos();
    }

    /**
     * Permite a las clases hijas inicializar sus propios controles (por ejemplo, listeners de radio buttons).
     */
    protected abstract void inicializarControlesEspecificos();

    /**
     * Recibe los servicios compartidos y llena el combo de clientes y la tabla de catálogo.
     */
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
     * Método que debe implementar cada controlador para iniciar la orden
     * correspondiente a su modelo de negocio.
     */
    @FXML
    public abstract void onIniciarOrden();

    /**
     * Agrega el producto seleccionado al carrito.
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

    /**
     * Remueve el producto seleccionado del carrito.
     */
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
     * Envía la orden a facturación.
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
     * Actualiza el subtotal acumulado.
     */
    protected void actualizarSubtotal() {
        double subtotal = ordenActual == null ? 0 : ordenActual.calcularSubtotal();
        lblSubtotal.setText(String.format("Subtotal: ₡%.2f", subtotal));
    }

    protected void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setTitle("Error");
        alerta.showAndWait();
    }

    protected void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje);
        alerta.setTitle("Información");
        alerta.showAndWait();
    }
}
