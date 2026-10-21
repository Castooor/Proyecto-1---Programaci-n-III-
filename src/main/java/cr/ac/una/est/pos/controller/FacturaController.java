package cr.ac.una.est.pos.controller;

import cr.ac.una.est.pos.model.ItemOrden;
import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.model.Pago;
import cr.ac.una.est.pos.service.FacturaService;
import cr.ac.una.est.pos.service.OrdenService;
import cr.ac.una.est.pos.service.ProductoService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Controlador de la pantalla de Facturación. Muestra el resumen de
 * la orden con subtotal, IVA y total, permite elegir el método de
 * pago, y calcula el vuelto si corresponde.
 */
public class FacturaController {

    @FXML private Label lblResumenOrden;
    @FXML private Label lblSubtotal;
    @FXML private Label lblEnvio;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;

    @FXML private RadioButton rbEfectivo;
    @FXML private RadioButton rbTarjeta;
    @FXML private RadioButton rbSinpe;
    @FXML private HBox boxEfectivo;
    @FXML private TextField txtMontoRecibido;

    @FXML private Label lblResultado;
    private ProductoService productoService;
    private OrdenService ordenService;
    private boolean yaFacturado = false;
    private FacturaService facturaService;
    private Orden orden;

    /**
     * Se ejecuta automáticamente al cargar el FXML. Prepara el
     * servicio de facturación y el listener que muestra/oculta el
     * campo de monto recibido según el método de pago elegido.
     *
     * @return no retorna nada
     */
    @FXML
    public void initialize() {
        facturaService = new FacturaService();
        rbEfectivo.selectedProperty().addListener((obs, antes, esEfectivo) -> {
            boxEfectivo.setVisible(esEfectivo);
            boxEfectivo.setManaged(esEfectivo);
        });
    }

    /**
     * Recibe la orden a facturar (enviada desde la pantalla de
     * Órdenes a través de MainController) y muestra su resumen,
     * subtotal, IVA y total.
     *
     * @param orden la orden a facturar
     * @param productoService el servicio de productos para actualizar stock
     * @param ordenService el servicio de órdenes para registrar el pedido facturado
     * @return no retorna nada
     */
    public void setOrden(Orden orden, ProductoService productoService, OrdenService ordenService) {
        this.orden = orden;
        this.productoService = productoService;
        this.ordenService = ordenService;
        lblResumenOrden.setText(orden.generarResumen());
        lblSubtotal.setText(String.format("₡%.2f", orden.calcularSubtotal()));
        lblEnvio.setText(String.format("₡%.2f", orden.calcularCostoAdicional()));
        lblIva.setText(String.format("₡%.2f", facturaService.calcularIva(orden)));
        lblTotal.setText(String.format("₡%.2f", facturaService.calcularTotalFinal(orden)));
    }

    /**
     * Construye el Pago según el método elegido, valida los datos
     * necesarios, y muestra el resumen final (incluyendo el vuelto
     * si el pago es en efectivo).
     *
     * @return no retorna nada
     */
    @FXML
    public void onConfirmarPago() {
        try {
            if (yaFacturado) {
                throw new IllegalArgumentException("Esta orden ya fue facturada.");
            }
            if (orden.getItems().isEmpty()) {
                throw new IllegalArgumentException("No se puede facturar una orden sin productos.");
            }

            Pago pago;
            if (rbEfectivo.isSelected()) {
                double montoRecibido = Double.parseDouble(txtMontoRecibido.getText());
                pago = facturaService.crearPagoEfectivo(orden, montoRecibido);
            } else if (rbTarjeta.isSelected()) {
                pago = facturaService.crearPagoTarjeta(orden);
            } else {
                pago = facturaService.crearPagoSinpe(orden);
            }

            for (ItemOrden item : orden.getItems()) {
                productoService.descontarInventario(item.getProducto(), item.getCantidad());
            }
            ordenService.finalizarPedido(orden);
            yaFacturado = true;

            lblResultado.setText("¡Factura completada! Inventario actualizado.\n" + pago.generarResumen());
        } catch (NumberFormatException e) {
            mostrarError("El monto recibido debe ser un número válido.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Muestra una alerta de error al usuario.
     *
     * @param mensaje el mensaje a mostrar
     * @return no retorna nada
     */
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setTitle("Error");
        alerta.showAndWait();
    }
}
