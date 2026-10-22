package unq.metodosDePago;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class BilleteraVirtualTest {

    private static final String ID_CUENTA = "cuenta-123";
    private static final float MONTO = 8000f;

    private APIBilletera api;
    private BilleteraVirtual billetera;

    @BeforeEach
    void setUp() {
        api = mock(APIBilletera.class);
        billetera = new BilleteraVirtual(MONTO, api, ID_CUENTA, "TX-1003");
    }

    @Test
    void validarDatos_delegaLaValidacionEnLaApi() {
        when(api.validarDatos(billetera)).thenReturn(true);

        assertTrue(billetera.validarDatos());

        verify(api).validarDatos(billetera);
    }

    @Test
    void validarDatos_cuandoLaApiRechazaLosDatos_devuelveFalse() {
        when(api.validarDatos(billetera)).thenReturn(false);

        assertFalse(billetera.validarDatos());
    }

    @Test
    void reservarFondos_bloqueaElSaldoDeLaCuenta() {
        billetera.reservarFondos();

        verify(api).bloquearElSaldo(ID_CUENTA, MONTO);
    }

    @Test
    void ejecutarTransaccion_acreditaElMontoAlVendedor() {
        billetera.ejecutarTransaccion();

        verify(api).acreditarVendedor(ID_CUENTA, MONTO);
    }

    @Test
    void notificarResultado_enviaLaNotificacionPushAlTitular() {
        billetera.notificarResultado();

        verify(api).notificarPush(ID_CUENTA, MONTO);
    }

    @Test
    void procesoDePago_conDatosValidos_bloqueaAcreditaYNotificaEnEseOrden() {
        when(api.validarDatos(billetera)).thenReturn(true);

        billetera.procesoDePago();

        InOrder orden = inOrder(api);
        orden.verify(api).validarDatos(billetera);
        orden.verify(api).bloquearElSaldo(ID_CUENTA, MONTO);
        orden.verify(api).acreditarVendedor(ID_CUENTA, MONTO);
        orden.verify(api).notificarPush(ID_CUENTA, MONTO);
    }
}
