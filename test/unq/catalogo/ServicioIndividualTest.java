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
                "Operador SA", new Categoria("Alojamiento"), 15000.0);
    }

    @Test
    void validar_conCodigoYNombrePresentesYSinDinamicos_noLanza() {
        assertDoesNotThrow(servicio::validar);
    }

    @Test
    void validar_sinCodigo_lanzaExcepcionDeDominio() {
        ServicioIndividual sinCodigo = new ServicioIndividual(null, "Hotel", "d",
                "op", new Categoria("Alojamiento"), 1.0);

        assertThrows(ExcepcionDominio.class, sinCodigo::validar);
    }

    @Test
    void validar_conCodigoEnBlanco_lanzaExcepcionDeDominio() {
        ServicioIndividual codigoEnBlanco = new ServicioIndividual("   ", "Hotel", "d",
                "op", new Categoria("Alojamiento"), 1.0);

        assertThrows(ExcepcionDominio.class, codigoEnBlanco::validar);
    }

    @Test
    void validar_sinNombre_lanzaExcepcionDeDominio() {
        ServicioIndividual sinNombre = new ServicioIndividual("HOT-001", null, "d",
                "op", new Categoria("Alojamiento"), 1.0);

        assertThrows(ExcepcionDominio.class, sinNombre::validar);
    }

    @Test
    void validar_sinOperador_lanzaExcepcionDeDominio() {
        ServicioIndividual sinOperador = new ServicioIndividual("HOT-001", "Hotel", "d",
                null, new Categoria("Alojamiento"), 1.0);

        assertThrows(ExcepcionDominio.class, sinOperador::validar);
    }

    @Test
    void validar_sinCategoria_lanzaExcepcionDeDominio() {
        ServicioIndividual sinCategoria = new ServicioIndividual("HOT-001", "Hotel", "d",
                "op", null, 1.0);

        assertThrows(ExcepcionDominio.class, sinCategoria::validar);
    }

    @Test
    void validar_conUnAtributoDinamicoInvalido_lanzaExcepcionDeDominio() {
        AtributoDinamico invalido = mock(AtributoDinamico.class);
        when(invalido.validar()).thenReturn(false);
        servicio.agregarAtributoDinamico(invalido);

        assertThrows(ExcepcionDominio.class, servicio::validar);
    }

    @Test
    void validar_recorreTodosLosAtributosDinamicosAunqueElPrimeroSeaValido() {
        AtributoDinamico valido = mock(AtributoDinamico.class);
        AtributoDinamico invalido = mock(AtributoDinamico.class);
        when(valido.validar()).thenReturn(true);
        when(invalido.validar()).thenReturn(false);
        servicio.agregarAtributoDinamico(valido);
        servicio.agregarAtributoDinamico(invalido);

        assertThrows(ExcepcionDominio.class, servicio::validar);

        verify(valido).validar();
        verify(invalido).validar();
    }

    @Test
    void validar_conTodosLosAtributosDinamicosValidos_noLanza() {
        AtributoDinamico primero = mock(AtributoDinamico.class);
        AtributoDinamico segundo = mock(AtributoDinamico.class);
        when(primero.validar()).thenReturn(true);
        when(segundo.validar()).thenReturn(true);
        servicio.agregarAtributoDinamico(primero);
        servicio.agregarAtributoDinamico(segundo);

        assertDoesNotThrow(servicio::validar);
    }

    @Test
    void setValorAtributo_delegaSoloEnElAtributoConEseNombre() {
        AtributoDinamico hotel = mock(AtributoDinamico.class);
        AtributoDinamico vuelo = mock(AtributoDinamico.class);
        when(hotel.tieneNombre("CapacidadMaxima")).thenReturn(true);
        when(vuelo.tieneNombre("CapacidadMaxima")).thenReturn(false);
        servicio.agregarAtributoDinamico(hotel);
        servicio.agregarAtributoDinamico(vuelo);

        servicio.setValorAtributo("CapacidadMaxima", 10);

        verify(hotel).setValor(10);
        verify(vuelo, never()).setValor(any());
    }

    @Test
    void setValorAtributo_conNombreInexistente_lanzaYNoModificaNinguno() {
        AtributoDinamico hotel = mock(AtributoDinamico.class);
        servicio.agregarAtributoDinamico(hotel);

        assertThrows(ExcepcionDominio.class,
                () -> servicio.setValorAtributo("Inexistente", 1));

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
    void precioFinal_sinDescuentoPromocional_esIgualAlPrecioBase() {
        assertEquals(15000.0, servicio.precioFinal(), 0.001);
    }

    @Test
    void precioFinal_conDescuentoPromocional_aplicaElPorcentaje() {
        servicio.aplicarDescuentoPromocional(10);

        assertEquals(13500.0, servicio.precioFinal(), 0.001);
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
                "op", new Categoria("Alojamiento"), 1.0);

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
}
