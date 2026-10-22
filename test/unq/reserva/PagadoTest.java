package unq.reserva;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;

class PagadoTest {

    private Pagado estado;
    private Reserva reserva;
    private Catalogo item;

    @BeforeEach
    void setUp() {
        estado = new Pagado();
        reserva = mock(Reserva.class);
        item = new ServicioIndividual("HOT-001", "Hotel Central", "Un hotel",
                "Operador SA", new Categoria("Alojamiento"), 15000.0);
    }

    @Test
    void hacerCheckin_cambiaElEstadoAEnChecking() {
        estado.hacerCheckin(reserva);

        ArgumentCaptor<EstadoDeReserva> captor = ArgumentCaptor.forClass(EstadoDeReserva.class);
        verify(reserva).cambiarEstado(captor.capture());

        assertInstanceOf(EnChecking.class, captor.getValue());
    }

    @Test
    void cancelar_liberaElCupoDeLasUnidades() {
        estado.cancelar(reserva);

        verify(reserva).liberarCupos();
    }

    @Test
    void cancelar_cambiaElEstadoACancelada() {
        estado.cancelar(reserva);

        ArgumentCaptor<EstadoDeReserva> captor = ArgumentCaptor.forClass(EstadoDeReserva.class);
        verify(reserva).cambiarEstado(captor.capture());

        assertInstanceOf(Cancelada.class, captor.getValue());
    }

    @Test
    void cancelar_noReembolsa() {
        estado.cancelar(reserva);

        verify(reserva, never()).reembolsar();
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
    void iniciar_lanzaExcepcionDeDominioYNoCambiaElEstado() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.iniciar(reserva));

        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void finalizar_lanzaExcepcionDeDominioYNoCambiaElEstado() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.finalizar(reserva));

        verify(reserva, never()).cambiarEstado(any());
    }
}
