package unq.reserva;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;

/**
 * Enunciado 2.2: "Intentar una operación inválida debe lanzar una excepción de dominio propia".
 * En el patrón State, lo seguro es que el estado abstracto considere inválida toda operación y que
 * cada estado concreto redefina sólo las transiciones que permite. Hoy EstadoDeReserva tiene cuerpos
 * vacíos: un estado nuevo que se olvide de redefinir una operación la acepta en silencio.
 */
class EstadoDeReservaTest {

    private EstadoDeReserva estadoSinTransiciones;
    private Reserva reserva;
    private Catalogo item;

    @BeforeEach
    void setUp() {
        estadoSinTransiciones = new EstadoDeReserva() {};
        reserva = mock(Reserva.class);
        item = mock(Catalogo.class);
    }

    @Test
    void porDefecto_agregarItemEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estadoSinTransiciones.agregarItem(reserva, item));
        verify(reserva, never()).agregarItemDirecto(any());
    }

    @Test
    void porDefecto_quitarItemEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estadoSinTransiciones.quitarItem(reserva, item));
        verify(reserva, never()).quitarItemDirecto(any());
    }

    @Test
    void porDefecto_confirmarEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estadoSinTransiciones.confirmar(reserva));
        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void porDefecto_hacerCheckinEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estadoSinTransiciones.hacerCheckin(reserva));
        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void porDefecto_iniciarEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estadoSinTransiciones.iniciar(reserva));
        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void porDefecto_finalizarEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estadoSinTransiciones.finalizar(reserva));
        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void porDefecto_cancelarEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estadoSinTransiciones.cancelar(reserva));
        verify(reserva, never()).cambiarEstado(any());
    }
}
