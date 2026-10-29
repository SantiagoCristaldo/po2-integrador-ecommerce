package unq.reserva;

import java.time.LocalDate;

public class VoucherReembolso {
    LocalDate fechaEmision;
    Reserva reservaAsociada;
    double montoAReembolsar;

    public VoucherReembolso(LocalDate fechaEmision, Reserva reserva, double montoAReembolsar) {
        this.fechaEmision = fechaEmision;
        this.montoAReembolsar = montoAReembolsar;
        this.reservaAsociada = reserva;
    }

    public double monto() {
        return montoAReembolsar;
    }

}
