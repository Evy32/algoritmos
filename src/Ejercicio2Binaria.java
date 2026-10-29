import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Binaria: lowerBound para insertar precios manteniendo el orden y soportando duplicados.
 *
 * Estructura: arreglo ordenado de precios (acceso por índice en O(1), requisito de la binaria).
 * lowerBound devuelve la primera posición con precio >= x: con duplicados cae en la primera
 * ocurrencia y, si x es mayor que todos, devuelve n (insertar al final).
 * Complejidad: lowerBound O(log n). Insertar es O(n) porque hay que copiar los elementos para
 * abrir el hueco. Espacio: O(n) del nuevo arreglo y O(log n) de la recursión.
 */
public final class Ejercicio2Binaria {
    private Ejercicio2Binaria() { }

    public static int lowerBound(int[] precios, int precio) {
        return lowerBound(precios, precio, 0, precios.length);
    }

    // Rango semiabierto [lo, hi): la posición buscada siempre está dentro de él.
    private static int lowerBound(int[] a, int x, int lo, int hi) {
        final int mid = lo + (hi - lo) / 2;
        return lo == hi   ? lo
             : a[mid] < x ? lowerBound(a, x, mid + 1, hi)
             :              lowerBound(a, x, lo, mid);
    }

    public static int[] insertarPrecio(int[] precios, int precio) {
        final int i = lowerBound(precios, precio);
        return IntStream.concat(
                        IntStream.concat(Arrays.stream(precios, 0, i), IntStream.of(precio)),
                        Arrays.stream(precios, i, precios.length))
                .toArray();
    }
}
