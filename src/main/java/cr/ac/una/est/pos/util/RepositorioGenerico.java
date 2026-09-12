package cr.ac.una.est.pos.util;

import java.util.ArrayList;

/**
 * Repositorio genérico que guarda una lista de elementos de
 * cualquier tipo en memoria. Se usa como base para evitar repetir
 * la misma lógica de "agregar/eliminar/listar" en cada Service
 * (ProductoService, ClienteService), reutilizando una sola
 * implementación mediante parámetros genéricos.
 *
 * @param <T> el tipo de elemento que este repositorio va a guardar
 */
public class RepositorioGenerico<T> {

    private ArrayList<T> elementos;

    /**
     * Crea el repositorio con la lista vacía.
     */
    public RepositorioGenerico() {
        this.elementos = new ArrayList<>();
    }

    /**
     * Agrega un elemento al repositorio.
     *
     * @param elemento el elemento a agregar
     */
    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    /**
     * Elimina un elemento del repositorio.
     *
     * @param elemento el elemento a eliminar
     */
    public void eliminar(T elemento) {
        elementos.remove(elemento);
    }

    /**
     * Obtiene la lista completa de elementos guardados.
     *
     * @return la lista de todos los elementos
     */
    public ArrayList<T> listarTodos() {
        return elementos;
    }
}
