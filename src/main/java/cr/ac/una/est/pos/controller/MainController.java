package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.service.ClienteService;
import cr.ac.una.est.pos.service.OrdenService;
import cr.ac.una.est.pos.service.ProductoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * Controlador de la pantalla principal (contenedora). Es dueño único
 * de los servicios compartidos (Producto, Cliente, Orden) y se
 * encarga de la navegación: al cargar cada vista, le inyecta el
 * servicio que necesite mediante su setter correspondiente. También
 * controla el cambio entre modo oscuro y modo claro de toda la
 * interfaz.
 */
public class MainController {

    private static final String CSS_OSCURO = "/css/dark.css";
    private static final String CSS_CLARO = "/css/light.css";

    @FXML
    private StackPane panelCentral;

    private final ProductoService productoService = new ProductoService();
    private final ClienteService clienteService = new ClienteService();
    private final OrdenService ordenService = new OrdenService(productoService);

    private Orden ordenEnFacturacion;
    private boolean modoOscuroActivo = false;

    /**
     * Se ejecuta automáticamente al cargar el FXML. Muestra el
     * catálogo como pantalla inicial por defecto.
     *
     * @return no retorna nada
     */
    @FXML
    public void initialize() {
        cargarVista("/cr/ac/una/est/pos/view/CatalogoView.fxml");
    }

    /**
     * Muestra la pantalla del catálogo de productos en el panel central.
     *
     * @return no retorna nada
     */
    @FXML
    public void onMostrarCatalogo() {
        cargarVista("/cr/ac/una/est/pos/view/CatalogoView.fxml");
    }

    /**
     * Muestra la pantalla de gestión de clientes en el panel central.
     *
     * @return no retorna nada
     */
    @FXML
    public void onMostrarClientes() {
        cargarVista("/cr/ac/una/est/pos/view/ClienteView.fxml");
    }

    /**
     * Muestra la pantalla de toma de pedidos (Órdenes) en el panel central.
     *
     * @return no retorna nada
     */
    @FXML
    public void onMostrarOrdenes() {
        cargarVista("/cr/ac/una/est/pos/view/OrdenView.fxml");
    }

    /**
     * Recibe la orden que se va a facturar (enviada desde
     * OrdenController) y navega a la pantalla de Facturación.
     *
     * @param orden la orden a facturar
     * @return no retorna nada
     */
    public void mostrarFacturacion(Orden orden) {
        this.ordenEnFacturacion = orden;
        cargarVista("/cr/ac/una/est/pos/view/FacturaView.fxml");
    }

    /**
     * Alterna toda la interfaz entre modo oscuro y modo claro,
     * cambiando la hoja de estilos CSS aplicada a la ventana completa.
     * Como el CSS se aplica a nivel de Scene (no de cada vista por
     * separado), el cambio afecta cualquier pantalla que se esté
     * mostrando en ese momento.
     *
     * @return no retorna nada
     */
    @FXML
    public void onCambiarModo() {
        Scene escena = panelCentral.getScene();
        escena.getStylesheets().clear();

        String hojaEstilos = modoOscuroActivo ? CSS_CLARO : CSS_OSCURO;
        escena.getStylesheets().add(getClass().getResource(hojaEstilos).toExternalForm());

        modoOscuroActivo = !modoOscuroActivo;
    }

    /**
     * Carga un archivo FXML, le inyecta al controlador resultante el
     * servicio compartido que corresponda según su tipo, y reemplaza
     * el contenido del panel central con la vista cargada.
     *
     * @param rutaFxml la ruta (dentro de resources) del FXML a cargar
     * @return no retorna nada
     */
    private void cargarVista(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent vista = loader.load();
            Object controlador = loader.getController();

            if (controlador instanceof CatalogoController) {
                CatalogoController catalogoController = (CatalogoController) controlador;
                catalogoController.setProductoService(productoService);
            } else if (controlador instanceof ClienteController) {
                ClienteController clienteController = (ClienteController) controlador;
                clienteController.setClienteService(clienteService);
            } else if (controlador instanceof OrdenController) {
                OrdenController ordenController = (OrdenController) controlador;
                ordenController.setServicios(productoService, clienteService, ordenService, this);
            } else if (controlador instanceof FacturaController) {
                FacturaController facturaController = (FacturaController) controlador;
                facturaController.setOrden(ordenEnFacturacion, productoService);
            }

            panelCentral.getChildren().setAll(vista);
        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + rutaFxml);
            e.printStackTrace();
        }
    }
}