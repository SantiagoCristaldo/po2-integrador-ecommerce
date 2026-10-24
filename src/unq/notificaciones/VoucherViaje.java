package unq.notificaciones;

import java.time.LocalDate;


import unq.reserva.Reserva;

public class VoucherViaje {
    private String codigo;
    private LocalDate fechaEmision;
    private Reserva reserva;

    public VoucherViaje(String codigo, LocalDate fechaEmision, Reserva reserva) {
        this.codigo = codigo;
        this.fechaEmision = fechaEmision;
        this.reserva = reserva;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public Reserva getReserva() {
        return reserva;
    }
}
