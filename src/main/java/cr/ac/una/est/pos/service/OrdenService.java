package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.model.Cliente;
import cr.ac.una.est.pos.model.ItemOrden;
import cr.ac.una.est.pos.model.Orden;
import cr.ac.una.est.pos.model.OrdenExpress;
import cr.ac.una.est.pos.model.OrdenLocal;
import cr.ac.una.est.pos.model.Producto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class OrdenService {

    private static final String RUTA_ARCHIVO = "data/pedidos.csv";

    private ProductoService productoService;
    private ClienteService clienteService;
    private ArrayList<Orden> pedidos;
    private int siguienteId = 1;

    public OrdenService(ProductoService productoService, ClienteService clienteService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.pedidos = new ArrayList<>();
        cargarPedidos();

        for (Orden pedido : pedidos) {
            if (pedido.getId() >= siguienteId) {
                siguienteId = pedido.getId() + 1;
            }
        }
    }

    public Orden crearOrdenLocal(Cliente cliente, int numeroMesa) {
        return new OrdenLocal(cliente, numeroMesa);
    }

    public Orden crearOrdenExpress(Cliente cliente, String direccion) {
        return new OrdenExpress(cliente, direccion);
    }

    public void agregarProducto(Orden orden, Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        int cantidadYaEnCarrito = cantidadEnCarrito(orden, producto);
        if (cantidadYaEnCarrito + cantidad > producto.getCantidadInventario()) {
            throw new IllegalArgumentException(
                    "No hay suficiente inventario de " + producto.getNombre()
                            + ". Disponible: " + producto.getCantidadInventario());
        }
        ItemOrden itemExistente = buscarItem(orden, producto);
        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
        } else {
            orden.agregarProducto(producto, cantidad);
        }
    }

    public void quitarProducto(Orden orden, Producto producto) {
        ItemOrden item = buscarItem(orden, producto);
        if (item != null) {
            orden.getItems().remove(item);
        }
    }

    private int cantidadEnCarrito(Orden orden, Producto producto) {
        ItemOrden item = buscarItem(orden, producto);
        return item == null ? 0 : item.getCantidad();
    }

    private ItemOrden buscarItem(Orden orden, Producto producto) {
        for (ItemOrden item : orden.getItems()) {
            if (item.getProducto().getCodigo().equals(producto.getCodigo())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Le asigna un id al pedido, lo agrega al historial en memoria
     * y reescribe pedidos.csv. Se llama cuando se confirma el pago.
     */
    public void finalizarPedido(Orden orden) {
        orden.setId(siguienteId);
        siguienteId = siguienteId + 1;
        pedidos.add(orden);
        guardarPedidos();
    }

    public ArrayList<Orden> listarPedidos() {
        return pedidos;
    }

    /**
     * Formato de cada linea: id;tipo;cedulaCliente;mesaODireccion;items
     * items va codificado como "codigo-cantidad,codigo-cantidad"
     * (aqui si usamos coma porque el punto y coma ya separa las columnas)
     */
    private void guardarPedidos() {
        File archivo = new File(RUTA_ARCHIVO);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        try {
            BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo));

            for (Orden orden : pedidos) {
                String tipo;
                String mesaODireccion;

                if (orden instanceof OrdenLocal) {
                    OrdenLocal local = (OrdenLocal) orden;
                    tipo = "LOCAL";
                    mesaODireccion = String.valueOf(local.getNumeroMesa());
                } else {
                    OrdenExpress express = (OrdenExpress) orden;
                    tipo = "EXPRESS";
                    mesaODireccion = express.getDireccion();
                }

                String items = "";
                for (ItemOrden item : orden.getItems()) {
                    if (!items.isEmpty()) {
                        items = items + ",";
                    }
                    items = items + item.getProducto().getCodigo() + "-" + item.getCantidad();
                }

                String linea = orden.getId() + ";" + tipo + ";" + orden.getCliente().getCedula()
                        + ";" + mesaODireccion + ";" + items;

                escritor.write(linea);
                escritor.newLine();
            }

            escritor.close();

        } catch (IOException e) {
            System.err.println("No se pudo guardar " + RUTA_ARCHIVO);
            e.printStackTrace();
        }
    }

    private void cargarPedidos() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return;
        }

        try {
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            String linea = lector.readLine();

            while (linea != null) {
                if (!linea.isBlank()) {
                    Orden pedido = convertirLineaAPedido(linea);
                    if (pedido != null) {
                        pedidos.add(pedido);
                    }
                }
                linea = lector.readLine();
            }
            lector.close();

        } catch (IOException e) {
            System.err.println("No se pudo leer " + RUTA_ARCHIVO);
            e.printStackTrace();
        }
    }

    private Orden convertirLineaAPedido(String linea) {
        String[] partes = linea.split(";", -1);
        if (partes.length < 5) {
            return null;
        }

        int id = Integer.parseInt(partes[0]);
        String tipo = partes[1];
        String cedulaCliente = partes[2];
        String mesaODireccion = partes[3];
        String itemsTexto = partes[4];

        Cliente cliente = clienteService.buscarPorCedula(cedulaCliente);
        if (cliente == null) {
            cliente = Cliente.clienteGenerico();
        }

        Orden orden;
        if (tipo.equals("LOCAL")) {
            int mesa = Integer.parseInt(mesaODireccion);
            orden = new OrdenLocal(cliente, mesa);
        } else {
            orden = new OrdenExpress(cliente, mesaODireccion);
        }
        orden.setId(id);

        if (!itemsTexto.isBlank()) {
            String[] itemsSeparados = itemsTexto.split(",");
            for (String itemTexto : itemsSeparados) {
                String[] codigoYCantidad = itemTexto.split("-");
                if (codigoYCantidad.length == 2) {
                    Producto producto = productoService.buscarPorCodigo(codigoYCantidad[0]);
                    int cantidad = Integer.parseInt(codigoYCantidad[1]);
                    if (producto != null) {
                        orden.agregarProducto(producto, cantidad);
                    }
                }
            }
        }

        return orden;
    }
}
