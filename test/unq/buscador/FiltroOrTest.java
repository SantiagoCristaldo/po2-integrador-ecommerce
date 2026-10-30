package unq.buscador;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;

class FiltroOrTest {

    private Catalogo item;
    private Filtro verdadero;
    private Filtro falso;

    @BeforeEach
    void setUp() {
        item = mock(Catalogo.class);
        verdadero = mock(Filtro.class);
        falso = mock(Filtro.class);
        when(verdadero.cumple(item)).thenReturn(true);
        when(falso.cumple(item)).thenReturn(false);
    }

    @Test
    void cumple_cuandoAlgunCriterioSeCumple_devuelveTrue() {
        assertTrue(new FiltroOr(falso, verdadero).cumple(item));
    }

    @Test
    void cumple_cuandoNingunCriterioSeCumple_devuelveFalse() {
        assertFalse(new FiltroOr(falso, falso).cumple(item));
    }

    @Test
    void cumple_sinCriterios_devuelveFalse() {
        assertFalse(new FiltroOr().cumple(item));
    }

    @Test
    void cumple_cortaAlPrimerCriterioQueSeCumple() {
        Filtro siguiente = mock(Filtro.class);

        new FiltroOr(verdadero, siguiente).cumple(item);

        verify(siguiente, never()).cumple(any());
    }

    @Test
    void agregar_sumaUnCriterioQueSeTieneEnCuenta() {
        FiltroOr filtro = new FiltroOr(falso);

        filtro.agregar(verdadero);

        assertTrue(filtro.cumple(item));
    }
}
