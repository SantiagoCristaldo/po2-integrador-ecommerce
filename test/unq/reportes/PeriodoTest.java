package unq.reportes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;

class PeriodoTest {

    private LocalDate desde;
    private LocalDate hasta;
    private Periodo periodo;

    @BeforeEach
    void setUp() {
        desde = LocalDate.of(2026, 1, 1);
        hasta = LocalDate.of(2026, 1, 31);
        periodo = new Periodo(desde, hasta);
    }

    @Test
    void constructor_conDesdePosteriorAHasta_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> new Periodo(hasta, desde));
    }

    @Test
    void constructor_conDesdeIgualAHasta_esValido() {
        assertDoesNotThrow(() -> new Periodo(desde, desde));
    }

    @Test
    void getters_devuelvenLasFechasDelConstructor() {
        assertEquals(desde, periodo.getDesde());
        assertEquals(hasta, periodo.getHasta());
    }

    @Test
    void contiene_fechaIntermedia_devuelveTrue() {
        assertTrue(periodo.contiene(LocalDate.of(2026, 1, 15)));
    }

    @Test
    void contiene_incluyeLosExtremos() {
        assertTrue(periodo.contiene(desde));
        assertTrue(periodo.contiene(hasta));
    }

    @Test
    void contiene_fechaAnterior_devuelveFalse() {
        assertFalse(periodo.contiene(LocalDate.of(2025, 12, 31)));
    }

    @Test
    void contiene_fechaPosterior_devuelveFalse() {
        assertFalse(periodo.contiene(LocalDate.of(2026, 2, 1)));
    }
}
