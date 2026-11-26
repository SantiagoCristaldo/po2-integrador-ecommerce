package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;

class CronogramaTest {

    private static final LocalDate DIA_1 = LocalDate.of(2027, 1, 1);
    private static final LocalDate DIA_2 = LocalDate.of(2027, 1, 2);
    private static final LocalDate DIA_3 = LocalDate.of(2027, 1, 3);
    private static final LocalDate DIA_4 = LocalDate.of(2027, 1, 4);

    private Cronograma cronograma;

    @BeforeEach
    void setUp() {
        cronograma = new Cronograma(10);
    }

    @Test
    void sinReservas_todasLasFechasTienenElCupoCompleto() {
        assertEquals(10, cronograma.getCupo());
        assertEquals(10, cronograma.cupoEn(DIA_1));
    }

    @Test
    void decrementar_descuentaCadaFechaDelRangoIncluyendoInicioYFin() {
        cronograma.decrementar(DIA_1, DIA_3, 2);

        assertEquals(8, cronograma.cupoEn(DIA_1));
        assertEquals(8, cronograma.cupoEn(DIA_2));
        assertEquals(8, cronograma.cupoEn(DIA_3));
        assertEquals(10, cronograma.cupoEn(DIA_4));
    }

    @Test
    void decrementar_seAcumulaEnFechasSolapadas() {
        cronograma.decrementar(DIA_1, DIA_2, 2);
        cronograma.decrementar(DIA_2, DIA_3, 3);

        assertEquals(8, cronograma.cupoEn(DIA_1));
        assertEquals(5, cronograma.cupoEn(DIA_2));
        assertEquals(7, cronograma.cupoEn(DIA_3));
    }

    @Test
    void decrementar_sinCupoSuficiente_lanzaExcepcionYNoDescuenta() {
        assertThrows(ExcepcionDominio.class, () -> cronograma.decrementar(DIA_1, DIA_2, 11));

        assertEquals(10, cronograma.cupoEn(DIA_1));
    }

    @Test
    void decrementar_conFinAnteriorAlInicio_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> cronograma.decrementar(DIA_2, DIA_1, 1));
    }

    @Test
    void incrementar_devuelveElCupoDelRango() {
        cronograma.decrementar(DIA_1, DIA_2, 4);

        cronograma.incrementar(DIA_1, DIA_2, 4);

        assertEquals(10, cronograma.cupoEn(DIA_1));
        assertEquals(10, cronograma.cupoEn(DIA_2));
    }

    @Test
    void tieneCupo_conCupoExacto_esVerdadero() {
        assertTrue(cronograma.tieneCupo(DIA_1, DIA_3, 10));
    }

    @Test
    void tieneCupo_conUnaFechaAgotada_esFalso() {
        cronograma.decrementar(DIA_2, DIA_2, 9);

        assertFalse(cronograma.tieneCupo(DIA_1, DIA_3, 2));
    }

    @Test
    void cuposTotalesEntre_esElCupoPorLaCantidadDeDias() {
        assertEquals(30, cronograma.cuposTotalesEntre(DIA_1, DIA_3));
    }

    @Test
    void cuposReservadosEntre_sumaSoloLasFechasDelRango() {
        cronograma.decrementar(DIA_1, DIA_3, 2);

        assertEquals(4, cronograma.cuposReservadosEntre(DIA_2, DIA_4));
    }

    @Test
    void cuposReservadosEntre_descuentaLoLiberado() {
        cronograma.decrementar(DIA_1, DIA_2, 4);
        cronograma.incrementar(DIA_1, DIA_2, 1);

        assertEquals(6, cronograma.cuposReservadosEntre(DIA_1, DIA_2));
    }
}
