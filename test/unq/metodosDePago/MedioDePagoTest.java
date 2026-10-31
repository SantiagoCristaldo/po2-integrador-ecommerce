package unq.metodosDePago;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import unq.excepciones.ExcepcionDominio;
import unq.excepciones.ExcepcionPagoInvalido;

/**
 * Tests de la plantilla (Template Method) de {@link MedioDePago}.
 *
 * <p>La plantilla se prueba con un <b>spy</b> sobre una subclase mínima declarada en estos tests
 * (no existe una subclase "concreta" que sirva para todos los casos: cada medio implementa los pasos
 * con su propia API) y con {@link InOrder} para verificar el orden de los pasos.</p>
 */
class MedioDePagoTest {

    /** Subclase mínima y sin colaboradores, usada sólo para poder espiar la plantilla. */
    static class MedioDePagoMinimo extends MedioDePago {

        MedioDePagoMinimo(float monto, String codigoTransaccion) {
            super(monto, codigoTransaccion);
        }

        @Override
        public boolean validarDatos() {
            return true;
        }

        @Override
        public void reservarFondos() {
        }

        @Override
        public void ejecutarTransaccion() {
        }
    }

    private MedioDePagoMinimo medio;

    @BeforeEach
    void setUp() {
        medio = spy(new MedioDePagoMinimo(1000f, "TX-0001"));
    }

    // --- La plantilla ---

    @Test
    void procesoDePago_ejecutaLosPasosEnElOrdenDelEnunciado() {
        medio.procesoDePago();

        InOrder orden = inOrder(medio);
        orden.verify(medio).validarDatos();
        orden.verify(medio).reservarFondos();
        orden.verify(medio).ejecutarTransaccion();
        orden.verify(medio).notificarResultado();
    }

    @Test
    void procesoDePago_conDatosValidos_ejecutaTodosLosPasos() {
        medio.procesoDePago();

        verify(medio).validarDatos();
        verify(medio).reservarFondos();
        verify(medio).ejecutarTransaccion();
        verify(medio).notificarResultado();
    }

    @Test
    void procesoDePago_conDatosInvalidos_lanzaLaExcepcionDePagoInvalido() {
        doReturn(false).when(medio).validarDatos();

        assertThrows(ExcepcionPagoInvalido.class, medio::procesoDePago);
    }

    @Test
    void laExcepcionDePagoInvalidoEsUnaExcepcionDeDominio() {
        doReturn(false).when(medio).validarDatos();

        assertThrows(ExcepcionDominio.class, medio::procesoDePago);
    }

    @Test
    void procesoDePago_conDatosInvalidos_noEjecutaNingunPasoPosterior() {
        doReturn(false).when(medio).validarDatos();

        assertThrows(ExcepcionDominio.class, medio::procesoDePago);

        verify(medio, never()).reservarFondos();
        verify(medio, never()).ejecutarTransaccion();
        verify(medio, never()).notificarResultado();
    }

    @Test
    void elMontoQuedaGuardadoEnElMedioDePago() {
        assertEquals(1000.0, medio.getMonto(), 0.001);
    }

    @Test
    void setMonto_reemplazaElMontoACobrar() {
        medio.setMonto(2500.0);

        assertEquals(2500.0, medio.getMonto(), 0.001);
    }

    @Test
    void elMontoNoSePuedeModificarDesdeAfuera() throws Exception {
        assertTrue(Modifier.isPrivate(MedioDePago.class.getDeclaredField("monto").getModifiers()),
                "el monto deberia ser privado: solo se cambia con setMonto");
    }

    // --- Notificacion por defecto: registro del codigo de transaccion ---

    @Test
    void notificarResultado_porDefecto_registraElCodigoDeTransaccion() {
        medio.notificarResultado();

        assertEquals(1, medio.getComprobantes().size());
        assertEquals("TX-0001", medio.getComprobantes().get(0).getCodigoTransaccion());
        assertEquals(1000.0, medio.getComprobantes().get(0).getMonto(), 0.001);
    }

    @Test
    void procesoDePago_conDatosValidos_dejaRegistradaLaTransaccion() {
        medio.procesoDePago();

        assertEquals(1, medio.getComprobantes().size());
    }

    @Test
    void procesoDePago_conDatosInvalidos_noRegistraLaTransaccion() {
        doReturn(false).when(medio).validarDatos();

        assertThrows(ExcepcionPagoInvalido.class, medio::procesoDePago);

        assertTrue(medio.getComprobantes().isEmpty());
    }

    @Test
    void getComprobantes_devuelveUnaCopia() {
        medio.notificarResultado();

        medio.getComprobantes().clear();

        assertEquals(1, medio.getComprobantes().size());
    }

    // --- Garantías del patrón ---

    @Test
    void procesoDePago_esFinalParaQueNingunaSubclasePuedaCambiarElOrdenDeLosPasos() throws Exception {
        Method metodo = MedioDePago.class.getDeclaredMethod("procesoDePago");

        assertTrue(Modifier.isFinal(metodo.getModifiers()),
                "procesoDePago() deberia ser final: es el metodo plantilla y no debe poder ser sobrescrito");
    }

    @Test
    void notificarResultado_tieneUnaImplementacionPorDefectoEnLaClaseAbstracta() throws Exception {
        Method metodo = MedioDePago.class.getDeclaredMethod("notificarResultado");

        assertFalse(Modifier.isAbstract(metodo.getModifiers()),
                "notificarResultado() deberia tener una implementacion por defecto (registro del codigo de transaccion)");
    }
}
