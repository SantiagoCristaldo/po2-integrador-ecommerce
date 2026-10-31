package unq.metodosDePago;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionPagoInvalido;

class PagoTransferenciaTest {

    private static final int CBU = 12345678;
    private static final String ALIAS = "alias.unqui.transferencia";
    private static final double MONTO = 15000.0;

    private APIBancaria api;
    private PagoTransferencia pago;

    @BeforeEach
    void setUp() {
        api = mock(APIBancaria.class);
        pago = new PagoTransferencia(CBU, ALIAS, LocalDateTime.now().plusDays(1), api, MONTO, "TX-1002");
    }

    // --- Validar datos: CBU/CVU y alias ---

    @Test
    void validarDatos_conCBUYAliasValidos_esVerdadero() {
        when(api.validarCBU(CBU)).thenReturn(true);
        when(api.validarAlias(ALIAS)).thenReturn(true);

        assertTrue(pago.validarDatos());
    }

    @Test
    void validarDatos_conCBUInvalido_esFalso() {
        when(api.validarCBU(CBU)).thenReturn(false);
        when(api.validarAlias(ALIAS)).thenReturn(true);

        assertFalse(pago.validarDatos());
    }

    @Test
    void validarDatos_conAliasInvalido_esFalso() {
        when(api.validarCBU(CBU)).thenReturn(true);
        when(api.validarAlias(ALIAS)).thenReturn(false);

        assertFalse(pago.validarDatos());
    }

    // --- Reservar fondos: no aplica ---

    @Test
    void reservarFondos_noInteractuaConLaApi() {
        pago.reservarFondos();

        verifyNoInteractions(api);
    }

    @Test
    void ejecutarTransaccion_transfiereDeInmediatoElMontoALaCBU() {
        pago.ejecutarTransaccion();

        verify(api).transferirDeInmediato(CBU, MONTO);
    }

    // --- Notificar: comprobante CBU con numero de operacion, generado y registrado ---

    @Test
    void notificarResultado_generaYRegistraElComprobanteCBUConElNumeroDeOperacion() {
        pago.notificarResultado();

        assertEquals(1, pago.getComprobantes().size());
        ComprobantePago comprobante = pago.getComprobantes().get(0);
        assertTrue(comprobante.getTipo().contains("CBU"));
        assertEquals("TX-1002", comprobante.getCodigoTransaccion());
        assertEquals(MONTO, comprobante.getMonto(), 0.001);
    }

    @Test
    void procesoDePago_conDatosInvalidos_lanzaPagoInvalidoYNoTransfiere() {
        when(api.validarCBU(CBU)).thenReturn(false);

        assertThrows(ExcepcionPagoInvalido.class, pago::procesoDePago);

        verify(api, never()).transferirDeInmediato(anyInt(), anyDouble());
        assertTrue(pago.getComprobantes().isEmpty());
    }
}
