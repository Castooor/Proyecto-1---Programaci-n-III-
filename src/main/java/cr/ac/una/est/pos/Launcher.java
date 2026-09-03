package cr.ac.una.est.pos;

/**
 * Clase auxiliar que permite ejecutar la aplicación directamente
 * desde el botón "Run" de IntelliJ, sin pasar por Maven.
 *
 * JavaFX exige un "module path" especial cuando la clase con el
 * método main() extiende Application directamente. Al delegar el
 * arranque a través de esta clase puente (que NO extiende
 * Application), evitamos esa restricción y el programa corre
 * usando el classpath normal, sin errores.
 *
 * @param args argumentos de línea de comandos, se reenvían tal cual a Main
 * @return no retorna nada
 */
public class Launcher {

    public static void main(String[] args) {
        Main.main(args);
    }
}