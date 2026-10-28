package unq.reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unq.catalogo.Catalogo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagadoTest {

    private Pagado estado;
    private Reserva reserva;   // MOCK
    private Catalogo item;     // DUMMY

    @BeforeEach
    void setUp() {
        estado = new Pagado();
        reserva = mock(Reserva.class);
        item = mock(Catalogo.class);
    }

    @Test
    void hacerCheckinPasaAEnChecking() {
        estado.hacerCheckin(reserva);
        verify(reserva).cambiarEstado(any(EnChecking.class));
    }

    @Test
    void cancelarLiberaCuposYPasaACancelada() {
        estado.cancelar(reserva);

        var orden = inOrder(reserva);
        orden.verify(reserva).liberarCupos();
        orden.verify(reserva).cambiarEstado(any(Cancelada.class));
    }

    @Test
    void cancelarNoReembolsa() {
        estado.cancelar(reserva);
        verify(reserva, never()).reembolsar();
    }

    @Test
    void agregarItemEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.agregarItem(reserva, item));
        verify(reserva, never()).agregarItemDirecto(any());
    }

    @Test
    void quitarItemEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.quitarItem(reserva, item));
        verify(reserva, never()).quitarItemDirecto(any());
    }

    @Test
    void confirmarDeNuevoEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.confirmar(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void iniciarSinCheckinEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.iniciar(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void finalizarEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.finalizar(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void notificaPorEmailYCuentaComoVenta() {
        assertTrue(estado.debeNotificarEmail());
        assertTrue(estado.cuentaComoVenta());
        assertFalse(estado.debeGenerarVoucher());
    }
}
