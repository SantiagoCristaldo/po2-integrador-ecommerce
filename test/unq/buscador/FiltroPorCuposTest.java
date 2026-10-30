package unq.buscador;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;

class FiltroPorCuposTest {

    private Catalogo item;
    private FiltroPorCupos filtro;

    @BeforeEach
    void setUp() {
        item = mock(Catalogo.class);
        filtro = new FiltroPorCupos();
    }

    @Test
    void cumple_conCuposDisponibles_devuelveTrue() {
        when(item.getCuposDisponibles()).thenReturn(1);

        assertTrue(filtro.cumple(item));
    }

    @Test
    void cumple_sinCuposDisponibles_devuelveFalse() {
        when(item.getCuposDisponibles()).thenReturn(0);

        assertFalse(filtro.cumple(item));
    }

    @Test
    void cumple_conCuposNegativos_devuelveFalse() {
        when(item.getCuposDisponibles()).thenReturn(-1);

        assertFalse(filtro.cumple(item));
    }
}
