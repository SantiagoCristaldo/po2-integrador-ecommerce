package unq.notificaciones;

import unq.reserva.*;


public class NotificadorEmail implements ObservadorReserva {
    private MailSender mailSender;

    public NotificadorEmail(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void actualizar(Reserva reserva, EstadoDeReserva estadoAnterior, EstadoDeReserva estadoActual) {
        if (estadoActual.debeNotificarEmail()) {
            String destino = null; // aca va la direccion de la reserva 
            String titulo = "Actualización en su reserva";
            String mensaje = "Se actualizo el estado de su reserva a" + estadoActual ;

            mailSender.enviarMail(destino, titulo, mensaje, null); // Nulo porque ni idea que ponerle
        }
    }
}
