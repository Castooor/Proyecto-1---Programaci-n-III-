package cr.ac.una.est.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * Controlador de la pantalla principal (contenedora). Se encarga
 * únicamente de la navegación: mostrar la vista de Catálogo o de
 * Clientes dentro del panel central, según el botón que el usuario
 * presione. No conoce nada sobre productos ni clientes.
 */
public class MainController {

    @FXML
    private StackPane panelCentral;

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
     * Carga un archivo FXML y reemplaza el contenido del panel
     * central con la vista resultante.
     *
     * @param rutaFxml la ruta (dentro de resources) del FXML a cargar
     * @return no retorna nada
     */
    private void cargarVista(String rutaFxml) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(rutaFxml));
            panelCentral.getChildren().setAll(vista);
        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + rutaFxml);
            e.printStackTrace();
        }
    }
}
