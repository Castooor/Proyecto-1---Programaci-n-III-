package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.Usuario;
import cr.ac.una.est.pos.service.UsuarioService;
import cr.ac.una.est.pos.dao.DAOException;
import cr.ac.una.est.pos.dao.jdbc.UsuarioDAOJdbc;
import cr.ac.una.est.pos.dao.jdbc.LogDAOJdbc;
import cr.ac.una.est.pos.service.LogService;
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
    private LogService logService;

    /**
     * Se ejecuta al cargar el FXML: crea los servicios de usuarios y bitácora
     * y limpia el mensaje de error.
     */
    @FXML
    public void initialize() {
        usuarioService = new UsuarioService(new UsuarioDAOJdbc());
        logService = new LogService(new LogDAOJdbc());
        lblMensaje.setText("");
    }

    /**
     * Valida los campos, autentica al usuario contra MySQL y, si es correcto,
     * abre la pantalla principal. Registra el intento en la bitácora.
     */
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
            logService.registrar(usuarioAutenticado.getNombre(),
                    usuarioAutenticado.getTipoNegocio(), "LOGIN", "Inicio de sesión");
            irAPantallaPrincipal(usuarioAutenticado);
        } else {
            logService.registrar(usuario, null, "LOGIN_FALLIDO", "Credenciales incorrectas");
            mostrarError("Usuario o contraseña incorrectos.");
            txtContrasena.clear();
        }
    }

    /**
     * Carga la pantalla principal y le entrega el usuario autenticado.
     *
     * @param usuario usuario que acaba de iniciar sesión
     */
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
            Scene escena = new Scene(root, 750, 500);
            escena.getStylesheets().add(getClass().getResource("/css/light.css").toExternalForm());
            stage.setScene(escena);

        } catch (IOException e) {
            mostrarError("No se pudo cargar la pantalla principal.");
            e.printStackTrace();
        }
    }

    /**
     * Muestra un mensaje de error debajo de los campos de login.
     *
     * @param mensaje texto del error
     */
    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
    }
}