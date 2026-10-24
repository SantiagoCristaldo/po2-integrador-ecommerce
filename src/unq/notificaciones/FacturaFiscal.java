package unq.notificaciones;

import java.time.LocalDate;

public class FacturaFiscal {
    private String numero;
    private LocalDate fechaEmision;
    private float total;
    private unq.reserva.Reserva reserva;

    public FacturaFiscal(String numero, LocalDate fechaEmision, float total, unq.reserva.Reserva reserva) {
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.total = total;
        this.reserva = reserva;
    }

    public String getNumero() {
        return numero;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public float getTotal() {
        return total;
    }

    public unq.reserva.Reserva getReserva() {
        return reserva;
    }
}
