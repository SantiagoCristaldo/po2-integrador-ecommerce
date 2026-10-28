package unq.reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unq.catalogo.Catalogo;
import unq.traslado.Direccion;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests de la Reserva real usando:
 *  - STUBS de Catalogo (precio fijo con when/thenReturn)
 *  - DUMMY de Direccion
 *  - SPY de la Reserva para verificar que los estados llaman a bloquear/liberar/reembolsar
 */
class ReservaTest {

    private Reserva reserva;
    private Direccion destino;   // DUMMY
    private Catalogo hotel;      // STUB
    private Catalogo excursion;  // STUB

    @BeforeEach
    void setUp() {
        destino = mock(Direccion.class);

        hotel = mock(Catalogo.class);
        when(hotel.precioFinal()).thenReturn(15000.0);

        excursion = mock(Catalogo.class);
        when(excursion.precioFinal()).thenReturn(4000.0);

        reserva = spy(new Reserva("cliente@mail.com", destino, 2));
    }

    // ---------- estado inicial y datos ----------

    @Test
    void arrancaEnSolicitudPendiente() {
        assertInstanceOf(SolicitudPendiente.class, reserva.getEstadoActual());
        assertFalse(reserva.cuentaComoVenta());
    }

    @Test
    void guardaDestinoYPasajeros() {
        assertSame(destino, reserva.getDestino());
        assertEquals(2, reserva.cantidadDePasajeros());
    }

    @Test
    void usaLaFechaIndicada() {
        LocalDate fecha = LocalDate.of(2026, 12, 24);
        Reserva conFecha = new Reserva("cliente@mail.com", destino, 1, fecha);
        assertEquals(fecha, conFecha.getFecha());
    }

    // ---------- items (stubs) ----------

    @Test
    void valorServiciosSumaElPrecioFinalDeLosItems() {
        reserva.agregarItem(hotel);
        reserva.agregarItem(excursion);

        assertEquals(19000.0, reserva.valorServicios());
        verify(hotel).precioFinal();
        verify(excursion).precioFinal();
    }

    @Test
    void quitarItemLoSacaDeLaReserva() {
        reserva.agregarItem(hotel);
        reserva.agregarItem(excursion);
        reserva.quitarItem(hotel);

        assertEquals(1, reserva.getItems().size());
        assertEquals(4000.0, reserva.valorServicios());
    }

    @Test
    void getItemsDevuelveUnaCopia() {
        reserva.agregarItem(hotel);
        reserva.getItems().clear();
        assertEquals(1, reserva.getItems().size());
    }

    @Test
    void noSePuedenAgregarItemsDespuesDeConfirmar() {
        reserva.agregarItem(hotel);
        reserva.confirmar();

        assertThrows(ExcepcionPorInvalido.class, () -> reserva.agregarItem(excursion));
        assertEquals(1, reserva.getItems().size());
    }

    // ---------- transiciones (spy) ----------

    @Test
    void confirmarBloqueaCuposYPasaAPagado() {
        reserva.agregarItem(hotel);
        reserva.confirmar();

        verify(reserva).bloquearCupos();
        assertInstanceOf(Pagado.class, reserva.getEstadoActual());
        assertTrue(reserva.cuentaComoVenta());
    }

    @Test
    void cicloCompletoHastaFinalizada() {
        reserva.agregarItem(hotel);
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.iniciar();
        reserva.finalizar();

        assertInstanceOf(Finalizada.class, reserva.getEstadoActual());
        verify(reserva, times(4)).cambiarEstado(any(EstadoDeReserva.class));
    }

    @Test
    void cancelarDesdePagadoLiberaCupos() {
        reserva.confirmar();
        reserva.cancelar();

        verify(reserva).liberarCupos();
        verify(reserva, never()).reembolsar();
        assertTrue(reserva.getEstadoActual().esCancelada());
    }

    @Test
    void cancelarDesdeCheckinLiberaYReembolsa() {
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.cancelar();

        verify(reserva).liberarCupos();
        verify(reserva).reembolsar();
        assertTrue(reserva.getEstadoActual().esCancelada());
    }

    @Test
    void cancelarEnCursoReembolsaPeroNoLibera() {
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.iniciar();
        reserva.cancelar();

        verify(reserva).reembolsar();
        verify(reserva, never()).liberarCupos();
    }

    @Test
    void cancelarDesdePendienteNoTocaCuposNiReembolsa() {
        reserva.cancelar();

        verify(reserva, never()).liberarCupos();
        verify(reserva, never()).reembolsar();
        assertTrue(reserva.getEstadoActual().esCancelada());
    }

    @Test
    void noSePuedeSaltearEstados() {
        assertThrows(ExcepcionPorInvalido.class, reserva::iniciar);
        assertThrows(ExcepcionPorInvalido.class, reserva::finalizar);
        assertThrows(ExcepcionPorInvalido.class, reserva::hacerCheckin);
        assertInstanceOf(SolicitudPendiente.class, reserva.getEstadoActual());
    }

    @Test
    void unaOperacionInvalidaNoCambiaElEstado() {
        assertThrows(ExcepcionPorInvalido.class, reserva::finalizar);
        verify(reserva, never()).cambiarEstado(any());
    }

    @Test
    void cancelarDosVecesFalla() {
        reserva.cancelar();
        assertThrows(ExcepcionPorInvalido.class, reserva::cancelar);
    }
}
