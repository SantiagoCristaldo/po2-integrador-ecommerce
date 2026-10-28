package unq.notificaciones;

import unq.reserva.EstadoDeReserva;
import unq.reserva.Reserva;

public class ClubViajeros implements ObservadorReserva {
    private MailSender mailSender;
    private float porcentajeCupon = 5;
    private static int contadorCupones = 0;

    public ClubViajeros(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void actualizar(Reserva reserva, EstadoDeReserva estadoAnterior, EstadoDeReserva estadoActual) {
        
        if (estadoActual.esCancelada()) {
            mailSender.enviarMail(null,"Actualizacion de el estado de la reserva",
                                     "Su reserva se encuentra cancelada", this.generarCupon(null));
        }
    }

    private CuponDescuento generarCupon(Reserva r) {
        contadorCupones++;
        return new CuponDescuento("CuponNro" + contadorCupones, porcentajeCupon);
    }

}
