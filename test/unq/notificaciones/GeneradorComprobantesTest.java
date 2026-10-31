package unq.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.reserva.Cancelada;
import unq.reserva.EnChecking;
import unq.reserva.EnCurso;
import unq.reserva.Finalizada;
import unq.reserva.Pagado;
import unq.reserva.Reserva;
import unq.reserva.SolicitudPendiente;

/**
 * Enunciado 2.5: "Generador de voucher/factura: crea el comprobante fiscal y voucher cuando la
 * reserva alcanza el estado FINALIZADA."
 */
class GeneradorComprobantesTest {

    private GeneradorComprobantes generador;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        generador = new GeneradorComprobantes();
        reserva = mock(Reserva.class);
        when(reserva.valorTotal()).thenReturn(35000.0);
    }

    @Test
    void alFinalizarLaReserva_creaUnaFacturaFiscal() {
        generador.actualizar(reserva, new EnCurso(), new Finalizada());

        assertEquals(1, generador.getFacturas().size());
    }

    @Test
    void alFinalizarLaReserva_creaUnVoucherDeViaje() {
        generador.actualizar(reserva, new EnCurso(), new Finalizada());

        assertEquals(1, generador.getVouchers().size());
    }

    @Test
    void laFacturaEsPorElValorTotalDeLaReserva() {
        generador.actualizar(reserva, new EnCurso(), new Finalizada());

        FacturaFiscal factura = generador.getFacturas().get(0);
        assertEquals(35000.0, factura.getTotal(), 0.001);
        assertSame(reserva, factura.getReserva());
    }

    @Test
    void elVoucherQuedaAsociadoALaReserva() {
        generador.actualizar(reserva, new EnCurso(), new Finalizada());

        assertSame(reserva, generador.getVouchers().get(0).getReserva());
    }

    @Test
    void cadaReservaFinalizadaTieneSuPropioNumeroDeFactura() {
        Reserva otra = mock(Reserva.class);

        generador.actualizar(reserva, new EnCurso(), new Finalizada());
        generador.actualizar(otra, new EnCurso(), new Finalizada());

        assertEquals(2, generador.getFacturas().size());
        assertNotEquals(generador.getFacturas().get(0).getNumero(), generador.getFacturas().get(1).getNumero());
    }

    @Test
    void lasTransicionesQueNoLleganAFinalizada_noGeneranComprobantes() {
        generador.actualizar(reserva, new SolicitudPendiente(), new Pagado());
        generador.actualizar(reserva, new Pagado(), new EnChecking());
        generador.actualizar(reserva, new EnChecking(), new EnCurso());
        generador.actualizar(reserva, new EnCurso(), new Cancelada());

        assertTrue(generador.getFacturas().isEmpty());
        assertTrue(generador.getVouchers().isEmpty());
    }
}
