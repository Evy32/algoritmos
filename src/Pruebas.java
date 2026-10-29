import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** Casos límite de los cinco ejercicios. Termina con código 1 si alguno falla. */
public final class Pruebas {
    private Pruebas() { }

    // Arreglo de referencia del README (n = 15).
    private static final int[] A = {3, 8, 12, 17, 23, 31, 38, 45, 52, 60, 67, 74, 81, 89, 96};

    public static void main(String[] args) {
        final List<Map.Entry<String, Boolean>> casos = Stream.of(lineal(), binaria(), saltos(), interpolacion(), exponencial())
                .flatMap(List::stream)
                .toList();
        final List<String> fallas = casos.stream().filter(c -> !c.getValue()).map(Map.Entry::getKey).toList();
        System.out.println(fallas.isEmpty() ? "Todas las pruebas pasaron (" + casos.size() + ")." : "Fallaron: " + fallas);
        System.exit(fallas.isEmpty() ? 0 : 1);
    }

    private static List<Map.Entry<String, Boolean>> lineal() {
        final List<Ejercicio1Lineal.Ticket> cola = List.of(ticket(1, false), ticket(2, true), ticket(3, false), ticket(4, true));
        final List<Ejercicio1Lineal.Ticket> mutable = new ArrayList<>(List.of(ticket(9, false), ticket(5, true), ticket(1, true)));
        final List<Ejercicio1Lineal.Ticket> copia = List.copyOf(mutable);
        return List.of(
                caso("lineal: primer crítico entre varios", Ejercicio1Lineal.primerTicketCritico(cola).equals(Optional.of(ticket(2, true)))),
                caso("lineal: cola vacía", Ejercicio1Lineal.primerTicketCritico(List.of()).isEmpty()),
                caso("lineal: sin críticos", Ejercicio1Lineal.primerTicketCritico(List.of(ticket(1, false))).isEmpty()),
                caso("lineal: crítico al final", Ejercicio1Lineal.primerTicketCritico(List.of(ticket(1, false), ticket(2, true))).equals(Optional.of(ticket(2, true)))),
                caso("lineal: no reordena la cola", Ejercicio1Lineal.primerTicketCritico(mutable).equals(Optional.of(ticket(5, true))) && mutable.equals(copia)));
    }

    private static List<Map.Entry<String, Boolean>> binaria() {
        final int[] precios = {100, 200, 200, 200, 300};
        return List.of(
                caso("binaria: arreglo vacío", Ejercicio2Binaria.lowerBound(new int[0], 50) == 0),
                caso("binaria: menor que todos", Ejercicio2Binaria.lowerBound(precios, 50) == 0),
                caso("binaria: mayor que todos", Ejercicio2Binaria.lowerBound(precios, 999) == 5),
                caso("binaria: duplicados -> primera ocurrencia", Ejercicio2Binaria.lowerBound(precios, 200) == 1),
                caso("binaria: ausente entre valores", Ejercicio2Binaria.lowerBound(precios, 250) == 4),
                caso("binaria: inserta duplicado en orden", Arrays.equals(Ejercicio2Binaria.insertarPrecio(precios, 200), new int[]{100, 200, 200, 200, 200, 300})),
                caso("binaria: inserta al inicio", Arrays.equals(Ejercicio2Binaria.insertarPrecio(precios, 50), new int[]{50, 100, 200, 200, 200, 300})),
                caso("binaria: inserta al final", Arrays.equals(Ejercicio2Binaria.insertarPrecio(precios, 999), new int[]{100, 200, 200, 200, 300, 999})),
                caso("binaria: inserta en vacío", Arrays.equals(Ejercicio2Binaria.insertarPrecio(new int[0], 150), new int[]{150})),
                caso("binaria: no modifica el original", Arrays.equals(precios, new int[]{100, 200, 200, 200, 300})));
    }

    private static List<Map.Entry<String, Boolean>> saltos() {
        return List.of(
                caso("saltos: 67 en 4 saltos + 2 lineales", saltos(A, 67, OptionalInt.of(10), 6)),
                caso("saltos: primer elemento", saltos(A, 3, OptionalInt.of(0), 2)),
                caso("saltos: último elemento", saltos(A, 96, OptionalInt.of(14), 8)),
                caso("saltos: ausente en medio", saltos(A, 50, OptionalInt.empty(), 6)),
                caso("saltos: menor que todos", saltos(A, 1, OptionalInt.empty(), 4)),
                caso("saltos: mayor que todos", saltos(A, 100, OptionalInt.empty(), 5)),
                caso("saltos: índice vacío", saltos(new int[0], 5, OptionalInt.empty(), 0)),
                caso("saltos: último bloque incompleto", saltos(IntStream.rangeClosed(1, 10).toArray(), 10, OptionalInt.of(9), 5)));
    }

