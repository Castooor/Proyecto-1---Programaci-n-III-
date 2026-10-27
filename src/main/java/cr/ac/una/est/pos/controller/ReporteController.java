package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.LogAccion;
import cr.ac.una.est.pos.model.VentaDia;
import cr.ac.una.est.pos.service.LogService;
import cr.ac.una.est.pos.service.OrdenService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de la pantalla de Reportes. Tiene dos pestañas: el reporte
 * de ventas del día (para todos los usuarios, del negocio activo) y la
 * bitácora de acciones del sistema (solo el administrador). Las consultas
 * a MySQL corren en un hilo aparte para no congelar la interfaz.
 */
public class ReporteController {

    private static final int LIMITE_BITACORA = 50;

    @FXML private DatePicker dpFecha;
    @FXML private TableView<VentaDia> tablaVentas;
    @FXML private TableColumn<VentaDia, Integer> colId;
    @FXML private TableColumn<VentaDia, String> colHora;
    @FXML private TableColumn<VentaDia, String> colTipo;
    @FXML private TableColumn<VentaDia, String> colCliente;
    @FXML private TableColumn<VentaDia, String> colMetodo;
    @FXML private TableColumn<VentaDia, Double> colMonto;
    @FXML private Label lblResumen;

    @FXML private Tab tabBitacora;
    @FXML private TableView<LogAccion> tablaBitacora;
    @FXML private TableColumn<LogAccion, String> colLogFecha;
    @FXML private TableColumn<LogAccion, String> colLogUsuario;
    @FXML private TableColumn<LogAccion, String> colLogNegocio;
    @FXML private TableColumn<LogAccion, String> colLogAccion;
    @FXML private TableColumn<LogAccion, String> colLogDetalle;

    private final ObservableList<VentaDia> ventas = FXCollections.observableArrayList();
    private final ObservableList<LogAccion> bitacora = FXCollections.observableArrayList();

    private OrdenService ordenService;
    private LogService logService;

    /**
     * Se ejecuta al cargar el FXML: enlaza las columnas con los datos
     * y deja la fecha de hoy seleccionada.
     */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoOrden"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodo"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        tablaVentas.setItems(ventas);

        colLogFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colLogUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colLogNegocio.setCellValueFactory(new PropertyValueFactory<>("negocio"));
        colLogAccion.setCellValueFactory(new PropertyValueFactory<>("accion"));
        colLogDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));
        tablaBitacora.setItems(bitacora);

        dpFecha.setValue(LocalDate.now());
    }

    /**
     * Recibe los servicios que usa la pantalla (los envía MainController)
     * y carga los datos iniciales.
     *
     * @param ordenService servicio de órdenes del negocio activo
     * @param logService servicio de bitácora
     * @param esAdmin true si el usuario es administrador (puede ver la bitácora)
     */
    public void setServicios(OrdenService ordenService, LogService logService, boolean esAdmin) {
        this.ordenService = ordenService;
        this.logService = logService;
        tabBitacora.setDisable(!esAdmin);
        onConsultar();
        if (esAdmin) {
            onActualizarBitacora();
        }
    }

    /**
     * Consulta las ventas de la fecha elegida, en un hilo aparte, y
     * muestra la cantidad de pedidos y el total vendido.
     */
    @FXML
    private void onConsultar() {
        LocalDate fecha = dpFecha.getValue();
        if (fecha == null) {
            mostrarError("Seleccione una fecha.");
            return;
        }
        lblResumen.setText("Consultando...");

        Task<List<VentaDia>> tarea = new Task<>() {
            /**
             * Consulta las ventas del día en el hilo secundario.
             *
             * @return ventas de la fecha elegida
             */
            @Override
            protected List<VentaDia> call() {
                return ordenService.ventasDelDia(fecha);
            }
        };
        tarea.setOnSucceeded(e -> {
            List<VentaDia> resultado = tarea.getValue();
            ventas.setAll(resultado);
            lblResumen.setText(String.format("Pedidos: %d   |   Total vendido: %,.2f",
                    resultado.size(), ordenService.totalVentas(resultado)));
        });
        tarea.setOnFailed(e -> {
            lblResumen.setText("");
            mostrarError("No se pudo generar el reporte: " + tarea.getException().getMessage());
        });
        iniciar(tarea);
    }

    /**
     * Recarga las últimas acciones de la bitácora, en un hilo aparte.
     */
    @FXML
    private void onActualizarBitacora() {
        Task<List<LogAccion>> tarea = new Task<>() {
            /**
             * Consulta la bitácora en el hilo secundario.
             *
             * @return las últimas acciones registradas
             */
            @Override
            protected List<LogAccion> call() {
                return logService.listarRecientes(LIMITE_BITACORA);
            }
        };
        tarea.setOnSucceeded(e -> bitacora.setAll(tarea.getValue()));
        tarea.setOnFailed(e ->
                mostrarError("No se pudo leer la bitácora: " + tarea.getException().getMessage()));
        iniciar(tarea);
    }

    /**
     * Ejecuta una tarea en un hilo secundario.
     *
     * @param tarea tarea a ejecutar
     */
    private void iniciar(Task<?> tarea) {
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Muestra una ventana de error.
     *
     * @param mensaje texto del error
     */
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}