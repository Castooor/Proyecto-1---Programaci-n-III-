package cr.ac.una.est.pos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que arranca la aplicación JavaFX y carga la
 * pantalla principal del sistema (el contenedor con navegación).
 */
public class Main extends Application {

    /**
     * Punto de entrada de JavaFX. Carga el FXML principal y lo
     * muestra en la ventana.
     *
     * @param stage la ventana principal que provee JavaFX
     * @return no retorna nada, solo configura y muestra la ventana
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/cr/ac/una/est/pos/view/MainView.fxml"));
        Scene scene = new Scene(root);

        stage.setTitle("Sistema POS - Soda o Pulpería");
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
