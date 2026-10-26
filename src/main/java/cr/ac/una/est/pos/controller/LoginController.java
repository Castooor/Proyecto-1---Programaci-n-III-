package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.Usuario;
import cr.ac.una.est.pos.service.UsuarioService;
import cr.ac.una.est.pos.dao.DAOException;
import cr.ac.una.est.pos.dao.jdbc.UsuarioDAOJdbc;
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
 * Controlador de la pantalla de inicio de sesión. Valida las credenciales
 * mediante UsuarioService y pasa el usuario autenticado (con su TipoNegocio)
 * a MainController para que cargue la interfaz adecuada (Soda o Pulpería).
 */
public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private Label lblMensaje;

    private UsuarioService usuarioService;

    @FXML
    public void initialize() {
        usuarioService = new UsuarioService(new UsuarioDAOJdbc());
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

        Usuario usuarioAutenticado;
        try {
            usuarioAutenticado = usuarioService.autenticar(usuario, contrasena);
        } catch (DAOException e) {
            mostrarError("No se pudo conectar con la base de datos.");
            return;
        }

        if (usuarioAutenticado != null) {
            irAPantallaPrincipal(usuarioAutenticado);
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
            txtContrasena.clear();
        }
    }

    private void irAPantallaPrincipal(Usuario usuario) {
        try {
            if (getClass().getResource("/cr/ac/una/est/pos/view/MainView.fxml") == null) {
                mostrarError("No se encontró el archivo FXML en la ruta especificada.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/est/pos/view/MainView.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setUsuarioActual(usuario);

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setTitle("Sistema POS - Modo " + usuario.getTipoNegocio().getDescripcion());
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
