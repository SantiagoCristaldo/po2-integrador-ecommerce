package unq.reserva;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;

class EnCheckingTest {

    private EnChecking estado;
    private Reserva reserva;
    private Catalogo item;

    @BeforeEach
    void setUp() {
        estado = new EnChecking();
        reserva = mock(Reserva.class);
        item = new ServicioIndividual("HOT-001", "Hotel Central", "Un hotel",
                "Operador SA", new Categoria("Alojamiento"), 15000.0);
    }

    @Test
    void iniciar_cambiaElEstadoAEnCurso() {
        estado.iniciar(reserva);

        ArgumentCaptor<EstadoDeReserva> captor = ArgumentCaptor.forClass(EstadoDeReserva.class);
        verify(reserva).cambiarEstado(captor.capture());

        assertInstanceOf(EnCurso.class, captor.getValue());
    }

    @Test
    void cancelar_liberaElCupoDeLasUnidades() {
        estado.cancelar(reserva);

        verify(reserva).liberarCupos();
    }

    @Test
    void cancelar_reembolsaLosServiciosYTasasDeReserva() {
        estado.cancelar(reserva);

        verify(reserva).reembolsar();
    }

    @Test
    void cancelar_cambiaElEstadoACancelada() {
        estado.cancelar(reserva);

        ArgumentCaptor<EstadoDeReserva> captor = ArgumentCaptor.forClass(EstadoDeReserva.class);
        verify(reserva).cambiarEstado(captor.capture());

        assertInstanceOf(Cancelada.class, captor.getValue());
    }

    @Test
    void cancelar_liberaElCupoAntesDeReembolsarYAntesDeCambiarElEstado() {
        estado.cancelar(reserva);

        InOrder orden = inOrder(reserva);
        orden.verify(reserva).liberarCupos();
        orden.verify(reserva).reembolsar();
        orden.verify(reserva).cambiarEstado(any(EstadoDeReserva.class));
    }

    @Test
    void agregarItem_lanzaExcepcionDeDominioYNoCambiaElEstado() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.agregarItem(reserva, item));

        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void quitarItem_lanzaExcepcionDeDominioYNoCambiaElEstado() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.quitarItem(reserva, item));

        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void confirmar_lanzaExcepcionDeDominioYNoCambiaElEstado() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.confirmar(reserva));

        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void hacerCheckin_lanzaExcepcionDeDominioYNoCambiaElEstado() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.hacerCheckin(reserva));

        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void finalizar_lanzaExcepcionDeDominioYNoCambiaElEstado() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.finalizar(reserva));

        verify(reserva, never()).cambiarEstado(any());
    }
}
