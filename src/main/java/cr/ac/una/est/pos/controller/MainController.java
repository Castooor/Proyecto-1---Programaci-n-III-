package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.model.TipoNegocio;
import cr.ac.una.est.pos.model.Usuario;
import cr.ac.una.est.pos.service.ClienteService;
import cr.ac.una.est.pos.service.OrdenService;
import cr.ac.una.est.pos.service.ProductoService;
import cr.ac.una.est.pos.dao.jdbc.LogDAOJdbc;
import cr.ac.una.est.pos.service.LogService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import java.io.IOException;
import cr.ac.una.est.pos.dao.ProductoDAO;
import cr.ac.una.est.pos.dao.OrdenDAO;
import cr.ac.una.est.pos.dao.jdbc.OrdenDAOJdbc;
import cr.ac.una.est.pos.dao.jdbc.ProductoDAOJdbc;
import cr.ac.una.est.pos.dao.jdbc.ClienteDAOJdbc;

/**
 * Controlador de la pantalla principal (contenedora). Es dueño único
 * de los servicios compartidos (Producto, Cliente, Orden) y se
 * encarga de la navegación: al cargar cada vista, le inyecta el
 * servicio que necesite mediante su setter correspondiente.
 * Adapta la navegación según el tipo de negocio del usuario actual
 * (Soda o Pulpería).
 */
public class MainController {

    private final LogService logService = new LogService(new LogDAOJdbc());

    private static final String CSS_OSCURO = "/css/dark.css";

    @FXML private StackPane panelCentral;
    @FXML private Label lblInfoUsuario;
    @FXML private Button btnNuevaOrden;

    // Un solo DAO por entidad, compartido por ambos negocios
    private final ProductoDAO productoDAO = new ProductoDAOJdbc();
    private final OrdenDAO ordenDAO = new OrdenDAOJdbc();

    @FXML private HBox boxSelectorNegocio;
    @FXML private ComboBox<TipoNegocio> cbNegocio;

    // Clientes: compartidos por ambos negocios
    private final ClienteService clienteService = new ClienteService(new ClienteDAOJdbc());

    // Servicios de la Soda
    private final ProductoService productoServiceSoda = new ProductoService(productoDAO, TipoNegocio.SODA);
    private final OrdenService ordenServiceSoda =
            new OrdenService(productoServiceSoda, clienteService, ordenDAO, TipoNegocio.SODA);

    // Servicios de la Pulpería
    private final ProductoService productoServicePulperia = new ProductoService(productoDAO, TipoNegocio.PULPERIA);
    private final OrdenService ordenServicePulperia =
            new OrdenService(productoServicePulperia, clienteService, ordenDAO, TipoNegocio.PULPERIA);

    /** Negocio con el que se está trabajando ahora (fijo para soda/pulpería, elegible para admin). */
    private TipoNegocio negocioActivo = TipoNegocio.SODA;

    private Orden ordenEnFacturacion;
    private Usuario usuarioActual;

    /**
     * Devuelve el servicio de productos del negocio activo.
     *
     * @return ProductoService de la Pulpería o de la Soda según negocioActivo
     */
    private ProductoService productoServiceActivo() {
        if (negocioActivo == TipoNegocio.PULPERIA) {
            return productoServicePulperia;
        }
        return productoServiceSoda;
    }

    /**
     * Devuelve el servicio de órdenes del negocio activo.
     *
     * @return OrdenService de la Pulpería o de la Soda según negocioActivo
     */
    private OrdenService ordenServiceActivo() {
        if (negocioActivo == TipoNegocio.PULPERIA) {
            return ordenServicePulperia;
        }
        return ordenServiceSoda;
    }

    /**
     * Se ejecuta automáticamente al cargar el FXML.
     * Muestra el catálogo como pantalla inicial.
     */
    @FXML
    public void initialize() {
        cbNegocio.getItems().addAll(TipoNegocio.SODA, TipoNegocio.PULPERIA);
        cbNegocio.setValue(TipoNegocio.SODA);
        cargarVista("/cr/ac/una/est/pos/view/CatalogoView.fxml");
    }

    /**
     * Establece el usuario autenticado y ajusta la interfaz (títulos y botones)
     * al modo Soda o Pulpería correspondiente.
     *
     * @param usuario el usuario que inició sesión
     */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;

        if (usuario.getTipoNegocio() == TipoNegocio.ADMIN) {
            negocioActivo = TipoNegocio.SODA;   // el admin arranca en Soda y puede cambiar
        } else {
            negocioActivo = usuario.getTipoNegocio();
        }

