import java.util.OptionalInt;

/**
 * Saltos: busca un código en un índice ordenado por bloques y reporta las comparaciones realizadas.
 *
 * Estructura: arreglo ordenado dividido en bloques de tamaño m = ⌊√n⌋. Se compara el último
 * código de cada bloque hasta dar con el que puede contener x, y luego se recorre solo ese bloque.
 * Complejidad: como máximo n/m saltos + m comparaciones dentro del bloque; con m = √n queda
 * O(√n) (≈ 2√n comparaciones). Espacio O(1) de datos; la recursión usa O(√n) de pila.
 */
public final class Ejercicio3Saltos {
    private Ejercicio3Saltos() { }

    public record Resultado(OptionalInt posicion, int comparaciones) { }

    public static Resultado buscarCodigo(int[] indice, int codigo) {
        return saltar(indice, codigo, Math.max(1, (int) Math.sqrt(indice.length)), 0, 0);
    }

    // Fase 1: compara el último código de cada bloque [inicio, fin).
    private static Resultado saltar(int[] a, int x, int paso, int inicio, int comparaciones) {
        final int fin = Math.min(inicio + paso, a.length);
        return inicio >= a.length ? new Resultado(OptionalInt.empty(), comparaciones)
             : a[fin - 1] >= x    ? recorrer(a, x, inicio, fin, comparaciones + 1)
             :                      saltar(a, x, paso, fin, comparaciones + 1);
    }

    // Fase 2: búsqueda lineal dentro del bloque elegido.
    private static Resultado recorrer(int[] a, int x, int i, int fin, int comparaciones) {
        return i == fin  ? new Resultado(OptionalInt.empty(), comparaciones)
             : a[i] == x ? new Resultado(OptionalInt.of(i), comparaciones + 1)
             :             recorrer(a, x, i + 1, fin, comparaciones + 1);
    }
}
