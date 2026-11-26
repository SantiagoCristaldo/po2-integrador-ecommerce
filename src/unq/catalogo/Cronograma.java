package unq.catalogo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import unq.excepciones.ExcepcionDominio;

/**
 * Cupos de un servicio del catálogo por fecha.
 *
 * El servicio tiene el mismo cupo todos los días. El mapa guarda, para cada fecha, cuántos cupos
 * están reservados; una fecha que no está en el mapa no tiene reservas.
 *
 * Los rangos de fechas incluyen tanto la fecha de inicio como la fecha final.
 */
public class Cronograma {
    private final int cupo;
    private final Map<LocalDate, Integer> reservados = new HashMap<>();

    public Cronograma(int cupo) {
        this.cupo = cupo;
    }

    public int getCupo() {
        return cupo;
    }

    public int cupoEn(LocalDate fecha) {
        return cupo - reservadosEn(fecha);
    }

    public boolean tieneCupo(LocalDate inicio, LocalDate fin, int cantidad) {
        return fechasEntre(inicio, fin).allMatch(fecha -> cupoEn(fecha) >= cantidad);
    }

    public void decrementar(LocalDate inicio, LocalDate fin, int cantidad) {
        if (!tieneCupo(inicio, fin, cantidad)) {
            throw new ExcepcionDominio("No hay cupo disponible entre " + inicio + " y " + fin);
        }
        fechasEntre(inicio, fin).forEach(fecha -> reservados.merge(fecha, cantidad, Integer::sum));
    }

    public void incrementar(LocalDate inicio, LocalDate fin, int cantidad) {
        fechasEntre(inicio, fin).forEach(fecha -> reservados.merge(fecha, -cantidad, Integer::sum));
    }

    public int cuposReservadosEntre(LocalDate inicio, LocalDate fin) {
        return fechasEntre(inicio, fin).mapToInt(this::reservadosEn).sum();
    }

    public int cuposTotalesEntre(LocalDate inicio, LocalDate fin) {
        return (int) fechasEntre(inicio, fin).count() * cupo;
    }

    private int reservadosEn(LocalDate fecha) {
        return reservados.getOrDefault(fecha, 0);
    }

    private Stream<LocalDate> fechasEntre(LocalDate inicio, LocalDate fin) {
        if (fin.isBefore(inicio)) {
            throw new ExcepcionDominio("La fecha final no puede ser anterior a la fecha de inicio");
        }
        return inicio.datesUntil(fin.plusDays(1));
    }
}
