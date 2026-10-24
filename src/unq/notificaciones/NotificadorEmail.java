package unq.notificaciones;

import java.util.HashSet;
import java.util.Set;
import unq.reserva.*;
import unq.notificaciones.*;

public class NotificadorEmail implements ObservadorReserva {
    private MailSender mailSender;

    public NotificadorEmail(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void actualizar(EventoCambioEstado evento) {
        
        if (evento.getEstadoActual().debeNotificarEmail()) {
            String destino = "clienteEmail@gmail.com" ;
            String titulo  = "Actualización en su reserva - UNQ-Travel";
            String mensaje = armarMensaje(evento);
            
            mailSender.enviarMail(destino, titulo, mensaje, null);
        }
    }

    private String armarMensaje(EventoCambioEstado e) {
        return "El estado de la reserva cambio";
    }

    
