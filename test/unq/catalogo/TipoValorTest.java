package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TipoValorTest {

    @Test
    void texto_aceptaCualquierString_inclusoVacio() {
        Texto texto = new Texto();

        assertTrue(texto.esValido("hola"));
        assertTrue(texto.esValido(""));
        assertFalse(texto.esValido(50));
    }

    @Test
    void numero_aceptaNumbers() {
        Numero numero = new Numero();

        assertTrue(numero.esValido(50));
        assertTrue(numero.esValido(1.5));
        assertFalse(numero.esValido("50"));
    }

    @Test
    void booleano_aceptaBooleans() {
        Booleano booleano = new Booleano();

        assertTrue(booleano.esValido(true));
        assertFalse(booleano.esValido("true"));
    }

    @Test
    void fecha_aceptaLocalDates() {
        Fecha fecha = new Fecha();

        assertTrue(fecha.esValido(LocalDate.now()));
        assertFalse(fecha.esValido("2026-01-01"));
    }
}
