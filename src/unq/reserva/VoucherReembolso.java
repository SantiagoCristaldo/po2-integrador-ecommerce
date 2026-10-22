package unq.reserva;

import java.time.LocalDate;

public class VoucherReembolso {
    LocalDate fechaEmision;
    Reserva reservaAsociada;

    public VoucherReembolso(LocalDate fechaEmision, Reserva reserva) {
        this.fechaEmision = fechaEmision;
        this.reservaAsociada = reserva;
    }

    double monto(int montoAReembolsar) {
        return montoAReembolsar;
    }
}
