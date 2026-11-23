package unq.buscador;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.ItemCatalogo;

class FiltroPrecioMaximoTest {

    private ItemCatalogo item;
    private FiltroPrecioMaximo filtro;

    @BeforeEach
    void setUp() {
        item = mock(ItemCatalogo.class);
        filtro = new FiltroPrecioMaximo(10000.0);
    }

    @Test
    void cumple_conPrecioMenorAlMaximo_devuelveTrue() {
        when(item.precioBase()).thenReturn(5000.0);

        assertTrue(filtro.cumple(item));
    }

    @Test
    void cumple_conPrecioIgualAlMaximo_devuelveTrue() {
        when(item.precioBase()).thenReturn(10000.0);

        assertTrue(filtro.cumple(item));
    }

    @Test
    void cumple_conPrecioMayorAlMaximo_devuelveFalse() {
        when(item.precioBase()).thenReturn(10000.01);

        assertFalse(filtro.cumple(item));
    }
}