        actualizarInterfazSegunUsuario();
        onMostrarCatalogo();   // recarga el catálogo con los servicios del negocio correcto
    }
    /**
     * Devuelve el usuario que inició sesión.
     *
     * @return el usuario actual (null si aún no hay sesión)
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Ajusta la pantalla según el usuario: muestra el selector de negocio solo
     * al administrador y cambia los textos según el negocio activo.
     */
    private void actualizarInterfazSegunUsuario() {
        if (usuarioActual == null) {
            return;
        }

        boolean esAdmin = usuarioActual.getTipoNegocio() == TipoNegocio.ADMIN;
        boxSelectorNegocio.setVisible(esAdmin);
        boxSelectorNegocio.setManaged(esAdmin);

        if (lblInfoUsuario != null) {
            lblInfoUsuario.setText("Modo " + negocioActivo.getDescripcion()
                    + " (" + usuarioActual.getNombre() + ")");
        }

        if (btnNuevaOrden != null) {
            if (negocioActivo == TipoNegocio.PULPERIA) {
                btnNuevaOrden.setText("Venta Mostrador");
            } else {
                btnNuevaOrden.setText("Nueva Orden (Mesa)");
            }
        }
    }

    /**
     * Muestra la pantalla del catálogo de productos.
     */
    @FXML
    public void onMostrarCatalogo() {
        cargarVista("/cr/ac/una/est/pos/view/CatalogoView.fxml");
    }

    /**
     * Muestra la pantalla de gestión de clientes.
     */
    @FXML
    public void onMostrarClientes() {
        cargarVista("/cr/ac/una/est/pos/view/ClienteView.fxml");
    }

    /**
     * Muestra la pantalla de reportes (ventas del día y bitácora).
     */
    @FXML
    public void onMostrarReportes() {
        cargarVista("/cr/ac/una/est/pos/view/ReporteView.fxml");
    }

    /**
     * Muestra la pantalla de toma de pedidos según el tipo de negocio
     * del usuario logueado: OrdenPulperiaView o OrdenSodaView.
     */
    @FXML
    public void onMostrarOrdenes() {
        if (negocioActivo == TipoNegocio.PULPERIA) {
            cargarVista("/cr/ac/una/est/pos/view/OrdenPulperiaView.fxml");
        } else {
            cargarVista("/cr/ac/una/est/pos/view/OrdenSodaView.fxml");
        }
    }

    /**
     * Recibe la orden que se va a facturar y muestra
     * la pantalla de facturación.
     *
     * @param orden la orden a facturar
     */
    public void mostrarFacturacion(Orden orden) {
        this.ordenEnFacturacion = orden;
        cargarVista("/cr/ac/una/est/pos/view/FacturaView.fxml");
    }

    /**
     * Alterna entre el diseño claro original y el modo oscuro.
     */
    @FXML
    private void onCambiarModo() {
        Scene escena = panelCentral.getScene();
        String oscuro = getClass().getResource(CSS_OSCURO).toExternalForm();

        if (escena.getStylesheets().contains(oscuro)) {
            escena.getStylesheets().remove(oscuro);
        } else {
            escena.getStylesheets().add(oscuro);
        }
    }

    /**
     * Cierra la sesión actual y regresa a la pantalla de login.
     */
    @FXML
    private void onCerrarSesion() {
        try {
            if (usuarioActual != null) {
                logService.registrar(usuarioActual.getNombre(), negocioActivo, "LOGOUT", "Cierre de sesión");
            }
            Parent login = FXMLLoader.load(
                    getClass().getResource("/cr/ac/una/est/pos/view/LoginView.fxml")
            );

            Stage stage = (Stage) panelCentral.getScene().getWindow();
            stage.setTitle("Sistema POS - Soda o Pulpería");
            stage.setScene(new Scene(login, 750, 500));

        } catch (IOException e) {
            System.err.println("No se pudo volver a la pantalla de login.");
            e.printStackTrace();
        }
    }

    /**
     * Carga una vista FXML en el panel central y le entrega los servicios que
     * necesita su controlador.
     *
     * @param rutaFxml ruta de la vista dentro de los recursos del proyecto
     */
    private void cargarVista(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(rutaFxml)
            );

            Parent vista = loader.load();
            Object controlador = loader.getController();

            if (controlador instanceof CatalogoController) {
                CatalogoController catalogoController = (CatalogoController) controlador;
                catalogoController.setProductoService(productoServiceActivo());

            } else if (controlador instanceof ClienteController) {
                ClienteController clienteController = (ClienteController) controlador;
                clienteController.setClienteService(clienteService);

            } else if (controlador instanceof BaseOrdenController) {
                BaseOrdenController ordenController = (BaseOrdenController) controlador;
                ordenController.setServicios(
                        productoServiceActivo(),
                        clienteService,
                        ordenServiceActivo(),
                        this
                );

            } else if (controlador instanceof FacturaController) {
                FacturaController facturaController = (FacturaController) controlador;
                facturaController.setOrden(
                        ordenEnFacturacion,
                        productoServiceActivo(),
                        ordenServiceActivo()
                );
                facturaController.setContextoLog(logService,
                        usuarioActual != null ? usuarioActual.getNombre() : "desconocido");

            } else if (controlador instanceof ReporteController) {
                ((ReporteController) controlador).setServicios(
                    ordenServiceActivo(),
                    logService,
                    usuarioActual != null && usuarioActual.getTipoNegocio() == TipoNegocio.ADMIN);
        }

            panelCentral.getChildren().setAll(vista);

        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + rutaFxml);
            e.printStackTrace();
        }
    }
    /**
     * Se ejecuta cuando el administrador elige Soda o Pulpería en el selector.
     * Cambia el negocio activo, ajusta los textos y vuelve al catálogo de ese negocio.
     */
    @FXML
    private void onCambiarNegocio() {
        TipoNegocio elegido = cbNegocio.getValue();
        if (elegido == null || elegido == negocioActivo) {
            return;
        }
        negocioActivo = elegido;
        actualizarInterfazSegunUsuario();
        onMostrarCatalogo();
    }
}