    private static List<Map.Entry<String, Boolean>> interpolacion() {
        final int[] repetidos = {10, 20, 20, 20, 20, 30, 40};
        return List.of(
                caso("interpolación: referencia 67", Ejercicio4Interpolacion.buscarLectura(A, 67).equals(OptionalInt.of(10))),
                caso("interpolación: IDs uniformes", Ejercicio4Interpolacion.buscarLectura(IntStream.rangeClosed(1, 10).map(i -> i * 10).toArray(), 70).equals(OptionalInt.of(6))),
                caso("interpolación: repetidos -> primera ocurrencia", Ejercicio4Interpolacion.buscarLectura(repetidos, 20).equals(OptionalInt.of(1))),
                caso("interpolación: repetidos al final", Ejercicio4Interpolacion.buscarLectura(new int[]{1, 2, 3, 9, 9, 9}, 9).equals(OptionalInt.of(3))),
                caso("interpolación: todos iguales (sin dividir por 0)", Ejercicio4Interpolacion.buscarLectura(new int[]{5, 5, 5, 5}, 5).equals(OptionalInt.of(0))),
                caso("interpolación: todos iguales, ausente", Ejercicio4Interpolacion.buscarLectura(new int[]{5, 5, 5, 5}, 6).isEmpty()),
                caso("interpolación: ausente dentro del rango", Ejercicio4Interpolacion.buscarLectura(repetidos, 25).isEmpty()),
                caso("interpolación: fuera de rango", Ejercicio4Interpolacion.buscarLectura(A, 1).isEmpty() && Ejercicio4Interpolacion.buscarLectura(A, 100).isEmpty()),
                caso("interpolación: un solo elemento", Ejercicio4Interpolacion.buscarLectura(new int[]{7}, 7).equals(OptionalInt.of(0))),
                caso("interpolación: serie vacía", Ejercicio4Interpolacion.buscarLectura(new int[0], 7).isEmpty()));
    }

    private static List<Map.Entry<String, Boolean>> exponencial() {
        final List<Integer> completa = Arrays.stream(A).boxed().toList();
        final List<Integer> parcial = completa.subList(0, 8);
        return List.of(
                caso("exponencial: referencia 67", Ejercicio5Exponencial.buscarEvento(completa, 67).equals(OptionalInt.of(10))),
                caso("exponencial: primer evento", Ejercicio5Exponencial.buscarEvento(completa, 3).equals(OptionalInt.of(0))),
                caso("exponencial: último evento", Ejercicio5Exponencial.buscarEvento(completa, 96).equals(OptionalInt.of(14))),
                caso("exponencial: ausente", Ejercicio5Exponencial.buscarEvento(completa, 50).isEmpty()),
                caso("exponencial: mayor que lo cargado", Ejercicio5Exponencial.buscarEvento(completa, 100).isEmpty()),
                caso("exponencial: lista vacía", Ejercicio5Exponencial.buscarEvento(List.of(), 5).isEmpty()),
                caso("exponencial: aún no cargado, luego sí", Ejercicio5Exponencial.buscarEvento(parcial, 67).isEmpty()
                        && Ejercicio5Exponencial.buscarEvento(completa, 67).equals(OptionalInt.of(10))),
                caso("exponencial: lista grande, evento cerca del inicio", Ejercicio5Exponencial.buscarEvento(IntStream.range(0, 1_000_000).map(i -> i * 2).boxed().toList(), 20).equals(OptionalInt.of(10))));
    }

    private static boolean saltos(int[] indice, int codigo, OptionalInt posicion, int comparaciones) {
        return Ejercicio3Saltos.buscarCodigo(indice, codigo).equals(new Ejercicio3Saltos.Resultado(posicion, comparaciones));
    }

    private static Ejercicio1Lineal.Ticket ticket(int id, boolean critico) {
        return new Ejercicio1Lineal.Ticket(id, critico);
    }

    private static Map.Entry<String, Boolean> caso(String nombre, boolean ok) {
        return Map.entry(nombre, ok);
    }
}
