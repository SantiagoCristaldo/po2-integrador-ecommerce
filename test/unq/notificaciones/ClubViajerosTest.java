package unq.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import unq.reserva.EstadoDeReserva;
import unq.reserva.Pagado;
import unq.reserva.Reserva;

class ClubViajerosTest {

    private MailSender mailSender;
    private ClubViajeros club;
    private Reserva reserva;
    private EstadoDeReserva anterior;
    private EstadoDeReserva actual;

    @BeforeEach
    void setUp() {
        mailSender = mock(MailSender.class);
        club = new ClubViajeros(mailSender);
        reserva = mock(Reserva.class);
        anterior = mock(EstadoDeReserva.class);
        actual = mock(EstadoDeReserva.class);
    }

    @Test
    void actualizar_cuandoLaReservaSeCancela_enviaMailConUnCuponDeDescuento() {
        when(actual.esCancelada()).thenReturn(true);
        ArgumentCaptor<Object> adjunto = ArgumentCaptor.forClass(Object.class);

        club.actualizar(reserva, anterior, actual);

        verify(mailSender).enviarMail(any(), anyString(), anyString(), adjunto.capture());
        assertInstanceOf(CuponDescuento.class, adjunto.getValue());
    }

    @Test
    void actualizar_elCuponGeneradoEsDel5PorCiento() {
        when(actual.esCancelada()).thenReturn(true);
        ArgumentCaptor<Object> adjunto = ArgumentCaptor.forClass(Object.class);

        club.actualizar(reserva, anterior, actual);

        verify(mailSender).enviarMail(any(), anyString(), anyString(), adjunto.capture());
        CuponDescuento cupon = (CuponDescuento) adjunto.getValue();
        assertEquals(5f, cupon.getPorcentaje(), 0.001f);
        assertTrue(cupon.getCodigo().startsWith("CuponNro"));
    }

    @Test
    void actualizar_cadaCancelacionGeneraUnCuponConCodigoDistinto() {
        when(actual.esCancelada()).thenReturn(true);
        ArgumentCaptor<Object> adjunto = ArgumentCaptor.forClass(Object.class);

        club.actualizar(reserva, anterior, actual);
        club.actualizar(reserva, anterior, actual);

        verify(mailSender, times(2)).enviarMail(any(), anyString(), anyString(), adjunto.capture());
        List<Object> cupones = adjunto.getAllValues();
        assertNotEquals(((CuponDescuento) cupones.get(0)).getCodigo(),
                ((CuponDescuento) cupones.get(1)).getCodigo());
    }

    @Test
    void actualizar_cuandoLaReservaNoSeCancela_noEnviaMail() {
        when(actual.esCancelada()).thenReturn(false);

        club.actualizar(reserva, anterior, actual);

        verify(mailSender, never()).enviarMail(any(), any(), any(), any());
    }

    @Test
    void actualizar_conEstadosReales_soloNotificaLaCancelacion() {
        club.actualizar(reserva, new Pagado(), new Cancelada());
        club.actualizar(reserva, new Cancelada(), new Pagado());

        verify(mailSender, times(1)).enviarMail(any(), anyString(), anyString(), any());
    }
}
