package unq.reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unq.catalogo.ItemCatalogo;
import unq.catalogo.Categoria;
import unq.catalogo.PaqueteTuristico;
import unq.catalogo.ServicioIndividual;
import unq.notificaciones.ObservadorReserva;
import unq.excepciones.ExcepcionDominio;
import unq.metodosDePago.MedioDePago;
import unq.traslado.Direccion;
import unq.traslado.MetodoTraslado;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests de la Reserva real usando:
 *  - STUBS de ItemCatalogo (precio fijo con when/thenReturn)
 *  - DUMMY de Direccion
 *  - MOCKS de MetodoTraslado y MedioDePago para costo de envio y pago
 */
class ReservaTest {

    private static final LocalDate INICIO = LocalDate.of(2027, 1, 10);
    private static final LocalDate FIN = LocalDate.of(2027, 1, 12);

    private Reserva reserva;
    private Direccion destino;   // DUMMY
    private ItemCatalogo hotel;      // STUB
    private ItemCatalogo excursion;  // STUB

    @BeforeEach
    void setUp() {
        destino = mock(Direccion.class);

        hotel = mock(ItemCatalogo.class);
        when(hotel.precioFinal()).thenReturn(15000.0);

        excursion = mock(ItemCatalogo.class);
        when(excursion.precioFinal()).thenReturn(4000.0);

        reserva = new Reserva("cliente@mail.com", destino, 2, INICIO, FIN);
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

    // ---------- transiciones ----------

    @Test
    void confirmarPasaAPagadoYCuentaComoVenta() {
        reserva.agregarItem(hotel);
        reserva.confirmar();

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
    }

    @Test
    void cancelarDesdePendienteNoReembolsaYQuedaCancelada() {
        reserva.cancelar();

        assertTrue(reserva.getVouchersDeReembolso().isEmpty());
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

    // ---------- cupos (enunciado 2.2) ----------

    private ServicioIndividual servicioConCupo(int cupo) {
        return new ServicioIndividual("H1", "Hotel", "d", "op", new Categoria("Alojamiento"), 15000.0, cupo);
    }

    @Test
    void confirmarDecrementaElCupoDeCadaItemPorPasajero() {
        ServicioIndividual servicio = servicioConCupo(10);
        reserva.agregarItem(servicio);

        reserva.confirmar();

        assertEquals(8, servicio.getCuposDisponiblesEn(INICIO));
    }

    @Test
    void confirmarDecrementaTodasLasFechasEntreInicioYFin() {
        ServicioIndividual servicio = servicioConCupo(10);
        reserva.agregarItem(servicio);

        reserva.confirmar();

        assertEquals(8, servicio.getCuposDisponiblesEn(INICIO));
        assertEquals(8, servicio.getCuposDisponiblesEn(INICIO.plusDays(1)));
        assertEquals(8, servicio.getCuposDisponiblesEn(FIN));
    }

    @Test
    void confirmarNoTocaLasFechasFueraDelRango() {
        ServicioIndividual servicio = servicioConCupo(10);
        reserva.agregarItem(servicio);

        reserva.confirmar();

        assertEquals(10, servicio.getCuposDisponiblesEn(INICIO.minusDays(1)));
        assertEquals(10, servicio.getCuposDisponiblesEn(FIN.plusDays(1)));
    }

    @Test
    void confirmarSinCupoEnUnaFechaDelRangoLanzaExcepcionYSigueEnPendiente() {
        ServicioIndividual servicio = servicioConCupo(10);
        servicio.definirCupo(FIN, 1);
        reserva.agregarItem(servicio);

        assertThrows(ExcepcionDominio.class, reserva::confirmar);

        assertInstanceOf(SolicitudPendiente.class, reserva.getEstadoActual());
    }

    @Test
    void confirmarSinCupoEnUnItemNoDescuentaLosDemasItems() {
        ServicioIndividual conCupo = servicioConCupo(10);
        ServicioIndividual sinCupo = servicioConCupo(1);
        reserva.agregarItem(conCupo);
        reserva.agregarItem(sinCupo);

        assertThrows(ExcepcionDominio.class, reserva::confirmar);

        assertEquals(10, conCupo.getCuposDisponiblesEn(INICIO));
        assertEquals(1, sinCupo.getCuposDisponiblesEn(INICIO));
    }

    @Test
    void confirmarConUnServicioRepetidoSinCupoParaAmbosNoDescuentaNada() {
        ServicioIndividual servicio = servicioConCupo(3);
        reserva.agregarItem(servicio);
        reserva.agregarItem(servicio);

        assertThrows(ExcepcionDominio.class, reserva::confirmar);

        assertEquals(3, servicio.getCuposDisponiblesEn(INICIO));
    }

    @Test
    void confirmarSinCupoDentroDeUnPaqueteNoDescuentaNada() {
        ServicioIndividual suelto = servicioConCupo(10);
        ServicioIndividual enPaqueteConCupo = servicioConCupo(10);
        ServicioIndividual enPaqueteSinCupo = servicioConCupo(1);
        PaqueteTuristico paquete = new PaqueteTuristico("Pack", "d", 0, 0);
        paquete.agregarItem(enPaqueteConCupo);
        paquete.agregarItem(enPaqueteSinCupo);
        reserva.agregarItem(suelto);
        reserva.agregarItem(paquete);

        assertThrows(ExcepcionDominio.class, reserva::confirmar);

        assertEquals(10, suelto.getCuposDisponiblesEn(INICIO));
        assertEquals(10, enPaqueteConCupo.getCuposDisponiblesEn(INICIO));
        assertEquals(1, enPaqueteSinCupo.getCuposDisponiblesEn(INICIO));
    }

    @Test
    void confirmarConUnPaqueteDecrementaLosCuposDeSusServicios() {
        ServicioIndividual hotelReal = servicioConCupo(10);
        ServicioIndividual tour = servicioConCupo(5);
        PaqueteTuristico paquete = new PaqueteTuristico("Pack", "d", 0, 0);
        paquete.agregarItem(hotelReal);
        paquete.agregarItem(tour);
        reserva.agregarItem(paquete);

        reserva.confirmar();

        assertEquals(8, hotelReal.getCuposDisponiblesEn(FIN));
        assertEquals(3, tour.getCuposDisponiblesEn(FIN));
    }

    @Test
    void noSePuedeCrearUnaReservaConFinAnteriorAlInicio() {
        assertThrows(ExcepcionDominio.class,
                () -> new Reserva("cliente@mail.com", destino, 2, FIN, INICIO));
    }

    @Test
    void guardaLasFechasDeInicioYFin() {
        assertEquals(INICIO, reserva.getFechaInicio());
        assertEquals(FIN, reserva.getFechaFin());
    }

    @Test
    void cancelarDesdePagadoDevuelveElCupo() {
        ServicioIndividual servicio = servicioConCupo(10);
        reserva.agregarItem(servicio);
        reserva.confirmar();

        reserva.cancelar();

        assertEquals(10, servicio.getCuposDisponiblesEn(INICIO));
        assertEquals(10, servicio.getCuposDisponiblesEn(FIN));
    }

    @Test
    void cancelarDesdeCheckinDevuelveElCupo() {
        ServicioIndividual servicio = servicioConCupo(10);
        reserva.agregarItem(servicio);
        reserva.confirmar();
        reserva.hacerCheckin();

        reserva.cancelar();

        assertEquals(10, servicio.getCuposDisponiblesEn(INICIO));
    }

    @Test
    void cancelarEnCursoNoDevuelveElCupo() {
        ServicioIndividual servicio = servicioConCupo(10);
        reserva.agregarItem(servicio);
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.iniciar();

        reserva.cancelar();

        assertEquals(8, servicio.getCuposDisponiblesEn(INICIO));
    }

    @Test
    void cancelarDesdePendienteNoTocaElCupo() {
        ServicioIndividual servicio = servicioConCupo(10);
        reserva.agregarItem(servicio);

        reserva.cancelar();

        assertEquals(10, servicio.getCuposDisponiblesEn(INICIO));
    }

    // ---------- reembolsos (enunciado 2.2) ----------

    private MetodoTraslado trasladoQueCuesta(double costo) {
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        when(traslado.calcularCosto(any())).thenReturn(costo);
        return traslado;
    }

    @Test
    void cancelarDesdeCheckinRegistraUnVoucherPorServiciosYTasas() {
        reserva.setMetodoTraslado(trasladoQueCuesta(2500.0));
        reserva.agregarItem(hotel);
        reserva.confirmar();
        reserva.hacerCheckin();

        reserva.cancelar();

        assertEquals(1, reserva.getVouchersDeReembolso().size());
        assertEquals(17500.0, reserva.getVouchersDeReembolso().get(0).monto());
    }

    @Test
    void cancelarDesdeCheckinSinTrasladoReembolsaSoloLosServicios() {
        reserva.agregarItem(hotel);
        reserva.confirmar();
        reserva.hacerCheckin();

        reserva.cancelar();

        assertEquals(15000.0, reserva.getVouchersDeReembolso().get(0).monto());
    }

    @Test
    void cancelarEnCursoRegistraUnVoucherSinLasTasas() {
        reserva.setMetodoTraslado(trasladoQueCuesta(2500.0));
        reserva.agregarItem(hotel);
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.iniciar();

        reserva.cancelar();

        assertEquals(1, reserva.getVouchersDeReembolso().size());
        assertEquals(15000.0, reserva.getVouchersDeReembolso().get(0).monto());
    }

    @Test
    void elVoucherDeReembolsoQuedaAsociadoALaReserva() {
        reserva.agregarItem(hotel);
        reserva.confirmar();
        reserva.hacerCheckin();

        reserva.cancelar();

        assertSame(reserva, reserva.getVouchersDeReembolso().get(0).reservaAsociada);
    }

    @Test
    void cancelarDesdePagadoNoGeneraVoucher() {
        reserva.agregarItem(hotel);
        reserva.confirmar();

        reserva.cancelar();

        assertTrue(reserva.getVouchersDeReembolso().isEmpty());
    }

    // ---------- notificaciones (enunciado 2.5) ----------

    @Test
    void guardaElEmailDelCliente() {
        assertEquals("cliente@mail.com", reserva.getEmail());
    }

    @Test
    void cadaCambioDeEstadoAvisaALosObservadoresConElEstadoAnteriorYElNuevo() {
        ObservadorReserva observador = mock(ObservadorReserva.class);
        reserva.agregarObservador(observador);

        reserva.confirmar();

        verify(observador).actualizar(eq(reserva), isA(SolicitudPendiente.class), isA(Pagado.class));
    }

    @Test
    void avisaATodosLosObservadoresRegistrados() {
        ObservadorReserva email = mock(ObservadorReserva.class);
        ObservadorReserva club = mock(ObservadorReserva.class);
        reserva.agregarObservador(email);
        reserva.agregarObservador(club);

        reserva.cancelar();

        verify(email).actualizar(eq(reserva), isA(SolicitudPendiente.class), isA(Cancelada.class));
        verify(club).actualizar(eq(reserva), isA(SolicitudPendiente.class), isA(Cancelada.class));
    }

    @Test
    void unObservadorQuitadoNoRecibeAvisos() {
        ObservadorReserva observador = mock(ObservadorReserva.class);
        reserva.agregarObservador(observador);
        reserva.quitarObservador(observador);

        reserva.confirmar();

        verifyNoInteractions(observador);
    }

    @Test
    void unaOperacionInvalidaNoAvisaALosObservadores() {
        ObservadorReserva observador = mock(ObservadorReserva.class);
        reserva.agregarObservador(observador);

        assertThrows(ExcepcionPorInvalido.class, reserva::finalizar);

        verifyNoInteractions(observador);
    }

    // ---------- excepcion de dominio propia (enunciado 2.2) ----------

    @Test
    void laExcepcionDeOperacionInvalidaEsUnaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, reserva::finalizar);
    }
}
