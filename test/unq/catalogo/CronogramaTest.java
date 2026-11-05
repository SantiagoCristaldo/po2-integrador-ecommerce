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
    void unaFechaSinDefinirTieneElCupoPorDefecto() {
        assertEquals(10, cronograma.cupoEn(DIA_1));
    }

    @Test
    void definirCupo_cambiaSoloElCupoDeEsaFecha() {
        cronograma.definirCupo(DIA_1, 3);

        assertEquals(3, cronograma.cupoEn(DIA_1));
        assertEquals(10, cronograma.cupoEn(DIA_2));
    }

    @Test
    void definirCupo_negativo_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> cronograma.definirCupo(DIA_1, -1));
    }

    @Test
    void decrementar_descuentaCadaFechaDelRangoIncluyendoInicioYFin() {
        cronograma.decrementar(DIA_1, DIA_3, 2);

        assertEquals(8, cronograma.cupoEn(DIA_1));
        assertEquals(8, cronograma.cupoEn(DIA_2));
        assertEquals(8, cronograma.cupoEn(DIA_3));
    }

    @Test
    void decrementar_noTocaLasFechasFueraDelRango() {
        cronograma.decrementar(DIA_2, DIA_3, 2);

        assertEquals(10, cronograma.cupoEn(DIA_1));
        assertEquals(10, cronograma.cupoEn(DIA_4));
    }

    @Test
    void decrementar_conInicioIgualAFin_descuentaUnaSolaFecha() {
        cronograma.decrementar(DIA_1, DIA_1, 1);

        assertEquals(9, cronograma.cupoEn(DIA_1));
        assertEquals(10, cronograma.cupoEn(DIA_2));
    }

    @Test
    void decrementar_hastaAgotarElCupo_loDejaEnCero() {
        cronograma.decrementar(DIA_1, DIA_1, 10);

        assertEquals(0, cronograma.cupoEn(DIA_1));
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
    void decrementar_sinCupoEnUnaFecha_lanzaExcepcionYNoDescuentaNinguna() {
        cronograma.definirCupo(DIA_2, 1);

        assertThrows(ExcepcionDominio.class, () -> cronograma.decrementar(DIA_1, DIA_3, 2));

        assertEquals(10, cronograma.cupoEn(DIA_1));
        assertEquals(1, cronograma.cupoEn(DIA_2));
        assertEquals(10, cronograma.cupoEn(DIA_3));
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
    void tieneCupo_conCupoExactoEnTodoElRango_esVerdadero() {
        cronograma.definirCupo(DIA_2, 2);

        assertTrue(cronograma.tieneCupo(DIA_1, DIA_3, 2));
    }

    @Test
    void tieneCupo_conUnaFechaInsuficiente_esFalso() {
        cronograma.definirCupo(DIA_2, 1);

        assertFalse(cronograma.tieneCupo(DIA_1, DIA_3, 2));
    }

    @Test
    void cuposTotalesEntre_sumaElCupoDeCadaFechaDelRango() {
        cronograma.definirCupo(DIA_2, 3);

        assertEquals(23, cronograma.cuposTotalesEntre(DIA_1, DIA_3));
    }

    @Test
    void cuposTotalesEntre_noCambiaAlReservarNiAlLiberar() {
        cronograma.decrementar(DIA_1, DIA_2, 4);
        assertEquals(30, cronograma.cuposTotalesEntre(DIA_1, DIA_3));

        cronograma.incrementar(DIA_1, DIA_2, 4);
        assertEquals(30, cronograma.cuposTotalesEntre(DIA_1, DIA_3));
    }

    @Test
    void cuposReservadosEntre_sinReservas_esCero() {
        assertEquals(0, cronograma.cuposReservadosEntre(DIA_1, DIA_3));
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

    @Test
    void definirCupo_conReservas_mantieneLoReservadoYCambiaLoLibre() {
        cronograma.decrementar(DIA_1, DIA_1, 4);

        cronograma.definirCupo(DIA_1, 2);

        assertEquals(2, cronograma.cupoEn(DIA_1));
        assertEquals(4, cronograma.cuposReservadosEntre(DIA_1, DIA_1));
        assertEquals(6, cronograma.cuposTotalesEntre(DIA_1, DIA_1));
    }

    @Test
    void cupoMaximo_sinFechasDefinidas_esElCupoPorDefecto() {
        assertEquals(10, cronograma.cupoMaximo());
    }

    @Test
    void cupoMaximo_conDefectoEnCero_esElMayorCupoDefinido() {
        Cronograma temporada = new Cronograma(0);
        temporada.definirCupo(DIA_1, 3);
        temporada.definirCupo(DIA_2, 5);

        assertEquals(5, temporada.cupoMaximo());
    }

    @Test
    void cupoMaximo_conLaUnicaFechaConCupoAgotada_esCero() {
        Cronograma temporada = new Cronograma(0);
        temporada.definirCupo(DIA_1, 2);

        temporada.decrementar(DIA_1, DIA_1, 2);

        assertEquals(0, temporada.cupoMaximo());
    }
}
