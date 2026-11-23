package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TipoValorTest {

    private final Texto texto = new Texto();
    private final Numero numero = new Numero();
    private final Fecha fecha = new Fecha();
    private final Booleano booleano = new Booleano();

    @Test
    void texto_conString_esValido() {
        assertTrue(texto.esValido("hola"));
    }

    @Test
    void texto_conCadenaVacia_esValidoPorqueSoloSeValidaElTipo() {
        assertTrue(texto.esValido(""));
    }

    @Test
    void texto_conNull_esInvalido() {
        assertFalse(texto.esValido(null));
    }

    @Test
    void numero_conEntero_esValido() {
        assertTrue(numero.esValido("10"));
    }

    @Test
    void numero_conDecimal_esValido() {
        assertTrue(numero.esValido("10.5"));
    }

    @Test
    void numero_conTextoNoNumerico_esInvalido() {
        assertFalse(numero.esValido("diez"));
    }

    @Test
    void numero_conNull_esInvalido() {
        assertFalse(numero.esValido(null));
    }

    @Test
    void fecha_conFormatoIso_esValida() {
        assertTrue(fecha.esValido("2026-11-12"));
    }

    @Test
    void fecha_conOtroFormato_esInvalida() {
        assertFalse(fecha.esValido("12/11/2026"));
    }

    @Test
    void fecha_conNull_esInvalida() {
        assertFalse(fecha.esValido(null));
    }

    @Test
    void booleano_conTrueOFalse_esValido() {
        assertTrue(booleano.esValido("true"));
        assertTrue(booleano.esValido("false"));
        assertTrue(booleano.esValido("TRUE"));
    }

    @Test
    void booleano_conOtroTexto_esInvalido() {
        assertFalse(booleano.esValido("si"));
    }

    @Test
    void booleano_conNull_esInvalido() {
        assertFalse(booleano.esValido(null));
    }
}
