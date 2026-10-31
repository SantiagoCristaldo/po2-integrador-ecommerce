package unq.reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unq.catalogo.Catalogo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitudPendienteTest {

    private SolicitudPendiente estado;
    private Reserva reserva;   // MOCK: el estado se testea aislado de la reserva real
    private Catalogo item;     // DUMMY: solo se pasa como parámetro

    @BeforeEach
    void setUp() {
        estado = new SolicitudPendiente();
        reserva = mock(Reserva.class);
        item = mock(Catalogo.class);
    }

    @Test
    void agregarItemDelegaEnLaReserva() {
        estado.agregarItem(reserva, item);
        verify(reserva).agregarItemDirecto(item);
    }

    @Test
    void quitarItemDelegaEnLaReserva() {
        estado.quitarItem(reserva, item);
        verify(reserva).quitarItemDirecto(item);
    }

    @Test
    void confirmarBloqueaCuposYPasaAPagado() {
        estado.confirmar(reserva);

        verify(reserva).bloquearCupos();
        verify(reserva).cambiarEstado(any(Pagado.class));
    }

    @Test
    void confirmarBloqueaCuposAntesDeCambiarDeEstado() {
        estado.confirmar(reserva);

        var orden = inOrder(reserva);
        orden.verify(reserva).bloquearCupos();
        orden.verify(reserva).cambiarEstado(any(Pagado.class));
    }

    @Test
    void cancelarPasaACanceladaSinLiberarNiReembolsar() {
        estado.cancelar(reserva);

        verify(reserva).cambiarEstado(any(Cancelada.class));
        verify(reserva, never()).liberarCupos();
        verify(reserva, never()).reembolsarServicios();
        verify(reserva, never()).reembolsarServiciosYTasas();
    }

    @Test
    void hacerCheckinEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.hacerCheckin(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void iniciarEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.iniciar(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void finalizarEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.finalizar(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void noCuentaComoVentaNiNotifica() {
        assertFalse(estado.cuentaComoVenta());
        assertFalse(estado.debeNotificarEmail());
        assertFalse(estado.debeGenerarVoucher());
        assertFalse(estado.esCancelada());
    }
}
