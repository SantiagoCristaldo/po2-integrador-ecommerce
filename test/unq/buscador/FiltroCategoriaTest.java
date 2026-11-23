package unq.buscador;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.ItemCatalogo;
import unq.catalogo.Categoria;

class FiltroCategoriaTest {

    private ItemCatalogo item;
    private Categoria alojamiento;
    private FiltroCategoria filtro;

    @BeforeEach
    void setUp() {
        item = mock(ItemCatalogo.class);
        alojamiento = new Categoria("Alojamiento");
        filtro = new FiltroCategoria(alojamiento);
    }

    @Test
    void cumple_cuandoElItemEsDeLaCategoria_devuelveTrue() {
        when(item.esDeCategoria(alojamiento)).thenReturn(true);

        assertTrue(filtro.cumple(item));
    }

    @Test
    void cumple_cuandoElItemNoEsDeLaCategoria_devuelveFalse() {
        when(item.esDeCategoria(alojamiento)).thenReturn(false);

        assertFalse(filtro.cumple(item));
    }

    @Test
    void cumple_delegaLaConsultaDeCategoriaEnElItem() {
        filtro.cumple(item);

        verify(item).esDeCategoria(alojamiento);
    }
}
