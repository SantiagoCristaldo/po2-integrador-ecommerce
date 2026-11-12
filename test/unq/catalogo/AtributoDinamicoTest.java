package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;

class AtributoDinamicoTest {

    // La obligatoriedad no se chequea al definir el atributo sino al validar el servicio
    // (ver ServicioIndividualTest#validar_conAtributoDinamicoObligatorioSinValor_lanzaExcepcionDeDominio).
    @Test
    void constructor_obligatorioSinValor_noLanza() {
        assertDoesNotThrow(() -> new AtributoDinamico("CapacidadMaxima", true, new Numero(), null));
    }

    @Test
    void constructor_noObligatorioSinValor_noLanza() {
        assertDoesNotThrow(() -> new AtributoDinamico("PoliticaMascotas", false, new Texto(), null));
    }

    @Test
    void constructor_conValorDelTipoCorrecto_noLanza() {
        assertDoesNotThrow(() -> new AtributoDinamico("RatingEstrellas", true, new Numero(), 5));
        assertDoesNotThrow(() -> new AtributoDinamico("RatingEstrellas", true, new Numero(), 4.5));
        assertDoesNotThrow(() -> new AtributoDinamico("Politica", true, new Texto(), ""));
        assertDoesNotThrow(() -> new AtributoDinamico("Salida", true, new Fecha(), LocalDate.of(2026, 11, 12)));
        assertDoesNotThrow(() -> new AtributoDinamico("Mascotas", true, new Booleano(), true));
    }

    @Test
    void constructor_conValorDeOtroTipo_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class,
                () -> new AtributoDinamico("RatingEstrellas", true, new Numero(), "cinco"));
        assertThrows(ExcepcionDominio.class,
                () -> new AtributoDinamico("Salida", true, new Fecha(), "2026-11-12"));
        assertThrows(ExcepcionDominio.class,
                () -> new AtributoDinamico("Mascotas", true, new Booleano(), "true"));
    }

    @Test
    void setValor_conValorDeOtroTipo_lanzaExcepcionDeDominio() {
        AtributoDinamico atributo = new AtributoDinamico("RatingEstrellas", true, new Numero(), 5);

        assertThrows(ExcepcionDominio.class, () -> atributo.setValor("cinco"));
    }

    @Test
    void setValor_nuloEnObligatorio_noLanza_loDetectaLaValidacionDelServicio() {
        AtributoDinamico atributo = new AtributoDinamico("RatingEstrellas", true, new Numero(), 5);

        assertDoesNotThrow(() -> atributo.setValor(null));
    }

    @Test
    void setValor_conValorDelTipoCorrecto_noLanza() {
        AtributoDinamico atributo = new AtributoDinamico("RatingEstrellas", true, new Numero(), 5);

        assertDoesNotThrow(() -> atributo.setValor(3));
    }

    @Test
    void tieneNombre_conElMismoNombre_esVerdadero() {
        AtributoDinamico atributo = new AtributoDinamico("CapacidadMaxima", false, new Numero(), null);

        assertTrue(atributo.tieneNombre("CapacidadMaxima"));
    }

    @Test
    void tieneNombre_conOtroNombre_esFalso() {
        AtributoDinamico atributo = new AtributoDinamico("CapacidadMaxima", false, new Numero(), null);

        assertFalse(atributo.tieneNombre("PoliticaMascotas"));
    }

    @Test
    void tieneNombre_conNombreInternoNulo_esFalso() {
        AtributoDinamico atributo = new AtributoDinamico(null, false, new Numero(), null);

        assertFalse(atributo.tieneNombre("CapacidadMaxima"));
    }

    @Test
    void mismoNombreQue_conAtributosHomónimos_esVerdadero() {
        AtributoDinamico uno = new AtributoDinamico("CapacidadMaxima", false, new Numero(), null);
        AtributoDinamico otro = new AtributoDinamico("CapacidadMaxima", false, new Numero(), null);

        assertTrue(uno.mismoNombreQue(otro));
    }

    @Test
    void mismoNombreQue_conDistintoNombre_esFalso() {
        AtributoDinamico uno = new AtributoDinamico("CapacidadMaxima", false, new Numero(), null);
        AtributoDinamico otro = new AtributoDinamico("PoliticaMascotas", false, new Numero(), null);

        assertFalse(uno.mismoNombreQue(otro));
    }
}
