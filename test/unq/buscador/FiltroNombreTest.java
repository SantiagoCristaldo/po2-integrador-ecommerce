package unq.buscador;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.ItemCatalogo;

class FiltroNombreTest {

    private ItemCatalogo item;
    private FiltroNombre filtro;

    @BeforeEach
    void setUp() {
        item = mock(ItemCatalogo.class);
        filtro = new FiltroNombre("hotel");
    }

    @Test
    void cumple_cuandoElItemContieneElTexto_devuelveTrue() {
        when(item.contieneEnNombre("hotel")).thenReturn(true);

        assertTrue(filtro.cumple(item));
    }

    @Test
    void cumple_cuandoElItemNoContieneElTexto_devuelveFalse() {
        when(item.contieneEnNombre("hotel")).thenReturn(false);

        assertFalse(filtro.cumple(item));
    }

    @Test
    void cumple_delegaLaBusquedaDelTextoEnElItem() {
        filtro.cumple(item);

        verify(item).contieneEnNombre("hotel");
    }
}
