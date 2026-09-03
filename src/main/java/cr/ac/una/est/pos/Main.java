package cr.ac.una.est.pos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Clase principal que arranca la aplicación JavaFX.
 * Por ahora solo muestra una ventana de prueba para confirmar
 * que la configuración de Maven y JavaFX está correcta.
 */
public class Main extends Application {

    /**
     * Punto de entrada de JavaFX. Se ejecuta automáticamente después
     * de llamar a launch() y aquí se arma la ventana principal.
     *
     * @param stage la ventana principal que provee JavaFX
     * @return no retorna nada, solo configura y muestra la ventana
     */
    @Override
    public void start(Stage stage) {
        Label label = new Label("¡JavaFX está funcionando!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("Sistema POS - Prueba inicial");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Punto de entrada del programa (el que ejecuta la JVM al inicio).
     * Delega el arranque a JavaFX mediante launch().
     *
     * @param args argumentos de línea de comandos (no se usan)
     * @return no retorna nada
     */
    public static void main(String[] args) {
        launch(args);
    }
}