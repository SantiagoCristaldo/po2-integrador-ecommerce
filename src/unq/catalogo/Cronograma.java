package unq.catalogo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import unq.excepciones.ExcepcionDominio;

/**
 * Cupos disponibles de un servicio del catálogo para cada fecha.
 *
 * El mapa de cupos guarda sólo las fechas que se apartan del cupo por defecto (porque se definieron a
 * mano o porque ya tienen reservas). Una fecha que no está en el mapa tiene el cupo por defecto.
 * El cupo de cada fecha es el que sigue libre; lo reservado se lleva aparte para los reportes.
 *
 * Los rangos de fechas incluyen tanto la fecha de inicio como la fecha final.
 */
public class Cronograma {
    private final int cupoPorDefecto;
    private final Map<LocalDate, Integer> cuposPorFecha = new HashMap<>();
    private final Map<LocalDate, Integer> reservadosPorFecha = new HashMap<>();

    public Cronograma(int cupoPorDefecto) {
        this.cupoPorDefecto = cupoPorDefecto;
    }

    public void definirCupo(LocalDate fecha, int cupo) {
        if (cupo < 0) {
            throw new ExcepcionDominio("El cupo de una fecha no puede ser negativo");
        }
        cuposPorFecha.put(fecha, cupo);
    }

    public int cupoEn(LocalDate fecha) {
        return cuposPorFecha.getOrDefault(fecha, cupoPorDefecto);
    }

    /** El mayor cupo de todas las fechas: es mayor a cero si hay cupo en al menos una fecha. */
    public int cupoMaximo() {
        return cuposPorFecha.values().stream().reduce(cupoPorDefecto, Math::max);
    }

    public boolean tieneCupo(LocalDate inicio, LocalDate fin, int cantidad) {
        return fechasEntre(inicio, fin).allMatch(fecha -> cupoEn(fecha) >= cantidad);
    }

    /** Descuenta el cupo de todas las fechas del rango, o de ninguna si alguna no alcanza. */
    public void decrementar(LocalDate inicio, LocalDate fin, int cantidad) {
        if (!tieneCupo(inicio, fin, cantidad)) {
            throw new ExcepcionDominio("No hay cupo disponible entre " + inicio + " y " + fin);
        }
        fechasEntre(inicio, fin).forEach(fecha -> moverCupos(fecha, cantidad));
    }

    public void incrementar(LocalDate inicio, LocalDate fin, int cantidad) {
        fechasEntre(inicio, fin).forEach(fecha -> moverCupos(fecha, -cantidad));
    }

    /** Suma, fecha por fecha, los cupos reservados del rango. */
    public int cuposReservadosEntre(LocalDate inicio, LocalDate fin) {
        return fechasEntre(inicio, fin).mapToInt(this::reservadosEn).sum();
    }

    /** Suma, fecha por fecha, los cupos del rango: los que siguen libres más los reservados. */
    public int cuposTotalesEntre(LocalDate inicio, LocalDate fin) {
        return fechasEntre(inicio, fin).mapToInt(fecha -> cupoEn(fecha) + reservadosEn(fecha)).sum();
    }

    private int reservadosEn(LocalDate fecha) {
        return reservadosPorFecha.getOrDefault(fecha, 0);
    }

    private void moverCupos(LocalDate fecha, int reservados) {
        cuposPorFecha.put(fecha, cupoEn(fecha) - reservados);
        reservadosPorFecha.put(fecha, reservadosEn(fecha) + reservados);
    }

    private Stream<LocalDate> fechasEntre(LocalDate inicio, LocalDate fin) {
        if (fin.isBefore(inicio)) {
            throw new ExcepcionDominio("La fecha final no puede ser anterior a la fecha de inicio");
        }
        return inicio.datesUntil(fin.plusDays(1));
    }
}
