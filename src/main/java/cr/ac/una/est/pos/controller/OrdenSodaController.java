package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * Controlador de toma de pedidos para la SODA.
 * Gestiona órdenes para comer en el local (con número de mesa)
 * y órdenes Express (con dirección de entrega y cargo por envío).
 */
public class OrdenSodaController extends BaseOrdenController {

    @FXML private RadioButton rbLocal;
    @FXML private RadioButton rbExpress;
    @FXML private Label lblMesa;
    @FXML private TextField txtMesa;
    @FXML private Label lblDireccion;
    @FXML private TextField txtDireccion;

    /**
     * Muestra u oculta los campos de mesa y dirección según el tipo de orden elegido.
     */
    @Override
    protected void inicializarControlesEspecificos() {
        if (rbLocal != null) {
            rbLocal.selectedProperty().addListener((obs, antes, esLocal) -> actualizarCamposSegunTipo(esLocal));
        }
    }

    /**
     * Muestra el campo de mesa si la orden es Local, o el de dirección si es Express.
     *
     * @param esLocal true si la orden es para consumir en una mesa
     */
    private void actualizarCamposSegunTipo(boolean esLocal) {
        if (lblMesa != null) {
            lblMesa.setVisible(esLocal);
            lblMesa.setManaged(esLocal);
        }
        if (txtMesa != null) {
            txtMesa.setVisible(esLocal);
            txtMesa.setManaged(esLocal);
        }
        if (lblDireccion != null) {
            lblDireccion.setVisible(!esLocal);
            lblDireccion.setManaged(!esLocal);
        }
        if (txtDireccion != null) {
            txtDireccion.setVisible(!esLocal);
            txtDireccion.setManaged(!esLocal);
        }
    }

    /**
     * Valida los datos y crea una orden Local (con mesa) o Express (con dirección).
     * Muestra un error si falta algún dato o el número de mesa no es válido.
     */
    @Override
    @FXML
    public void onIniciarOrden() {
        try {
            Cliente cliente = cbCliente.getValue();
            if (cliente == null) {
                throw new IllegalArgumentException("Debe seleccionar un cliente.");
            }

            if (rbLocal.isSelected()) {
                if (txtMesa.getText().isBlank()) {
                    throw new IllegalArgumentException("Debe ingresar un número de mesa.");
                }
                int mesa = Integer.parseInt(txtMesa.getText().trim());
                if (mesa <= 0) {
                    throw new IllegalArgumentException("El número de mesa debe ser mayor a cero.");
                }
                ordenActual = ordenService.crearOrdenLocal(cliente, mesa);
            } else {
                if (txtDireccion.getText().isBlank()) {
                    throw new IllegalArgumentException("Debe ingresar una dirección para la orden Express.");
                }
                ordenActual = ordenService.crearOrdenExpress(cliente, txtDireccion.getText().trim());
            }

            listaCarrito.clear();
            actualizarSubtotal();
            mostrarInfo("Orden iniciada (Soda). Ya puede agregar productos al carrito.");
        } catch (NumberFormatException e) {
            mostrarError("El número de mesa debe ser un número entero válido.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }
}