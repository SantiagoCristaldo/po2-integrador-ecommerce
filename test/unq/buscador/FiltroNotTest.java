package unq.buscador;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;

class FiltroNotTest {

    private Catalogo item;
    private Filtro criterio;
    private FiltroNot filtro;

    @BeforeEach
    void setUp() {
        item = mock(Catalogo.class);
        criterio = mock(Filtro.class);
        filtro = new FiltroNot(criterio);
    }

    @Test
    void cumple_cuandoElCriterioSeCumple_devuelveFalse() {
        when(criterio.cumple(item)).thenReturn(true);

        assertFalse(filtro.cumple(item));
    }

    @Test
    void cumple_cuandoElCriterioNoSeCumple_devuelveTrue() {
        when(criterio.cumple(item)).thenReturn(false);

        assertTrue(filtro.cumple(item));
    }
}
