package unq.reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unq.catalogo.Catalogo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnCursoTest {

    private EnCurso estado;
    private Reserva reserva;   // MOCK
    private Catalogo item;     // DUMMY

    @BeforeEach
    void setUp() {
        estado = new EnCurso();
        reserva = mock(Reserva.class);
        item = mock(Catalogo.class);
    }

    @Test
    void finalizarPasaAFinalizada() {
        estado.finalizar(reserva);
        verify(reserva).cambiarEstado(any(Finalizada.class));
    }

    @Test
    void cancelarReembolsaYPasaACancelada() {
        estado.cancelar(reserva);

        var orden = inOrder(reserva);
        orden.verify(reserva).reembolsarServicios();
        orden.verify(reserva).cambiarEstado(any(Cancelada.class));
    }

    @Test
    void cancelarNoLiberaCupos() {
        estado.cancelar(reserva);
        verify(reserva, never()).liberarCupos();
    }

    @Test
    void cancelarNoReembolsaLasTasas() {
        estado.cancelar(reserva);
        verify(reserva, never()).reembolsarServiciosYTasas();
    }

    @Test
    void agregarItemEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.agregarItem(reserva, item));
        verifyNoInteractions(reserva);
    }

    @Test
    void quitarItemEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.quitarItem(reserva, item));
        verifyNoInteractions(reserva);
    }

    @Test
    void confirmarEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.confirmar(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void hacerCheckinEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.hacerCheckin(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void iniciarDeNuevoEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.iniciar(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void notificaPorEmail() {
        assertTrue(estado.debeNotificarEmail());
        assertFalse(estado.debeGenerarVoucher());
    }
}
