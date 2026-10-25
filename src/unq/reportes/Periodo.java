package unq.reportes;

import java.time.LocalDate;

import unq.excepciones.ExcepcionDominio;

public class Periodo {
    private LocalDate desde;
    private LocalDate hasta;

    public Periodo(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new ExcepcionDominio("Las fechas no estan bien");
        }
        this.desde = desde;
        this.hasta = hasta;
    }

    public boolean contiene(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }
}