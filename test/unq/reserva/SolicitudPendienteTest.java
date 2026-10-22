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

class SolicitudPendienteTest {

    private SolicitudPendiente estado;
    private Reserva reserva;
    private Catalogo item;

    @BeforeEach
    void setUp() {
        estado = new SolicitudPendiente();
        reserva = mock(Reserva.class);
        item = new ServicioIndividual("HOT-001", "Hotel Central", "Un hotel",
                "Operador SA", new Categoria("Alojamiento"), 15000.0);
    }

    @Test
    void agregarItem_agregaElItemALaReserva() {
        estado.agregarItem(reserva, item);

        verify(reserva).agregarItemDirecto(item);
    }

    @Test
    void quitarItem_quitaElItemDeLaReserva() {
        estado.quitarItem(reserva, item);

        verify(reserva).quitarItemDirecto(item);
    }

    @Test
    void confirmar_bloqueaElCupoDeLasUnidades() {
        estado.confirmar(reserva);

        verify(reserva).bloquearCupos();
    }

    @Test
    void confirmar_cambiaElEstadoAPagado() {
        estado.confirmar(reserva);

        ArgumentCaptor<EstadoDeReserva> captor = ArgumentCaptor.forClass(EstadoDeReserva.class);
        verify(reserva).cambiarEstado(captor.capture());

        assertInstanceOf(Pagado.class, captor.getValue());
    }

    @Test
    void confirmar_bloqueaElCupoAntesDeCambiarElEstado() {
        estado.confirmar(reserva);

        InOrder orden = inOrder(reserva);
        orden.verify(reserva).bloquearCupos();
        orden.verify(reserva).cambiarEstado(any(EstadoDeReserva.class));
    }

    @Test
    void cancelar_cambiaElEstadoACancelada() {
        estado.cancelar(reserva);

        ArgumentCaptor<EstadoDeReserva> captor = ArgumentCaptor.forClass(EstadoDeReserva.class);
        verify(reserva).cambiarEstado(captor.capture());

        assertInstanceOf(Cancelada.class, captor.getValue());
    }

    @Test
    void cancelar_noLiberaCuposNiReembolsa() {
        estado.cancelar(reserva);

        verify(reserva, never()).liberarCupos();
        verify(reserva, never()).reembolsar();
    }

    @Test
    void hacerCheckin_lanzaExcepcionDeDominioYNoCambiaElEstado() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.hacerCheckin(reserva));

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
