package unq.notificaciones;

import unq.reserva.*;


public class NotificadorEmail implements ObservadorReserva {
    private MailSender mailSender;

    public NotificadorEmail(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void actualizar(Reserva reserva) {
        if (reserva.getEstadoActual().debeNotificarEmail()) {
            String destino = reserva.getEmail();
            String titulo = "Actualización en su reserva";
            String mensaje = "Se actualizo el estado de su reserva a" + reserva.getEstadoActual().nombre() ;

            mailSender.enviarMail(destino, titulo, mensaje, null); // Nulo porque ni idea que ponerle
        }
    }
}
