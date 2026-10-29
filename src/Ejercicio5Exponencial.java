import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * Exponencial: encuentra un evento en una lista ordenada cuyo tamaño crece durante la carga.
 *
 * Estructura: lista ordenada que solo crece al final (los eventos llegan en orden). Como n
 * cambia mientras se busca, no se usa como cota: cada índice se consulta en el momento y uno
 * que aún no se ha cargado cuenta como "mayor que x". Se duplica el límite (1, 2, 4, ...)
 * hasta pasar a x o salir de lo cargado, y se hace binaria en [límite/2, límite].
 * Complejidad: O(log i), con i la posición del evento (no depende del tamaño final).
 * Espacio O(1) de datos y O(log i) de recursión.
 * Para cargas concurrentes la lista debe ser segura entre hilos (p. ej. CopyOnWriteArrayList).
 */
public final class Ejercicio5Exponencial {
    private Ejercicio5Exponencial() { }

    public static OptionalInt buscarEvento(List<Integer> eventos, int evento) {
        final int limite = IntStream.iterate(1, b -> b * 2)
                .filter(b -> !antesDe(eventos, b, evento))
                .findFirst()
                .getAsInt();
        return binaria(eventos, evento, limite / 2, limite);
    }

    private static OptionalInt binaria(List<Integer> eventos, int x, int lo, int hi) {
        final int mid = lo + (hi - lo) / 2;
        return lo > hi                   ? OptionalInt.empty()
             : antesDe(eventos, mid, x)  ? binaria(eventos, x, mid + 1, hi)
             : esEvento(eventos, mid, x) ? OptionalInt.of(mid)
             :                             binaria(eventos, x, lo, mid - 1);
    }

    private static boolean antesDe(List<Integer> eventos, int i, int x) {
        return i < eventos.size() && eventos.get(i) < x;
    }

    private static boolean esEvento(List<Integer> eventos, int i, int x) {
        return i < eventos.size() && eventos.get(i) == x;
    }
}
