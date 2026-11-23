package unq.reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unq.catalogo.ItemCatalogo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnCheckingTest {

    private EnChecking estado;
    private Reserva reserva;   // MOCK
    private ItemCatalogo item;     // DUMMY

    @BeforeEach
    void setUp() {
        estado = new EnChecking();
        reserva = mock(Reserva.class);
        item = mock(ItemCatalogo.class);
    }

    @Test
    void iniciarPasaAEnCurso() {
        estado.iniciar(reserva);
        verify(reserva).cambiarEstado(any(EnCurso.class));
    }

    @Test
    void cancelarLiberaCuposReembolsaYPasaACancelada() {
        estado.cancelar(reserva);

        var orden = inOrder(reserva);
        orden.verify(reserva).liberarCupos();
        orden.verify(reserva).reembolsarServiciosYTasas();
        orden.verify(reserva).cambiarEstado(any(Cancelada.class));
    }

    @Test
    void cancelarNoReembolsaSoloLosServicios() {
        estado.cancelar(reserva);
        verify(reserva, never()).reembolsarServicios();
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
    void hacerCheckinDeNuevoEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.hacerCheckin(reserva));
        verifyNoInteractions(reserva);
    }

    @Test
    void finalizarSinIniciarEsInvalido() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.finalizar(reserva));
        verifyNoInteractions(reserva);
    }
}
