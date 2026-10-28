package unq.reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unq.catalogo.Catalogo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinalizadaTest {

    private Finalizada estado;
    private Reserva reserva;   // MOCK
    private Catalogo item;     // DUMMY

    @BeforeEach
    void setUp() {
        estado = new Finalizada();
        reserva = mock(Reserva.class);
        item = mock(Catalogo.class);
    }

    @Test
    void esTerminalNingunaOperacionEsValida() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.agregarItem(reserva, item));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.quitarItem(reserva, item));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.confirmar(reserva));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.hacerCheckin(reserva));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.iniciar(reserva));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.finalizar(reserva));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.cancelar(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void notificaEmailYGeneraVoucher() {
        assertTrue(estado.debeNotificarEmail());
        assertTrue(estado.debeGenerarVoucher());
        assertTrue(estado.cuentaComoVenta());
        assertFalse(estado.esCancelada());
    }
}
