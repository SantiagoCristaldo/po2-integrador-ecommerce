package ar.edu.unq.travel.catalogo;

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

/**
 * SUT: ServicioIndividual.
 * Colaborador doblado: AtributoDinamico (mock). Su lógica interna (obligatoriedad + TipoValor)
 * se testea aparte; acá solo importa cómo ServicioIndividual lo usa.
 */
class ServicioIndividualAtributosDinamicosTest {

    private AtributoDinamico capacidad;
    private AtributoDinamico mascotas;
    private ServicioIndividual servicio;

    @BeforeEach
    void setUp() {
        capacidad = mock(AtributoDinamico.class);
        when(capacidad.getNombre()).thenReturn("CapacidadMaxima");

        mascotas = mock(AtributoDinamico.class);
        when(mascotas.getNombre()).thenReturn("PoliticaMascotas");

        servicio = servicioConDatosFijos();
    }

    // Único lugar que conoce el constructor: si cambia, se toca solo acá.
    private ServicioIndividual servicioConDatosFijos() {
        return new ServicioIndividual("HOT-001", "Hotel Central", "Operador SA",
                mock(Categoria.class), 15000.0);
    }

    private ServicioIndividual servicioSin(String codigo, String nombre) {
        return new ServicioIndividual(codigo, nombre, "Operador SA",
                mock(Categoria.class), 15000.0);
    }

    // ---------- validar(): atributos fijos ----------

    @Test
    void sinAtributosDinamicos_yConDatosFijos_esValido() {
        assertTrue(servicio.validar());
    }

    @Test
    void sinCodigo_noEsValido() {
        assertFalse(servicioSin(null, "Hotel Central").validar());
    }

    @Test
    void sinNombre_noEsValido() {
        assertFalse(servicioSin("HOT-001", null).validar());
    }

    // ---------- validar(): atributos dinámicos (delegación) ----------

    @Test
    void conUnAtributoDinamicoValido_esValido() {
        when(capacidad.validar()).thenReturn(true);
        servicio.agregarAtributoDinamico(capacidad);

        assertTrue(servicio.validar());
    }

    @Test
    void conUnAtributoDinamicoInvalido_noEsValido() {
        when(capacidad.validar()).thenReturn(false);
        servicio.agregarAtributoDinamico(capacidad);

        assertFalse(servicio.validar());
    }

    @Test
    void conVariosAtributos_siUnoEsInvalido_noEsValido() {
        when(capacidad.validar()).thenReturn(true);
        when(mascotas.validar()).thenReturn(false);
        servicio.agregarAtributoDinamico(capacidad);
        servicio.agregarAtributoDinamico(mascotas);

        assertFalse(servicio.validar());
    }

    @Test
    void conVariosAtributos_todosValidos_esValido() {
        when(capacidad.validar()).thenReturn(true);
        when(mascotas.validar()).thenReturn(true);
        servicio.agregarAtributoDinamico(capacidad);
        servicio.agregarAtributoDinamico(mascotas);

        assertTrue(servicio.validar());
    }

    // ---------- setValorAtributo(nombre, valor) ----------

    @Test
    void setValorAtributo_delegaSoloEnElAtributoDeEseNombre() {
        servicio.agregarAtributoDinamico(capacidad);
        servicio.agregarAtributoDinamico(mascotas);

        servicio.setValorAtributo("CapacidadMaxima", 50);

        verify(capacidad).setValor(50);
        verify(mascotas, never()).setValor(any());
    }

    @Test
    void setValorAtributo_conNombreInexistente_lanzaExcepcionDeDominio() {
        servicio.agregarAtributoDinamico(capacidad);

        assertThrows(AtributoInexistenteException.class,
                () -> servicio.setValorAtributo("RatingEstrellas", 5));
        verify(capacidad, never()).setValor(any());
    }

    // ---------- agregarAtributoDinamico ----------

    @Test
    void agregarDosAtributosConElMismoNombre_lanzaExcepcionDeDominio() {
        AtributoDinamico otraCapacidad = mock(AtributoDinamico.class);
        when(otraCapacidad.getNombre()).thenReturn("CapacidadMaxima");
        servicio.agregarAtributoDinamico(capacidad);

        assertThrows(AtributoDuplicadoException.class,
                () -> servicio.agregarAtributoDinamico(otraCapacidad));
    }
}