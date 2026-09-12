package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.service.ClienteService;
import cr.ac.una.est.pos.service.OrdenService;
import cr.ac.una.est.pos.service.ProductoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * Controlador de la pantalla principal (contenedora). Es dueño único
 * de los servicios compartidos (Producto, Cliente, Orden) y se
 * encarga de la navegación: al cargar cada vista, le inyecta el
 * servicio que necesite mediante su setter correspondiente.
 */
public class MainController {

    @FXML
    private StackPane panelCentral;

    private final ProductoService productoService = new ProductoService();
    private final ClienteService clienteService = new ClienteService();
    private final OrdenService ordenService = new OrdenService(productoService);

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

            if (controlador instanceof CatalogoController catalogoController) {
                catalogoController.setProductoService(productoService);
            } else if (controlador instanceof ClienteController clienteController) {
                clienteController.setClienteService(clienteService);
            } else if (controlador instanceof OrdenController ordenController) {
                ordenController.setServicios(productoService, clienteService, ordenService);
            }

            panelCentral.getChildren().setAll(vista);
        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + rutaFxml);
            e.printStackTrace();
        }
    }
}
