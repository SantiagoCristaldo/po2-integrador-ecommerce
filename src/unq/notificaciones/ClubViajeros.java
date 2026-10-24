package unq.notificaciones;

import unq.reserva.Reserva;

public class ClubViajeros implements ObservadorReserva {
    private MailSender mailSender;
    private float porcentajeCupon = 5;

    public ClubViajeros(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void actualizar(EventoCambioEstado e) {
    // 1. Delegación polimórfica sobre el estado nuevo
    if (e.getEstadoActual().esCancelada()) {
        notificarCuponDescuento(e.getReserva());
    }
}

    private CuponDescuento generarCupon(Reserva r) {
        if (r == null) {
            return new CuponDescuento("CUPON-DEFAULT", porcentajeCupon, java.time.LocalDate.now().plusMonths(1));
        }
        return new CuponDescuento("CUPON-" + System.currentTimeMillis(), porcentajeCupon, java.time.LocalDate.now().plusMonths(1));
    }
