package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CategoriaTest {

    @Test
    void seLlama_comparaPorNombreExacto() {
        Categoria categoria = new Categoria("Alojamiento");

        assertTrue(categoria.seLlama("Alojamiento"));
        assertFalse(categoria.seLlama("Vuelo"));
    }

    @Test
    void dosCategoriasConElMismoNombre_sonIguales() {
        assertEquals(new Categoria("Vuelo"), new Categoria("Vuelo"));
        assertEquals(new Categoria("Vuelo").hashCode(), new Categoria("Vuelo").hashCode());
    }

    @Test
    void categoriasConDistintoNombre_noSonIguales() {
        assertNotEquals(new Categoria("Vuelo"), new Categoria("Excursión"));
    }
}
