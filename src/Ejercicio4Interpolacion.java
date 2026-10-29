import java.util.OptionalInt;

/**
 * Interpolación: busca una lectura en una serie de IDs uniformes y maneja valores repetidos.
 *
 * Estructura: arreglo ordenado de IDs con distribución uniforme, la condición para que la
 * fórmula pos = lo + (x − a[lo])·(hi − lo) / (a[hi] − a[lo]) acierte cerca del objetivo.
 * Repetidos: si a[lo] == a[hi] el tramo es constante y se responde sin dividir por cero; al
 * encontrar x se retrocede hasta su primera ocurrencia para que la respuesta sea única.
 * Complejidad: O(log log n) en promedio con datos uniformes, O(n) en el peor caso (datos
 * sesgados), más O(k) para retroceder sobre k repetidos. Espacio O(1) de datos.
 */
public final class Ejercicio4Interpolacion {
    private Ejercicio4Interpolacion() { }

    public static OptionalInt buscarLectura(int[] ids, int id) {
        return buscar(ids, id, 0, ids.length - 1);
    }

    // shortcut: recursiva; con datos muy sesgados la profundidad llega a O(n), pasar a iterativa si n es grande.
    private static OptionalInt buscar(int[] a, int x, int lo, int hi) {
        return lo > hi || x < a[lo] || x > a[hi] ? OptionalInt.empty()
             : a[lo] == a[hi]                     ? OptionalInt.of(lo)
             : sondear(a, x, lo, hi, lo + (int) (((long) x - a[lo]) * (hi - lo) / ((long) a[hi] - a[lo])));
    }

    private static OptionalInt sondear(int[] a, int x, int lo, int hi, int pos) {
        return a[pos] == x ? OptionalInt.of(primeraOcurrencia(a, x, pos))
             : a[pos] < x  ? buscar(a, x, pos + 1, hi)
             :               buscar(a, x, lo, pos - 1);
    }

    private static int primeraOcurrencia(int[] a, int x, int i) {
        return i > 0 && a[i - 1] == x ? primeraOcurrencia(a, x, i - 1) : i;
    }
}
