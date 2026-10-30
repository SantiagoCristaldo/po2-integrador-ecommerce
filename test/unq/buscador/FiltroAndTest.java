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

class FiltroAndTest {

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
    void cumple_cuandoTodosLosCriteriosSeCumplen_devuelveTrue() {
        assertTrue(new FiltroAnd(verdadero, verdadero).cumple(item));
    }

    @Test
    void cumple_cuandoAlgunCriterioNoSeCumple_devuelveFalse() {
        assertFalse(new FiltroAnd(verdadero, falso).cumple(item));
    }

    @Test
    void cumple_sinCriterios_devuelveTrue() {
        assertTrue(new FiltroAnd().cumple(item));
    }

    @Test
    void cumple_cortaAlPrimerCriterioQueNoSeCumple() {
        Filtro siguiente = mock(Filtro.class);

        new FiltroAnd(falso, siguiente).cumple(item);

        verify(siguiente, never()).cumple(any());
    }

    @Test
    void agregar_sumaUnCriterioQueSeTieneEnCuenta() {
        FiltroAnd filtro = new FiltroAnd(verdadero);

        filtro.agregar(falso);

        assertFalse(filtro.cumple(item));
    }

    @Test
    void cumple_admiteFiltrosCompuestosAnidados() {
        FiltroAnd filtro = new FiltroAnd(verdadero, new FiltroOr(falso, verdadero));

        assertTrue(filtro.cumple(item));
    }
}
