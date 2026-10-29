import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Imprime el tiempo promedio de cada búsqueda en sus casos límite de uso.
 * Cada caso se calienta antes de medir para que el JIT no distorsione el resultado;
 * aun así los tiempos son orientativos y cambian entre máquinas y ejecuciones.
 */
public final class Tiempos {
    private Tiempos() { }

    private static final int N = 1_000_000;
    private static final long CALENTAMIENTO_MS = 100;
    private static final int REPETICIONES = 200;

    // Pares 0, 2, 4, ...: uniformes, y cualquier impar es un valor ausente dentro del rango.
    private static final int[] ORDENADO = IntStream.range(0, N).map(i -> i * 2).toArray();
    private static final int ULTIMO = ORDENADO[N - 1];

    public static void main(String[] args) {
        System.out.printf("Tiempo promedio por búsqueda (n = %,d, %d repeticiones tras %d ms de calentamiento)%n",
                N, REPETICIONES, CALENTAMIENTO_MS);
        lineal();
        binaria();
        saltos();
        interpolacion();
        exponencial();
    }

    private static void lineal() {
        final List<Ejercicio1Lineal.Ticket> alInicio = tickets(0);
        final List<Ejercicio1Lineal.Ticket> alFinal = tickets(N - 1);
        final List<Ejercicio1Lineal.Ticket> sinCriticos = tickets(-1);
        seccion("1. Lineal: primer ticket crítico", List.of(
                caso("crítico al inicio (mejor caso)", () -> Ejercicio1Lineal.primerTicketCritico(alInicio)),
                caso("crítico al final (peor caso)", () -> Ejercicio1Lineal.primerTicketCritico(alFinal)),
                caso("sin críticos (recorre todo)", () -> Ejercicio1Lineal.primerTicketCritico(sinCriticos)),
                caso("cola vacía", () -> Ejercicio1Lineal.primerTicketCritico(List.of()))));
    }

    private static void binaria() {
        final int[] iguales = IntStream.generate(() -> 500).limit(N).toArray();
        seccion("2. Binaria: lowerBound e inserción de precios", List.of(
                caso("lowerBound menor que todos", () -> Ejercicio2Binaria.lowerBound(ORDENADO, -1)),
                caso("lowerBound mayor que todos", () -> Ejercicio2Binaria.lowerBound(ORDENADO, ULTIMO + 1)),
                caso("lowerBound todos duplicados", () -> Ejercicio2Binaria.lowerBound(iguales, 500)),
                caso("lowerBound arreglo vacío", () -> Ejercicio2Binaria.lowerBound(new int[0], 500)),
                caso("insertar al inicio (copia O(n))", () -> "longitud " + Ejercicio2Binaria.insertarPrecio(ORDENADO, -1).length),
                caso("insertar al final (copia O(n))", () -> "longitud " + Ejercicio2Binaria.insertarPrecio(ORDENADO, ULTIMO + 1).length)));
    }

    private static void saltos() {
        seccion("3. Saltos: código en índice por bloques", List.of(
                caso("primer código (mejor caso)", () -> Ejercicio3Saltos.buscarCodigo(ORDENADO, 0)),
                caso("último código (peor caso)", () -> Ejercicio3Saltos.buscarCodigo(ORDENADO, ULTIMO)),
                caso("ausente en medio", () -> Ejercicio3Saltos.buscarCodigo(ORDENADO, N + 1)),
                caso("mayor que todos", () -> Ejercicio3Saltos.buscarCodigo(ORDENADO, ULTIMO + 1)),
                caso("índice vacío", () -> Ejercicio3Saltos.buscarCodigo(new int[0], 0))));
    }

    private static void interpolacion() {
        final int[] bloquesRepetidos = IntStream.range(0, N).map(i -> i / 1000 * 1000).toArray();
        // Un valor atípico al final hace que cada sondeo avance una sola posición.
        final int[] sesgado = IntStream.range(0, 5_000).map(i -> i == 4_999 ? 1_000_000_000 : i).toArray();
        seccion("4. Interpolación: lectura en IDs uniformes", List.of(
                caso("uniforme, en medio", () -> Ejercicio4Interpolacion.buscarLectura(ORDENADO, N)),
                caso("uniforme, último", () -> Ejercicio4Interpolacion.buscarLectura(ORDENADO, ULTIMO)),
                caso("ausente dentro del rango", () -> Ejercicio4Interpolacion.buscarLectura(ORDENADO, N + 1)),
                caso("fuera de rango", () -> Ejercicio4Interpolacion.buscarLectura(ORDENADO, -1)),
                caso("repetidos (bloques de 1.000)", () -> Ejercicio4Interpolacion.buscarLectura(bloquesRepetidos, 500_000)),
                caso("sesgado, n = 5.000 (peor caso O(n))", () -> Ejercicio4Interpolacion.buscarLectura(sesgado, 4_998))));
    }

    private static void exponencial() {
        final List<Integer> eventos = IntStream.of(ORDENADO).boxed().toList();
        seccion("5. Exponencial: evento en lista que crece", List.of(
                caso("evento cerca del inicio (posición 10)", () -> Ejercicio5Exponencial.buscarEvento(eventos, 20)),
                caso("evento al final", () -> Ejercicio5Exponencial.buscarEvento(eventos, ULTIMO)),
                caso("ausente", () -> Ejercicio5Exponencial.buscarEvento(eventos, N + 1)),
                caso("mayor que lo cargado", () -> Ejercicio5Exponencial.buscarEvento(eventos, ULTIMO + 1)),
                caso("carga parcial (mitad de la lista)", () -> Ejercicio5Exponencial.buscarEvento(eventos.subList(0, N / 2), ULTIMO)),
                caso("lista vacía", () -> Ejercicio5Exponencial.buscarEvento(List.of(), 0))));
    }

    private static void seccion(String titulo, List<Map.Entry<String, Supplier<Object>>> casos) {
        System.out.println(System.lineSeparator() + titulo);
        casos.forEach(c -> medir(c.getKey(), c.getValue()));
    }

    private static void medir(String caso, Supplier<Object> busqueda) {
        // Calentar por tiempo y no por número de llamadas: los casos baratos necesitan muchas más para que el JIT los compile.
        final long finCalentamiento = System.nanoTime() + CALENTAMIENTO_MS * 1_000_000;
        Stream.generate(busqueda).takeWhile(r -> System.nanoTime() < finCalentamiento).count();
        final long inicio = System.nanoTime();
        final Object resultado = IntStream.range(0, REPETICIONES).mapToObj(i -> busqueda.get()).reduce((a, b) -> b).orElseThrow();
        final long promedio = (System.nanoTime() - inicio) / REPETICIONES;
        System.out.printf("  %-42s %,12d ns   %s%n", caso, promedio, resultado);
    }

    private static Map.Entry<String, Supplier<Object>> caso(String nombre, Supplier<Object> busqueda) {
        return Map.entry(nombre, busqueda);
    }

    // Cola de N tickets donde solo el de la posición indicada es crítico (-1: ninguno).
    private static List<Ejercicio1Lineal.Ticket> tickets(int critico) {
        return IntStream.range(0, N).mapToObj(i -> new Ejercicio1Lineal.Ticket(i, i == critico)).toList();
    }
}
