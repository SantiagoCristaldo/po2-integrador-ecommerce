package unq.notificaciones;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.reserva.EstadoDeReserva;
import unq.reserva.Finalizada;
import unq.reserva.Pagado;
import unq.reserva.Reserva;
import unq.reserva.SolicitudPendiente;

class NotificadorEmailTest {

    private MailSender mailSender;
    private NotificadorEmail notificador;
    private Reserva reserva;
    private EstadoDeReserva anterior;
    private EstadoDeReserva actual;

    @BeforeEach
    void setUp() {
        mailSender = mock(MailSender.class);
        notificador = new NotificadorEmail(mailSender);
        reserva = mock(Reserva.class);
        anterior = mock(EstadoDeReserva.class);
        actual = mock(EstadoDeReserva.class);
    }

    @Test
    void actualizar_cuandoElEstadoActualDebeNotificar_enviaUnMail() {
        when(actual.debeNotificarEmail()).thenReturn(true);

        notificador.actualizar(reserva, anterior, actual);

        verify(mailSender).enviarMail(any(), eq("Actualización en su reserva"), anyString(), any());
    }

    @Test
    void actualizar_cuandoElEstadoActualNoDebeNotificar_noEnviaMail() {
        when(actual.debeNotificarEmail()).thenReturn(false);

        notificador.actualizar(reserva, anterior, actual);

        verify(mailSender, never()).enviarMail(any(), any(), any(), any());
    }

    @Test
    void actualizar_alPasarAPagado_enviaUnMail() {
        notificador.actualizar(reserva, new SolicitudPendiente(), new Pagado());

        verify(mailSender).enviarMail(any(), anyString(), anyString(), any());
    }

    @Test
    void actualizar_alPasarAFinalizada_enviaUnMail() {
        notificador.actualizar(reserva, anterior, new Finalizada());

        verify(mailSender).enviarMail(any(), anyString(), anyString(), any());
    }

    @Test
    void actualizar_alQuedarEnSolicitudPendiente_noEnviaMail() {
        notificador.actualizar(reserva, anterior, new SolicitudPendiente());

        verify(mailSender, never()).enviarMail(any(), any(), any(), any());
    }
}
