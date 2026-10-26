package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * Controlador de toma de pedidos para la PULPERÍA / ABASTECEDORA.
 * Gestiona ventas directas en mostrador / caja (sin requerir número de mesa)
 * y órdenes Express para envíos a domicilio.
 */
public class OrdenPulperiaController extends BaseOrdenController {

    @FXML private RadioButton rbMostrador;
    @FXML private RadioButton rbExpress;
    @FXML private Label lblDireccion;
    @FXML private TextField txtDireccion;

    /**
     * Muestra el campo de dirección solo cuando la orden es Express.
     */
    @Override
    protected void inicializarControlesEspecificos() {
        if (rbExpress != null) {
            rbExpress.selectedProperty().addListener((obs, antes, esExpress) -> {
                if (lblDireccion != null) {
                    lblDireccion.setVisible(esExpress);
                    lblDireccion.setManaged(esExpress);
                }
                if (txtDireccion != null) {
                    txtDireccion.setVisible(esExpress);
                    txtDireccion.setManaged(esExpress);
                }
            });
        }
    }

    /**
     * Valida los datos y crea una orden de Mostrador o Express (con dirección).
     * Muestra un error si falta algún dato.
     */
    @Override
    @FXML
    public void onIniciarOrden() {
        try {
            Cliente cliente = cbCliente.getValue();
            if (cliente == null) {
                throw new IllegalArgumentException("Debe seleccionar un cliente.");
            }

            if (rbMostrador != null && rbMostrador.isSelected()) {
                // Venta directa en mostrador: no pide número de mesa
                ordenActual = ordenService.crearOrdenMostrador(cliente);
                mostrarInfo("Venta iniciada en mostrador. Ya puede agregar productos.");
            } else {
                // Entrega express
                if (txtDireccion.getText().isBlank()) {
                    throw new IllegalArgumentException("Debe ingresar una dirección para el envío express.");
                }
                ordenActual = ordenService.crearOrdenExpress(cliente, txtDireccion.getText().trim());
                mostrarInfo("Orden Express iniciada (Pulpería). Ya puede agregar productos.");
            }

            listaCarrito.clear();
            actualizarSubtotal();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }
}