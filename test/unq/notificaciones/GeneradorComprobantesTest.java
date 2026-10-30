package unq.notificaciones;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.reserva.EstadoDeReserva;
import unq.reserva.Finalizada;
import unq.reserva.Pagado;
import unq.reserva.Reserva;

class GeneradorComprobantesTest {

    private GeneradorComprobantes generador;
    private Reserva reserva;
    private EstadoDeReserva anterior;
    private EstadoDeReserva actual;

    @BeforeEach
    void setUp() {
        generador = new GeneradorComprobantes();
        reserva = mock(Reserva.class);
        anterior = mock(EstadoDeReserva.class);
        actual = mock(EstadoDeReserva.class);
    }

    @Test
    void actualizar_cuandoElEstadoAnteriorDebeGenerarVoucher_facturaElValorTotalDeLaReserva() {
        when(anterior.debeGenerarVoucher()).thenReturn(true);

        generador.actualizar(reserva, anterior, actual);

        verify(reserva).valorTotal();
    }

    @Test
    void actualizar_cuandoElEstadoAnteriorNoDebeGenerarVoucher_noFactura() {
        when(anterior.debeGenerarVoucher()).thenReturn(false);

        generador.actualizar(reserva, anterior, actual);

        verify(reserva, never()).valorTotal();
    }

    @Test
    void actualizar_desdeFinalizada_generaComprobantes() {
        generador.actualizar(reserva, new Finalizada(), actual);

        verify(reserva).valorTotal();
    }

    @Test
    void actualizar_desdePagado_noGeneraComprobantes() {
        generador.actualizar(reserva, new Pagado(), actual);

        verify(reserva, never()).valorTotal();
    }
}
