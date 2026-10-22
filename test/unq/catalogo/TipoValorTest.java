package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

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
    void texto_conOtroTipo_esInvalido() {
        assertFalse(texto.esValido(10));
    }

    @Test
    void texto_conNull_esInvalido() {
        assertFalse(texto.esValido(null));
    }

    @Test
    void numero_conEntero_esValido() {
        assertTrue(numero.esValido(10));
    }

    @Test
    void numero_conDecimal_esValido() {
        assertTrue(numero.esValido(10.5));
    }

    @Test
    void numero_conTexto_esInvalido() {
        assertFalse(numero.esValido("10"));
    }

    @Test
    void numero_conNull_esInvalido() {
        assertFalse(numero.esValido(null));
    }

    @Test
    void fecha_conLocalDate_esValida() {
        assertTrue(fecha.esValido(LocalDate.of(2026, 11, 12)));
    }

    @Test
    void fecha_conTexto_esInvalida() {
        assertFalse(fecha.esValido("2026-11-12"));
    }

    @Test
    void fecha_conNull_esInvalida() {
        assertFalse(fecha.esValido(null));
    }

    @Test
    void booleano_conBoolean_esValido() {
        assertTrue(booleano.esValido(true));
    }

    @Test
    void booleano_conTexto_esInvalido() {
        assertFalse(booleano.esValido("true"));
    }

    @Test
    void booleano_conNull_esInvalido() {
        assertFalse(booleano.esValido(null));
    }
}
