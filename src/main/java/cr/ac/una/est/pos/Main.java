package cr.ac.una.est.pos;

import javafx.scene.image.Image;
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
     * Punto de entrada de JavaFX. Configura un manejador de errores
     * no previstos, carga el FXML principal y lo muestra en la
     * ventana.
     *
     * @param stage la ventana principal que provee JavaFX
     * @return no retorna nada, solo configura y muestra la ventana
     */
    @Override
    public void start(Stage stage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((thread, excepcion) -> {
            System.err.println("Error inesperado: " + excepcion.getMessage());
            excepcion.printStackTrace();
        });

        Parent root = FXMLLoader.load(
                getClass().getResource("/cr/ac/una/est/pos/view/MainView.fxml")
        );

        Scene scene = new Scene(root);

        stage.setTitle("Sistema POS - Soda o Pulpería");

        Image icono = new Image(
                getClass().getResourceAsStream("/images/icono.png")
        );

        stage.getIcons().add(icono);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Punto de entrada del programa.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}