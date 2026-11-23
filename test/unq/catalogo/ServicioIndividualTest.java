package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;

class ServicioIndividualTest {

    private ServicioIndividual servicio;

    @BeforeEach
    void setUp() {
        servicio = servicioValido();
    }

    private ServicioIndividual servicioValido() {
        return new ServicioIndividual("HOT-001", "Hotel Central", "Un hotel céntrico",
                "Operador SA", new Categoria("Alojamiento"), 15000.0, 10);
    }

    @Test
    void validar_conCodigoYNombrePresentesYSinDinamicos_noLanza() {
        assertDoesNotThrow(servicio::validar);
    }

    @Test
    void validar_sinCodigo_lanzaExcepcionDeDominio() {
        ServicioIndividual sinCodigo = new ServicioIndividual(null, "Hotel", "d",
                "op", new Categoria("Alojamiento"), 1.0, 10);

        assertThrows(ExcepcionDominio.class, sinCodigo::validar);
    }

    @Test
    void validar_conCodigoEnBlanco_lanzaExcepcionDeDominio() {
        ServicioIndividual codigoEnBlanco = new ServicioIndividual("   ", "Hotel", "d",
                "op", new Categoria("Alojamiento"), 1.0, 10);

        assertThrows(ExcepcionDominio.class, codigoEnBlanco::validar);
    }

    @Test
    void validar_sinNombre_lanzaExcepcionDeDominio() {
        ServicioIndividual sinNombre = new ServicioIndividual("HOT-001", null, "d",
                "op", new Categoria("Alojamiento"), 1.0, 10);

        assertThrows(ExcepcionDominio.class, sinNombre::validar);
    }

    @Test
    void validar_sinOperador_lanzaExcepcionDeDominio() {
        ServicioIndividual sinOperador = new ServicioIndividual("HOT-001", "Hotel", "d",
                null, new Categoria("Alojamiento"), 1.0, 10);

        assertThrows(ExcepcionDominio.class, sinOperador::validar);
    }

    @Test
    void validar_sinCategoria_lanzaExcepcionDeDominio() {
        ServicioIndividual sinCategoria = new ServicioIndividual("HOT-001", "Hotel", "d",
                "op", null, 1.0, 10);

        assertThrows(ExcepcionDominio.class, sinCategoria::validar);
    }

    @Test
    void setValorAtributo_delegaSoloEnElAtributoConEseNombre() {
        AtributoDinamico hotel = mock(AtributoDinamico.class);
        AtributoDinamico vuelo = mock(AtributoDinamico.class);
        when(hotel.tieneNombre("CapacidadMaxima")).thenReturn(true);
        when(vuelo.tieneNombre("CapacidadMaxima")).thenReturn(false);
        servicio.agregarAtributoDinamico(hotel);
        servicio.agregarAtributoDinamico(vuelo);

        servicio.setValorAtributo("CapacidadMaxima", "10");

        verify(hotel).setValor("10");
        verify(vuelo, never()).setValor(any());
    }

    @Test
    void setValorAtributo_conNombreInexistente_lanzaYNoModificaNinguno() {
        AtributoDinamico hotel = mock(AtributoDinamico.class);
        servicio.agregarAtributoDinamico(hotel);

        assertThrows(ExcepcionDominio.class,
                () -> servicio.setValorAtributo("Inexistente", "1"));

        verify(hotel, never()).setValor(any());
    }

    @Test
    void agregarAtributoDinamico_conNombreRepetido_lanzaExcepcionDeDominio() {
        AtributoDinamico original = mock(AtributoDinamico.class);
        AtributoDinamico repetido = mock(AtributoDinamico.class);
        when(original.mismoNombreQue(repetido)).thenReturn(true);
        servicio.agregarAtributoDinamico(original);

        assertThrows(ExcepcionDominio.class,
                () -> servicio.agregarAtributoDinamico(repetido));
    }

    @Test
    void decrementarCupo_descuentaLoLibreSinCambiarLosCuposTotales() {
        LocalDate dia = LocalDate.of(2027, 1, 10);

        servicio.decrementarCupo(dia, dia, 4);

        assertEquals(6, servicio.getCuposDisponiblesEn(dia));
        assertEquals(4, servicio.cuposReservadosEntre(dia, dia));
        assertEquals(10, servicio.cuposTotalesEntre(dia, dia));
    }

    @Test
    void getCuposDisponibles_conCupoEnOtraFecha_siguePositivo() {
        LocalDate dia = LocalDate.of(2027, 1, 10);

        servicio.decrementarCupo(dia, dia, 10);

        assertEquals(0, servicio.getCuposDisponiblesEn(dia));
        assertEquals(10, servicio.getCuposDisponibles());
    }

    @Test
    void definirCupo_daDisponibilidadEnEsaFechaAUnServicioSinCupoPorDefecto() {
        ServicioIndividual temporada = new ServicioIndividual("HOT-002", "Hotel", "d",
                "op", new Categoria("Alojamiento"), 1000.0, 0);
        LocalDate dia = LocalDate.of(2027, 1, 10);

        temporada.definirCupo(dia, 3);

        assertEquals(3, temporada.getCuposDisponibles());
        assertTrue(temporada.tieneCupo(dia, dia, 3));
        assertFalse(temporada.tieneCupo(dia, dia.plusDays(1), 3));
    }

