package unq.notificaciones;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import unq.reserva.Cancelada;
import unq.reserva.EnChecking;
import unq.reserva.EnCurso;
import unq.reserva.EstadoDeReserva;
import unq.reserva.Finalizada;
import unq.reserva.Pagado;
import unq.reserva.Reserva;
import unq.reserva.SolicitudPendiente;

/**
 * Enunciado 2.5: "Notificador de email: envía un correo al cliente con el nuevo estado de su
 * reserva. Solo actúa ante las transiciones CONFIRMADA, EN_CURSO y FINALIZADA."
 */
class NotificadorEmailTest {

    private static final String EMAIL = "cliente@mail.com";

    private MailSender mailSender;
    private NotificadorEmail notificador;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        mailSender = mock(MailSender.class);
        notificador = new NotificadorEmail(mailSender);
        reserva = mock(Reserva.class);
        when(reserva.getEmail()).thenReturn(EMAIL);
    }

    // --- Transiciones en las que actúa ---

    @Test
    void alConfirmarse_enviaUnMail() {
        notificador.actualizar(reserva, new SolicitudPendiente(), new Pagado());

        verify(mailSender).enviarMail(any(), anyString(), anyString(), any());
    }

    @Test
    void alPasarAEnCurso_enviaUnMail() {
        notificador.actualizar(reserva, new EnChecking(), new EnCurso());

        verify(mailSender).enviarMail(any(), anyString(), anyString(), any());
    }

    @Test
    void alFinalizar_enviaUnMail() {
        notificador.actualizar(reserva, new EnCurso(), new Finalizada());

        verify(mailSender).enviarMail(any(), anyString(), anyString(), any());
    }

    // --- Transiciones en las que no actúa ---

    @Test
    void alHacerCheckin_noEnviaMail() {
        notificador.actualizar(reserva, new Pagado(), new EnChecking());

        verify(mailSender, never()).enviarMail(any(), any(), any(), any());
    }

    @Test
    void alCancelarse_noEnviaMail() {
        notificador.actualizar(reserva, new Pagado(), new Cancelada());

        verify(mailSender, never()).enviarMail(any(), any(), any(), any());
    }

    // --- Contenido del mail ---

    @Test
    void elMailSeEnviaAlEmailDelCliente() {
        notificador.actualizar(reserva, new SolicitudPendiente(), new Pagado());

        verify(mailSender).enviarMail(eq(EMAIL), anyString(), anyString(), any());
    }

    @Test
    void elMensajeIndicaElNuevoEstadoDeLaReserva() {
        EstadoDeReserva nuevo = new Pagado();
        ArgumentCaptor<String> mensaje = ArgumentCaptor.forClass(String.class);

        notificador.actualizar(reserva, new SolicitudPendiente(), nuevo);

        verify(mailSender).enviarMail(any(), anyString(), mensaje.capture(), any());
        assertTrue(mensaje.getValue().contains(nuevo.nombre()),
                "el mensaje deberia nombrar el nuevo estado (" + nuevo.nombre() + ") y fue: " + mensaje.getValue());
    }

    @Test
    void elMensajeNoMuestraElObjetoInternoDelEstado() {
        EstadoDeReserva nuevo = new Pagado();
        ArgumentCaptor<String> mensaje = ArgumentCaptor.forClass(String.class);

        notificador.actualizar(reserva, new SolicitudPendiente(), nuevo);

        verify(mailSender).enviarMail(any(), anyString(), mensaje.capture(), any());
        assertFalse(mensaje.getValue().contains(nuevo.toString()),
                "el cliente no deberia ver '" + nuevo.toString() + "' en el mail");
    }
}
