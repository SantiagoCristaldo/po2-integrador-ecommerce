package unq.reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unq.catalogo.Catalogo;
import unq.excepciones.ExcepcionDominio;
import unq.metodosDePago.MedioDePago;
import unq.traslado.Direccion;
import unq.traslado.MetodoTraslado;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests de la Reserva real usando:
 *  - STUBS de Catalogo (precio fijo con when/thenReturn)
 *  - DUMMY de Direccion
 *  - SPY de la Reserva para verificar que los estados llaman a bloquear/liberar/reembolsar
 *  - MOCKS de MetodoTraslado y MedioDePago para costo de envio y pago
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
    void guardaLaCantidadDePasajeros() {
        assertEquals(2, reserva.cantidadDePasajeros());
    }

    @Test
    void laFechaEsLaDeCreacion() {
        assertEquals(LocalDate.now(), reserva.getFecha());
    }

    @Test
    void arrancaSinItems() {
        assertTrue(reserva.getItems().isEmpty());
        assertEquals(0.0, reserva.valorServicios());
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

    @Test
    void unaReservaCanceladaNoCuentaComoVenta() {
        reserva.confirmar();
        reserva.cancelar();

        assertFalse(reserva.cuentaComoVenta());
    }

    @Test
    void noSePuedenQuitarItemsDespuesDeConfirmar() {
        reserva.agregarItem(hotel);
        reserva.confirmar();

        assertThrows(ExcepcionPorInvalido.class, () -> reserva.quitarItem(hotel));
        assertEquals(1, reserva.getItems().size());
    }

    // ---------- costo de envio (mock de MetodoTraslado) ----------

    @Test
    void getCostoEnvioSinMetodoDeTrasladoLanza() {
        assertThrows(ExcepcionDominio.class, reserva::getCostoEnvio);
    }

    @Test
    void getCostoEnvioDelegaEnElMetodoDeTraslado() {
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        when(traslado.calcularCosto(reserva)).thenReturn(2500.0);
        reserva.setMetodoTraslado(traslado);

        assertEquals(2500.0, reserva.getCostoEnvio());
        verify(traslado).calcularCosto(reserva);
    }

    @Test
    void valorTotalSumaServiciosYEnvio() {
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        when(traslado.calcularCosto(reserva)).thenReturn(2500.0);
        reserva.setMetodoTraslado(traslado);
        reserva.agregarItem(hotel);
        reserva.agregarItem(excursion);

        assertEquals(21500.0, reserva.valorTotal());
    }

    @Test
    void valorTotalSinMetodoDeTrasladoLanza() {
        reserva.agregarItem(hotel);
        assertThrows(ExcepcionDominio.class, reserva::valorTotal);
    }

    // ---------- pago (mock de MedioDePago) ----------

    @Test
    void procesarPagoSinMedioDePagoLanza() {
        assertThrows(ExcepcionDominio.class, reserva::procesarPago);
    }

    @Test
    void procesarPagoCobraElValorTotal() {
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        when(traslado.calcularCosto(reserva)).thenReturn(1000.0);
        MedioDePago medio = mock(MedioDePago.class);
        reserva.setMetodoTraslado(traslado);
        reserva.setMetodoPago(medio);
        reserva.agregarItem(hotel);

        reserva.procesarPago();

        var orden = inOrder(medio);
        orden.verify(medio).setMonto(16000.0);
        orden.verify(medio).procesoDePago();
    }

    @Test
    void procesarPagoSinMetodoDeTrasladoNoCobra() {
        MedioDePago medio = mock(MedioDePago.class);
        reserva.setMetodoPago(medio);

        assertThrows(ExcepcionDominio.class, reserva::procesarPago);
        verify(medio, never()).procesoDePago();
    }

    @Test
    void siElPagoFallaSePropagaLaExcepcion() {
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        MedioDePago medio = mock(MedioDePago.class);
        doThrow(new ExcepcionDominio("rechazado")).when(medio).procesoDePago();
        reserva.setMetodoTraslado(traslado);
        reserva.setMetodoPago(medio);

        assertThrows(ExcepcionDominio.class, reserva::procesarPago);
    }
}
