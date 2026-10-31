package unq.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import unq.reserva.Cancelada;
import unq.reserva.EnChecking;
import unq.reserva.EnCurso;
import unq.reserva.Finalizada;
import unq.reserva.Pagado;
import unq.reserva.Reserva;
import unq.reserva.SolicitudPendiente;

/**
 * Enunciado 2.5: "Fidelización / Club de Viajeros: si se cancela una reserva, se le envía un
 * mensaje con un cupón de descuento del 5%."
 */
class ClubViajerosTest {

    private static final String EMAIL = "cliente@mail.com";

    private MailSender mailSender;
    private ClubViajeros club;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        mailSender = mock(MailSender.class);
        club = new ClubViajeros(mailSender);
        reserva = mock(Reserva.class);
        when(reserva.getEmail()).thenReturn(EMAIL);
    }

    private CuponDescuento cuponEnviado() {
        ArgumentCaptor<Object> adjunto = ArgumentCaptor.forClass(Object.class);
        verify(mailSender).enviarMail(any(), anyString(), anyString(), adjunto.capture());
        return assertInstanceOf(CuponDescuento.class, adjunto.getValue());
    }

    @Test
    void alCancelarse_enviaUnMensajeConUnCupon() {
        club.actualizar(reserva, new Pagado(), new Cancelada());

        cuponEnviado();
    }

    @Test
    void elCuponEsDel5PorCiento() {
        club.actualizar(reserva, new Pagado(), new Cancelada());

        assertEquals(5f, cuponEnviado().getPorcentaje(), 0.001f);
    }

    @Test
    void elMensajeSeEnviaAlClienteDeLaReserva() {
        club.actualizar(reserva, new Pagado(), new Cancelada());

        verify(mailSender).enviarMail(eq(EMAIL), anyString(), anyString(), any());
    }

    @Test
    void cadaCancelacionGeneraUnCuponDistinto() {
        ArgumentCaptor<Object> adjunto = ArgumentCaptor.forClass(Object.class);

        club.actualizar(reserva, new Pagado(), new Cancelada());
        club.actualizar(reserva, new SolicitudPendiente(), new Cancelada());

        verify(mailSender, times(2)).enviarMail(any(), anyString(), anyString(), adjunto.capture());
        List<Object> cupones = adjunto.getAllValues();
        assertNotEquals(((CuponDescuento) cupones.get(0)).getCodigo(),
                ((CuponDescuento) cupones.get(1)).getCodigo());
    }

    @Test
    void cancelarDesdeCualquierEstado_enviaElCupon() {
        club.actualizar(reserva, new SolicitudPendiente(), new Cancelada());
        club.actualizar(reserva, new EnChecking(), new Cancelada());
        club.actualizar(reserva, new EnCurso(), new Cancelada());

        verify(mailSender, times(3)).enviarMail(any(), anyString(), anyString(), any());
    }

    @Test
    void lasTransicionesQueNoSonCancelacion_noEnvianNada() {
        club.actualizar(reserva, new SolicitudPendiente(), new Pagado());
        club.actualizar(reserva, new Pagado(), new EnChecking());
        club.actualizar(reserva, new EnChecking(), new EnCurso());
        club.actualizar(reserva, new EnCurso(), new Finalizada());

        verify(mailSender, never()).enviarMail(any(), any(), any(), any());
    }
}
