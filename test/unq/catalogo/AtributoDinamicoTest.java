package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;

class AtributoDinamicoTest {

    // Decisión de diseño: la obligatoriedad y el tipo se chequean al asignar el valor (constructor y
    // setValor), así que un atributo obligatorio nunca queda sin valor. Uno opcional sí puede.
    @Test
    void constructor_obligatorioSinValor_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class,
                () -> new AtributoDinamico("CapacidadMaxima", true, new Numero(), null));
    }

    @Test
    void constructor_noObligatorioSinValor_noLanza() {
        assertDoesNotThrow(() -> new AtributoDinamico("PoliticaMascotas", false, new Texto(), null));
    }

    @Test
    void constructor_conValorDelTipoCorrecto_noLanza() {
        assertDoesNotThrow(() -> new AtributoDinamico("RatingEstrellas", true, new Numero(), "5"));
        assertDoesNotThrow(() -> new AtributoDinamico("RatingEstrellas", true, new Numero(), "4.5"));
        assertDoesNotThrow(() -> new AtributoDinamico("Politica", true, new Texto(), ""));
        assertDoesNotThrow(() -> new AtributoDinamico("Salida", true, new Fecha(), "2026-11-12"));
        assertDoesNotThrow(() -> new AtributoDinamico("Mascotas", true, new Booleano(), "true"));
    }

    @Test
    void constructor_conValorDeOtroTipo_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class,
                () -> new AtributoDinamico("RatingEstrellas", true, new Numero(), "cinco"));
        assertThrows(ExcepcionDominio.class,
                () -> new AtributoDinamico("Salida", true, new Fecha(), "12/11/2026"));
        assertThrows(ExcepcionDominio.class,
                () -> new AtributoDinamico("Mascotas", true, new Booleano(), "si"));
    }

    @Test
    void setValor_conValorDeOtroTipo_lanzaExcepcionDeDominio() {
        AtributoDinamico atributo = new AtributoDinamico("RatingEstrellas", true, new Numero(), "5");

        assertThrows(ExcepcionDominio.class, () -> atributo.setValor("cinco"));
    }

    @Test
    void setValor_nuloEnObligatorio_lanzaExcepcionDeDominio() {
        AtributoDinamico atributo = new AtributoDinamico("RatingEstrellas", true, new Numero(), "5");

        assertThrows(ExcepcionDominio.class, () -> atributo.setValor(null));
    }

    @Test
    void setValor_conValorDelTipoCorrecto_noLanza() {
        AtributoDinamico atributo = new AtributoDinamico("RatingEstrellas", true, new Numero(), "5");

        assertDoesNotThrow(() -> atributo.setValor("3"));
    }

    @Test
    void tieneNombre_conElMismoNombre_esVerdadero() {
        AtributoDinamico atributo = new AtributoDinamico("CapacidadMaxima", false, new Numero(), "10");

        assertTrue(atributo.tieneNombre("CapacidadMaxima"));
    }

    @Test
    void tieneNombre_conOtroNombre_esFalso() {
        AtributoDinamico atributo = new AtributoDinamico("CapacidadMaxima", false, new Numero(), "10");

        assertFalse(atributo.tieneNombre("PoliticaMascotas"));
    }

    @Test
    void tieneNombre_conNombreInternoNulo_esFalso() {
        AtributoDinamico atributo = new AtributoDinamico(null, false, new Numero(), "10");

        assertFalse(atributo.tieneNombre("CapacidadMaxima"));
    }

    @Test
    void mismoNombreQue_conAtributosHomónimos_esVerdadero() {
        AtributoDinamico uno = new AtributoDinamico("CapacidadMaxima", false, new Numero(), "10");
        AtributoDinamico otro = new AtributoDinamico("CapacidadMaxima", false, new Numero(), "10");

        assertTrue(uno.mismoNombreQue(otro));
    }

    @Test
    void mismoNombreQue_conDistintoNombre_esFalso() {
        AtributoDinamico uno = new AtributoDinamico("CapacidadMaxima", false, new Numero(), "10");
        AtributoDinamico otro = new AtributoDinamico("PoliticaMascotas", false, new Numero(), "10");

        assertFalse(uno.mismoNombreQue(otro));
    }
}
