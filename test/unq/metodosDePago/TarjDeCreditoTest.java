package unq.metodosDePago;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import unq.excepciones.ExcepcionPagoInvalido;

class TarjDeCreditoTest {

    private static final int NUMERO_TARJETA = 4539;
    private static final int CVV = 123;
    private static final double MONTO = 25000.0;

    private APITarjetaDeCredito api;
    private LocalDateTime vencimiento;
    private TarjDeCredito tarjeta;

    @BeforeEach
    void setUp() {
        api = mock(APITarjetaDeCredito.class);
        vencimiento = LocalDateTime.of(2028, 5, 1, 0, 0);
        tarjeta = new TarjDeCredito(NUMERO_TARJETA, CVV, vencimiento, MONTO, "TX-1001", api);
    }

    private void datosValidos() {
        when(api.validarCVV(NUMERO_TARJETA, CVV)).thenReturn(true);
        when(api.validarVencimiento(vencimiento)).thenReturn(true);
        when(api.preAutorizar(NUMERO_TARJETA, MONTO)).thenReturn(true);
    }

    // --- Validar datos: numero de tarjeta, CVV y vencimiento ---

    @Test
    void validarDatos_conCVVYVencimientoValidos_esVerdadero() {
        datosValidos();

        assertTrue(tarjeta.validarDatos());
    }

    @Test
    void validarDatos_validaElCVVContraElNumeroDeTarjeta() {
        datosValidos();

        tarjeta.validarDatos();

        verify(api).validarCVV(NUMERO_TARJETA, CVV);
    }

    @Test
    void validarDatos_validaElVencimiento() {
        datosValidos();

        tarjeta.validarDatos();

        verify(api).validarVencimiento(vencimiento);
    }

    @Test
    void validarDatos_conCVVInvalido_esFalso() {
        when(api.validarCVV(NUMERO_TARJETA, CVV)).thenReturn(false);
        when(api.validarVencimiento(vencimiento)).thenReturn(true);

        assertFalse(tarjeta.validarDatos());
    }

    @Test
    void validarDatos_conTarjetaVencida_esFalso() {
        when(api.validarCVV(NUMERO_TARJETA, CVV)).thenReturn(true);
        when(api.validarVencimiento(vencimiento)).thenReturn(false);

        assertFalse(tarjeta.validarDatos());
    }

    // --- Reservar fondos y ejecutar ---

    @Test
    void reservarFondos_haceLaPreAutorizacionDelMontoEnLaTarjeta() {
        when(api.preAutorizar(NUMERO_TARJETA, MONTO)).thenReturn(true);

        tarjeta.reservarFondos();

        verify(api).preAutorizar(NUMERO_TARJETA, MONTO);
    }

    @Test
    void ejecutarTransaccion_transfiereElMontoDeInmediato() {
        tarjeta.ejecutarTransaccion();

        verify(api).transferirInmediata(MONTO);
    }

    // --- Notificar: voucher impreso/digital, generado y registrado ---

    @Test
    void notificarResultado_generaYRegistraUnVoucher() {
        tarjeta.notificarResultado();

        assertEquals(1, tarjeta.getComprobantes().size());
        ComprobantePago voucher = tarjeta.getComprobantes().get(0);
        assertTrue(voucher.getTipo().contains("Voucher"));
        assertEquals("TX-1001", voucher.getCodigoTransaccion());
        assertEquals(MONTO, voucher.getMonto(), 0.001);
    }

    // --- La plantilla completa ---

    @Test
    void procesoDePago_conDatosValidos_validaPreAutorizaTransfiereYRegistraElVoucher() {
        datosValidos();

        tarjeta.procesoDePago();

        InOrder orden = inOrder(api);
        orden.verify(api).validarCVV(NUMERO_TARJETA, CVV);
        orden.verify(api).preAutorizar(NUMERO_TARJETA, MONTO);
        orden.verify(api).transferirInmediata(MONTO);
        assertEquals(1, tarjeta.getComprobantes().size());
    }

    // Reservar fondos = pre-autorización al banco emisor. Si el banco la rechaza, no hay fondos
    // reservados y no se puede ejecutar la transacción.
    @Test
    void procesoDePago_conPreAutorizacionRechazada_lanzaPagoInvalidoYNoTransfiere() {
        datosValidos();
        when(api.preAutorizar(NUMERO_TARJETA, MONTO)).thenReturn(false);

        assertThrows(ExcepcionPagoInvalido.class, tarjeta::procesoDePago);

        verify(api, never()).transferirInmediata(anyDouble());
        assertTrue(tarjeta.getComprobantes().isEmpty());
    }

    @Test
    void procesoDePago_conDatosInvalidos_lanzaPagoInvalidoYNoCobra() {
        when(api.validarCVV(NUMERO_TARJETA, CVV)).thenReturn(false);

        assertThrows(ExcepcionPagoInvalido.class, tarjeta::procesoDePago);

        verify(api, never()).preAutorizar(anyInt(), anyDouble());
        verify(api, never()).transferirInmediata(anyDouble());
        assertTrue(tarjeta.getComprobantes().isEmpty());
    }
}
