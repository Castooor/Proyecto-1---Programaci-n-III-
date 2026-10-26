package cr.ac.una.est.pos.service;

import cr.ac.una.est.pos.dao.LogDAO;
import cr.ac.una.est.pos.model.LogAccion;
import cr.ac.una.est.pos.model.TipoNegocio;

import java.util.List;

/**
 * Servicio de bitácora. Registrar una acción nunca debe romper ni
 * congelar la aplicación, por eso se guarda en un hilo aparte y los
 * errores solo se muestran en consola.
 */
public class LogService {

    private final LogDAO dao;

    /**
     * Crea el servicio de bitácora.
     *
     * @param dao acceso a datos de la bitácora
     */
    public LogService(LogDAO dao) {
        this.dao = dao;
    }

    /**
     * Registra una acción en la bitácora sin bloquear la interfaz.
     *
     * @param usuario nombre del usuario que realizó la acción
     * @param negocio negocio donde ocurrió (puede ser null)
     * @param accion tipo de acción (LOGIN, LOGOUT, FACTURA...)
     * @param detalle descripción breve
     */
    public void registrar(String usuario, TipoNegocio negocio, String accion, String detalle) {
        final String u = recortar(usuario, 30);
        final String a = recortar(accion, 30);
        final String d = recortar(detalle, 255);
        Thread hilo = new Thread(() -> {
            try {
                dao.registrar(u, negocio, a, d);
            } catch (RuntimeException e) {
                System.err.println("No se pudo registrar en la bitácora: " + e.getMessage());
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Devuelve las acciones más recientes.
     *
     * @param limite cantidad máxima de registros
     * @return lista de acciones, de la más reciente a la más antigua
     */
    public List<LogAccion> listarRecientes(int limite) {
        return dao.listarRecientes(limite);
    }

    /**
     * Corta un texto para que quepa en la columna de la base de datos.
     *
     * @param texto texto original (puede ser null)
     * @param max largo máximo permitido
     * @return el texto recortado, o "" si era null
     */
    private String recortar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}