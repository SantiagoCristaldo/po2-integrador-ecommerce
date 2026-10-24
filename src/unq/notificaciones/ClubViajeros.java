package unq.notificaciones;

import unq.reserva.Reserva;

public class ClubViajeros implements ObservadorReserva {
    private MailSender mailSender;
    private float porcentajeCupon = 5;

    public ClubViajeros(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void actualizar(EstadoDeReserva estadoAnterior, EstadoDeReserva estadoNuevo, Reserva reserva) {
        // 1. Delegación polimórfica sobre el estado nuevo
        if (e.getEstadoActual().esCancelada()) {
            mailSender.enviarMail("direccionDestino", "titulo", "mensaje", generarCupon(null));
        }
    }

    private CuponDescuento generarCupon(Reserva r) {
        return new CuponDescuento("CUPON-" + System.currentTimeMillis(), porcentajeCupon,
                java.time.LocalDate.now().plusMonths(1));
    }

}
