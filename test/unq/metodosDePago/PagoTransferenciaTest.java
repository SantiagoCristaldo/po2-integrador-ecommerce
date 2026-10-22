package unq.metodosDePago;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PagoTransferenciaTest {

    private static final int CBU = 12345678;
    private static final String ALIAS = "alias.unqui.transferencia";
    private static final float MONTO = 15000f;

    private APIBancaria api;
    private PagoTransferencia pago;

    @BeforeEach
    void setUp() {
        api = mock(APIBancaria.class);
        pago = new PagoTransferencia(CBU, ALIAS, LocalDateTime.now().plusDays(1), api, MONTO, "TX-1002");
    }

    @Test
    void validarDatos_delegaLaValidacionEnLaApi() {
        when(api.validarDatos(pago)).thenReturn(true);

        assertTrue(pago.validarDatos());

        verify(api).validarDatos(pago);
    }

    @Test
    void validarDatos_cuandoLaApiRechazaLosDatos_devuelveFalse() {
        when(api.validarDatos(pago)).thenReturn(false);

        assertFalse(pago.validarDatos());
    }

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

    @Test
    void notificarResultado_noGeneraElComprobanteConElNumeroDeOperacion() {
        pago.notificarResultado();

        verifyNoInteractions(api);
    }
}
