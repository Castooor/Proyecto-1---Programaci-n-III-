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
 * servicio que necesite mediante su setter correspondiente.
 * También controla el cambio entre modo oscuro y modo claro.
 */
public class MainController {

    private static final String CSS_OSCURO =
            "/css/dark.css";

    @FXML
    private StackPane panelCentral;

    private final ProductoService productoService = new ProductoService();
    private final ClienteService clienteService = new ClienteService();
    private final OrdenService ordenService =
            new OrdenService(productoService);

    private Orden ordenEnFacturacion;

    /**
     * Se ejecuta automáticamente al cargar el FXML.
     * Muestra el catálogo como pantalla inicial.
     */
    @FXML
    public void initialize() {
        cargarVista("/cr/ac/una/est/pos/view/CatalogoView.fxml");
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
     * Muestra la pantalla de toma de pedidos.
     */
    @FXML
    public void onMostrarOrdenes() {
        cargarVista("/cr/ac/una/est/pos/view/OrdenView.fxml");
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
     *
     * El modo claro no utiliza un CSS adicional. Al quitar
     * dark.css, JavaFX vuelve a utilizar el diseño original
     * definido por los controles y los FXML.
     */
    @FXML
    private void onCambiarModo() {

        Scene escena = panelCentral.getScene();

        String oscuro = getClass()
                .getResource(CSS_OSCURO)
                .toExternalForm();

        if (escena.getStylesheets().contains(oscuro)) {

            // Quitar modo oscuro
            escena.getStylesheets().remove(oscuro);

        } else {

            // Activar modo oscuro
            escena.getStylesheets().add(oscuro);
        }
    }

    /**
     * Carga un archivo FXML, obtiene su controlador y le
     * proporciona los servicios que necesita.
     *
     * @param rutaFxml ruta del archivo FXML
     */
    private void cargarVista(String rutaFxml) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(rutaFxml)
            );

            Parent vista = loader.load();

            Object controlador = loader.getController();

            if (controlador instanceof CatalogoController) {

                CatalogoController catalogoController =
                        (CatalogoController) controlador;

                catalogoController.setProductoService(productoService);

            } else if (controlador instanceof ClienteController) {

                ClienteController clienteController =
                        (ClienteController) controlador;

                clienteController.setClienteService(clienteService);

            } else if (controlador instanceof OrdenController) {

                OrdenController ordenController =
                        (OrdenController) controlador;

                ordenController.setServicios(
                        productoService,
                        clienteService,
                        ordenService,
                        this
                );

            } else if (controlador instanceof FacturaController) {

                FacturaController facturaController =
                        (FacturaController) controlador;

                facturaController.setOrden(
                        ordenEnFacturacion,
                        productoService
                );
            }

            panelCentral.getChildren().setAll(vista);

        } catch (IOException e) {

            System.err.println(
                    "No se pudo cargar la vista: " + rutaFxml
            );

            e.printStackTrace();
        }
    }
}