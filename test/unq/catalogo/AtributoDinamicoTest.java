package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AtributoDinamicoTest {

    private TipoValor tipo;

    @BeforeEach
    void setUp() {
        tipo = mock(TipoValor.class);
    }

    @Test
    void validar_obligatorioSinValor_esInvalidoYNoConsultaElTipo() {
        AtributoDinamico atributo = new AtributoDinamico("CapacidadMaxima", true, tipo);

        assertFalse(atributo.validar());

        verify(tipo, never()).esValido(any());
    }

    @Test
    void validar_noObligatorioSinValor_esValidoYNoConsultaElTipo() {
        AtributoDinamico atributo = new AtributoDinamico("PoliticaMascotas", false, tipo);

        assertTrue(atributo.validar());

        verify(tipo, never()).esValido(any());
    }

    @Test
    void validar_conValorAceptadoPorElTipo_esValido() {
        AtributoDinamico atributo = new AtributoDinamico("RatingEstrellas", true, tipo);
        atributo.setValor(5);
        when(tipo.esValido(5)).thenReturn(true);

        assertTrue(atributo.validar());
    }

    @Test
    void validar_conValorRechazadoPorElTipo_esInvalido() {
        AtributoDinamico atributo = new AtributoDinamico("RatingEstrellas", true, tipo);
        atributo.setValor("cinco");
        when(tipo.esValido("cinco")).thenReturn(false);

        assertFalse(atributo.validar());
    }

    @Test
    void tieneNombre_conElMismoNombre_esVerdadero() {
        AtributoDinamico atributo = new AtributoDinamico("CapacidadMaxima", true, tipo);

        assertTrue(atributo.tieneNombre("CapacidadMaxima"));
    }

    @Test
    void tieneNombre_conOtroNombre_esFalso() {
        AtributoDinamico atributo = new AtributoDinamico("CapacidadMaxima", true, tipo);

        assertFalse(atributo.tieneNombre("PoliticaMascotas"));
    }

    @Test
    void tieneNombre_conNombreInternoNulo_esFalso() {
        AtributoDinamico atributo = new AtributoDinamico(null, true, tipo);

        assertFalse(atributo.tieneNombre("CapacidadMaxima"));
    }

    @Test
    void mismoNombreQue_conAtributosHomónimos_esVerdadero() {
        AtributoDinamico uno = new AtributoDinamico("CapacidadMaxima", true, tipo);
        AtributoDinamico otro = new AtributoDinamico("CapacidadMaxima", false, tipo);

        assertTrue(uno.mismoNombreQue(otro));
    }

    @Test
    void mismoNombreQue_conDistintoNombre_esFalso() {
        AtributoDinamico uno = new AtributoDinamico("CapacidadMaxima", true, tipo);
        AtributoDinamico otro = new AtributoDinamico("PoliticaMascotas", true, tipo);

        assertFalse(uno.mismoNombreQue(otro));
    }
}
