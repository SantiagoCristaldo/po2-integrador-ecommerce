package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;

class CatalogoTest {

    private final ItemCatalogo hotel = mock(ItemCatalogo.class);
    private final ItemCatalogo excursion = mock(ItemCatalogo.class);

    @Test
    void constructor_sinItems_quedaVacio() {
        Catalogo catalogo = new Catalogo();

        assertTrue(catalogo.getItems().isEmpty());
        assertEquals(0, catalogo.cantidadDeItems());
    }

    @Test
    void constructor_conItems_losContieneEnOrden() {
        Catalogo catalogo = new Catalogo(hotel, excursion);

        assertEquals(List.of(hotel, excursion), catalogo.getItems());
        assertEquals(2, catalogo.cantidadDeItems());
    }

    @Test
    void agregarItem_loSumaAlCatalogo() {
        Catalogo catalogo = new Catalogo(hotel);

        catalogo.agregarItem(excursion);

        assertEquals(List.of(hotel, excursion), catalogo.getItems());
    }

    @Test
    void quitarItem_loSacaDelCatalogo() {
        Catalogo catalogo = new Catalogo(hotel, excursion);

        catalogo.quitarItem(hotel);

        assertEquals(List.of(excursion), catalogo.getItems());
    }

    @Test
    void getItems_devuelveUnaCopiaQueNoModificaElCatalogo() {
        Catalogo catalogo = new Catalogo(hotel);

        catalogo.getItems().add(excursion);

        assertEquals(1, catalogo.cantidadDeItems());
    }
}
