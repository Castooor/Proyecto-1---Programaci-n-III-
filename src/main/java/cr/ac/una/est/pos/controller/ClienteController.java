package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.Cliente;
import cr.ac.una.est.pos.service.ClienteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la pantalla de gestión de Clientes. Conecta los
 * eventos de los botones y la tabla con la lógica de negocio que
 * vive en ClienteService.
 */
public class ClienteController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;

    private ClienteService clienteService;
    private ObservableList<Cliente> listaObservable;

    /**
     * Se ejecuta automáticamente apenas JavaFX carga el FXML.
     * Aquí conectamos las columnas de la tabla con los atributos
     * de Cliente y agregamos el listener que llena el formulario
     * al seleccionar una fila. El servicio se recibe después,
     * mediante setClienteService().
     *
     * @return no retorna nada
     */
    @FXML
    public void initialize() {
        listaObservable = FXCollections.observableArrayList();
        tablaClientes.setItems(listaObservable);

        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                cargarFormulario(seleccionado);
            }
        });
    }

    /**
     * Recibe el servicio de clientes compartido (creado una sola
     * vez en MainController) y refresca la tabla con sus datos.
     *
     * @param clienteService el servicio de clientes a usar
     * @return no retorna nada
     */
    public void setClienteService(ClienteService clienteService) {
        this.clienteService = clienteService;
        refrescarTabla();
    }

    /**
     * Lee los campos del formulario, valida los datos y agrega
     * un nuevo cliente.
     *
     * @return no retorna nada
     */
    @FXML
    public void onAgregar() {
        try {
            if (txtCedula.getText().isBlank() || txtNombre.getText().isBlank()) {
                throw new IllegalArgumentException("Cédula y nombre son obligatorios.");
            }
            Cliente cliente = new Cliente(txtCedula.getText(), txtNombre.getText(), txtTelefono.getText());
            clienteService.crear(cliente);
            refrescarTabla();
            onLimpiar();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Actualiza el cliente seleccionado con los valores actuales
     * del formulario.
     *
     * @return no retorna nada
     */
    @FXML
    public void onActualizar() {
        try {
            clienteService.actualizar(txtCedula.getText(), txtNombre.getText(), txtTelefono.getText());
            refrescarTabla();
            onLimpiar();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Elimina el cliente cuya cédula está en el formulario.
     *
     * @return no retorna nada
     */
    @FXML
    public void onEliminar() {
        try {
            clienteService.eliminar(txtCedula.getText());
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
        txtCedula.clear();
        txtNombre.clear();
        txtTelefono.clear();
        tablaClientes.getSelectionModel().clearSelection();
    }

    /**
     * Llena el formulario con los datos de un cliente, usado
     * cuando el usuario selecciona una fila de la tabla.
     *
     * @param cliente el cliente cuyos datos se van a mostrar
     * @return no retorna nada
     */
    private void cargarFormulario(Cliente cliente) {
        txtCedula.setText(cliente.getCedula());
        txtNombre.setText(cliente.getNombre());
        txtTelefono.setText(cliente.getTelefono());
    }

    /**
     * Refresca la tabla para que muestre el estado actual de la lista de clientes.
     *
     * @return no retorna nada
     */
    private void refrescarTabla() {
        listaObservable.setAll(clienteService.listarTodos());
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
