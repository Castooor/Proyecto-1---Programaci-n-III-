package cr.ac.una.est.pos.dao;

import cr.ac.una.est.pos.model.LogAccion;
import cr.ac.una.est.pos.model.TipoNegocio;

import java.util.List;

/**
 * Contrato de acceso a datos de la bitácora (tabla log_accion).
 */
public interface LogDAO {

    /**
     * Guarda una acción en la bitácora.
     *
     * @param usuario nombre del usuario que realizó la acción
     * @param negocio negocio donde ocurrió; null o ADMIN si es una acción general
     * @param accion tipo de acción (máx. 30 caracteres)
     * @param detalle descripción breve (máx. 255 caracteres)
     * @throws DAOException si falla la base de datos
     */
    void registrar(String usuario, TipoNegocio negocio, String accion, String detalle) throws DAOException;

    /**
     * Lista las acciones más recientes de la bitácora.
     *
     * @param limite cantidad máxima de registros a traer
     * @return lista ordenada de la más reciente a la más antigua
     * @throws DAOException si falla la base de datos
     */
    List<LogAccion> listarRecientes(int limite) throws DAOException;
}