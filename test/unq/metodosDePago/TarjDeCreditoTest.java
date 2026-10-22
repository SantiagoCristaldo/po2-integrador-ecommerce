package unq.metodosDePago;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class TarjDeCreditoTest {

    private static final int NUMERO_TARJETA = 4539;
    private static final int CVV = 123;
    private static final float MONTO = 25000f;

    private APITarjetaDeCredito api;
    private TarjDeCredito tarjeta;

    @BeforeEach
    void setUp() {
        api = mock(APITarjetaDeCredito.class);
        tarjeta = new TarjDeCredito(NUMERO_TARJETA, CVV, LocalDateTime.now().plusYears(2), MONTO, "TX-1001");
        inyectarApi(tarjeta, api);
    }

    /**
     * La tarjeta no recibe la API por constructor (ver H24 en HALLAZGOS.md), asi que no hay forma de
     * inyectarla sin tocar produccion. Se usa reflexion sobre el campo privado.
     */
    private static void inyectarApi(TarjDeCredito tarjeta, APITarjetaDeCredito api) {
        try {
            Field campo = TarjDeCredito.class.getDeclaredField("api");
            campo.setAccessible(true);
            campo.set(tarjeta, api);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo inyectar la API en la tarjeta de credito", e);
        }
    }

    @Test
    void validarDatos_delegaLaValidacionEnLaApi() {
        when(api.validarDatos(tarjeta)).thenReturn(true);

        assertTrue(tarjeta.validarDatos());

        verify(api).validarDatos(tarjeta);
    }

    @Test
    void validarDatos_cuandoLaApiRechazaLosDatos_devuelveFalse() {
        when(api.validarDatos(tarjeta)).thenReturn(false);

        assertFalse(tarjeta.validarDatos());
    }

    @Test
    void reservarFondos_haceLaPreAutorizacionDelMontoEnLaTarjeta() {
        tarjeta.reservarFondos();

        verify(api).preAutorizar(NUMERO_TARJETA, MONTO);
    }

    @Test
    void ejecutarTransaccion_transfiereElMontoDeInmediato() {
        tarjeta.ejecutarTransaccion();

        verify(api).transferirInmediata(MONTO);
    }

    @Test
    void notificarResultado_noGeneraElVoucherQuePideElEnunciado() {
        tarjeta.notificarResultado();

        verifyNoInteractions(api);
    }

    @Test
    void procesoDePago_conDatosValidos_validaPreAutorizaYTransfiereEnEseOrden() {
        when(api.validarDatos(tarjeta)).thenReturn(true);

        tarjeta.procesoDePago();

        InOrder orden = inOrder(api);
        orden.verify(api).validarDatos(tarjeta);
        orden.verify(api).preAutorizar(NUMERO_TARJETA, MONTO);
        orden.verify(api).transferirInmediata(MONTO);
    }
}
