import java.util.List;
import java.util.Optional;

/**
 * Lineal: en una cola de tickets, devuelve el primer ticket crítico sin reordenar la lista.
 *
 * Estructura: List en orden de llegada. La cola no está ordenada por criticidad y ordenarla
 * rompería el turno de atención, así que la única opción es recorrerla en orden.
 * Complejidad: O(n) en el peor caso (no hay críticos o el crítico es el último) y O(1) si es
 * el primero. Espacio O(1): findFirst corta en el primer crítico y la lista solo se lee.
 */
public final class Ejercicio1Lineal {
    private Ejercicio1Lineal() { }

    public record Ticket(int id, boolean critico) { }

    public static Optional<Ticket> primerTicketCritico(List<Ticket> cola) {
        return cola.stream().filter(Ticket::critico).findFirst();
    }
}
