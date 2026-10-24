package unq.notificaciones;

import java.time.LocalDate;

import unq.reserva.Reserva;

public class GeneradorComprobantes implements ObservadorReserva {
    private RegistroTransacciones registro;

    public GeneradorComprobantes(RegistroTransacciones registro) {
        this.registro = registro;
    }

    @Override
    public void actualizar(EstadoDeReserva estadoAnterior, EstadoDeReserva estadoNuevo, Reserva reserva) {

    }

    private FacturaFiscal generarFactura(Reserva r) {

    }

    private VoucherViaje generarVoucher(Reserva r) {

    }

    private Reserva obtenerReserva() {
    }
}
