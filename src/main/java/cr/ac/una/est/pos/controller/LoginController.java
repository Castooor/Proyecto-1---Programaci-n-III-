package cr.ac.una.est.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador de la pantalla de inicio de sesión. Valida las credenciales del usuario y, si son correctas, reemplaza la escena
 actual de la ventana por la pantalla principal del sistema.

 Como el proyecto no maneja usuarios en base de datos, las
 credenciales válidas se dejan fijas en esta clase.**/
public class LoginController {

    private static final String USUARIO_VALIDO = "admin";
    private static final String CONTRASENA_VALIDA = "admin123";

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private Label lblMensaje;

    @FXML
    public void initialize() {
        lblMensaje.setText("");
    }

    @FXML
    public void onIngresar() {
        String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText() == null ? "" : txtContrasena.getText();

        if (usuario.isBlank() || contrasena.isBlank()) {
            mostrarError("Usuario y contraseña son obligatorios.");
            return;
        }

        if (usuario.equals(USUARIO_VALIDO) && contrasena.equals(CONTRASENA_VALIDA)) {
            irAPantallaPrincipal();
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
            txtContrasena.clear();
        }
    }

    private void irAPantallaPrincipal() {
        try {
            if (getClass().getResource("/cr/ac/una/est/pos/view/MainView.fxml") == null) {
                mostrarError("No se encontró el archivo FXML en la ruta especificada.");
                return;
            }

            Parent root = FXMLLoader.load(getClass().getResource("/cr/ac/una/est/pos/view/MainView.fxml"));
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root, 750, 500));

        } catch (IOException e) {
            mostrarError("No se pudo cargar la pantalla principal.");
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
    }
}
