package unq.catalogo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import unq.excepciones.ExcepcionDominio;

/**
 * Cupos disponibles de un servicio del catálogo para cada fecha.
 *
 * Hay un único mapa de cupos, que guarda sólo las fechas que se apartan del cupo por defecto (porque
 * se definieron a mano o porque ya tienen reservas). Una fecha que no está en el mapa tiene el cupo
 * por defecto libre y ninguno reservado. Cada fecha guarda un CupoDiario con sus libres y reservados.
 *
 * Los rangos de fechas incluyen tanto la fecha de inicio como la fecha final.
 */
public class Cronograma {
    private final int cupoPorDefecto;
    private final Map<LocalDate, CupoDiario> cupos;

    public Cronograma(int cupoPorDefecto) {
        this.cupoPorDefecto = cupoPorDefecto;
        this.cupos = new HashMap<>();
    }

    public void definirCupo(LocalDate fecha, int cupo) {
        if (cupo < 0) {
            throw new ExcepcionDominio("El cupo de una fecha no puede ser negativo");
        }
        cupoDiarioEn(fecha).definirLibres(cupo);
    }

    /** Define el mismo cupo para todas las fechas del rango, en vez de ir fecha por fecha. */
    public void definirCupo(LocalDate inicio, LocalDate fin, int cupo) {
        if (cupo < 0) {
            throw new ExcepcionDominio("El cupo de una fecha no puede ser negativo");
        }
        fechasEntre(inicio, fin).forEach(fecha -> definirCupo(fecha, cupo));
    }

    public int cupoEn(LocalDate fecha) {
        CupoDiario cupo = cupos.get(fecha);
        return cupo == null ? cupoPorDefecto : cupo.getLibres();
    }

    /** El mayor cupo de todas las fechas: es mayor a cero si hay cupo en al menos una fecha. */
    public int cupoMaximo() {
        return cupos.values().stream().map(CupoDiario::getLibres).reduce(cupoPorDefecto, Math::max);
    }

    public boolean tieneCupo(LocalDate inicio, LocalDate fin, int cantidad) {
        return fechasEntre(inicio, fin).allMatch(fecha -> cupoEn(fecha) >= cantidad);
    }

    /** Descuenta el cupo de todas las fechas del rango, o de ninguna si alguna no alcanza. */
    public void decrementar(LocalDate inicio, LocalDate fin, int cantidad) {
        if (!tieneCupo(inicio, fin, cantidad)) {
            throw new ExcepcionDominio("No hay cupo disponible entre " + inicio + " y " + fin);
        }
        fechasEntre(inicio, fin).forEach(fecha -> cupoDiarioEn(fecha).reservar(cantidad));
    }

    public void incrementar(LocalDate inicio, LocalDate fin, int cantidad) {
        fechasEntre(inicio, fin).forEach(fecha -> cupoDiarioEn(fecha).reservar(-cantidad));
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
        CupoDiario cupo = cupos.get(fecha);
        return cupo == null ? 0 : cupo.getReservados();
    }

    /** El cupo de la fecha; si todavía no estaba en el mapa, lo crea con el cupo por defecto. */
    private CupoDiario cupoDiarioEn(LocalDate fecha) {
        return cupos.computeIfAbsent(fecha, f -> new CupoDiario(cupoPorDefecto));
    }

    private Stream<LocalDate> fechasEntre(LocalDate inicio, LocalDate fin) {
        if (fin.isBefore(inicio)) {
            throw new ExcepcionDominio("La fecha final no puede ser anterior a la fecha de inicio");
        }
        return inicio.datesUntil(fin.plusDays(1));
    }
}
