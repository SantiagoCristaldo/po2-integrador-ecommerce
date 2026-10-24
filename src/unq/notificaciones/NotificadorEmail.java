package unq.notificaciones;

import java.util.HashSet;
import java.util.Set;

public class NotificadorEmail implements ObservadorReserva {
    private MailSender mailSender;
    private Set<String> estadosQueNotifica = new HashSet<String>();

    public NotificadorEmail(MailSender mailSender) {
        this.mailSender = mailSender;
        this.estadosQueNotifica.add("CONFIRMADA");
        this.estadosQueNotifica.add("EN_CURSO");
        this.estadosQueNotifica.add("FINALIZADA");
    }

    @Override
    public void actualizar(EventoCambioEstado e) {
        if (estadosQueNotifica.contains(this.obtenerNombreEstado(e.getEstadoActual()))) {
            String mensaje = armarMensaje(e);
            mailSender.enviarMail(null, "Cambio de estado", mensaje, null);
        }
    }

    private String armarMensaje(EventoCambioEstado e) {
        return "El estado de la reserva cambio a " + this.obtenerNombreEstado(e.getEstadoActual());
    }

    private String obtenerNombreEstado(unq.reserva.EstadoDeReserva estado) {
        if (estado == null) {
            return "";
        }
        return estado.getClass().getSimpleName().toUpperCase();
    }
}