    @Test
    void definirCupoEnRango_daDisponibilidadEnTodaLaTemporada() {
        ServicioIndividual temporada = new ServicioIndividual("HOT-003", "Hotel", "d",
                "op", new Categoria("Alojamiento"), 1000.0, 0);
        LocalDate desde = LocalDate.of(2027, 1, 1);
        LocalDate hasta = LocalDate.of(2027, 1, 31);

        temporada.definirCupo(desde, hasta, 5);

        assertTrue(temporada.tieneCupo(desde, hasta, 5));
        assertFalse(temporada.tieneCupo(desde, hasta.plusDays(1), 5));
    }

    @Test
    void precioFinal_sinDescuentoPromocional_esIgualAlPrecioBase() {
        assertEquals(15000.0, servicio.precioFinal(), 0.001);
    }

    @Test
    void precioFinal_conDescuentoPromocional_devuelveElPrecioBase() {
        servicio.aplicarDescuentoPromocional(10);

        assertEquals(servicio.precioBase(), servicio.precioFinal(), 0.001);
    }

    @Test
    void aplicarDescuentoPromocional_conPorcentajeNegativo_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> servicio.aplicarDescuentoPromocional(-1));
    }

    @Test
    void aplicarDescuentoPromocional_conPorcentajeMayorACien_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> servicio.aplicarDescuentoPromocional(101));
    }

    @Test
    void contieneEnNombre_conNombreNulo_esFalso() {
        ServicioIndividual sinNombre = new ServicioIndividual("HOT-001", null, "d",
                "op", new Categoria("Alojamiento"), 1.0, 10);

        assertFalse(sinNombre.contieneEnNombre("central"));
    }

    @Test
    void contieneEnNombre_sinDistinguirMayusculas() {
        assertTrue(servicio.contieneEnNombre("central"));
    }

    @Test
    void contieneEnNombre_conTextoQueNoAparece_esFalso() {
        assertFalse(servicio.contieneEnNombre("Hostel"));
    }

    @Test
    void esDeCategoria_conLaMismaCategoria_esVerdadero() {
        assertTrue(servicio.esDeCategoria(new Categoria("Alojamiento")));
    }

    @Test
    void esDeCategoria_conOtraCategoria_esFalso() {
        assertFalse(servicio.esDeCategoria(new Categoria("Vuelo")));
    }

    // --- Validación de atributos dinámicos (enunciado 2.1) ---
    // Un atributo dinámico se DEFINE con nombre, obligatoriedad y tipo; el valor se asigna después.
    // Es validar() del servicio quien comprueba que todo obligatorio (fijo o dinámico) tenga valor.

    @Test
    void setValorAtributo_nuloEnUnObligatorio_lanzaExcepcionDeDominio() {
        servicio.agregarAtributoDinamico(new AtributoDinamico("CapacidadMaxima", true, new Numero(), "4"));

        assertThrows(ExcepcionDominio.class, () -> servicio.setValorAtributo("CapacidadMaxima", null));
    }

    @Test
    void validar_conAtributoDinamicoOpcionalSinValor_noLanza() {
        servicio.agregarAtributoDinamico(new AtributoDinamico("PoliticaMascotas", false, new Texto(), null));

        assertDoesNotThrow(() -> servicio.validar());
    }

    @Test
    void validar_conAtributoDinamicoOpcionalConValor_noLanza() {
        servicio.agregarAtributoDinamico(new AtributoDinamico("PoliticaMascotas", false, new Texto(), "Se aceptan"));

        assertDoesNotThrow(() -> servicio.validar());
    }

    @Test
    void validar_despuesDeCambiarElValorDeUnObligatorio_noLanza() {
        servicio.agregarAtributoDinamico(new AtributoDinamico("CapacidadMaxima", true, new Numero(), "2"));

        servicio.setValorAtributo("CapacidadMaxima", "4");

        assertDoesNotThrow(() -> servicio.validar());
    }

    @Test
    void validar_conLosAtributosDinamicosDelEnunciadoCompletos_noLanza() {
        servicio.agregarAtributoDinamico(new AtributoDinamico("CapacidadMaxima", true, new Numero(), "4"));
        servicio.agregarAtributoDinamico(new AtributoDinamico("PoliticaMascotas", true, new Booleano(), "true"));
        servicio.agregarAtributoDinamico(new AtributoDinamico("RatingEstrellas", false, new Numero(), "4.5"));

        assertDoesNotThrow(() -> servicio.validar());
    }

    @Test
    void setValorAtributo_conValorDeOtroTipo_lanzaExcepcionDeDominio() {
        servicio.agregarAtributoDinamico(new AtributoDinamico("FechaApertura", true, new Fecha(), "2027-01-01"));

        assertThrows(ExcepcionDominio.class, () -> servicio.setValorAtributo("FechaApertura", "01/01/2027"));
    }
}
