package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CategoriaTest {

    @Test
    void seLlama_conElMismoNombre_esVerdadero() {
        assertTrue(new Categoria("Alojamiento").seLlama("Alojamiento"));
    }

    @Test
    void seLlama_conOtroNombre_esFalso() {
        assertFalse(new Categoria("Alojamiento").seLlama("Vuelo"));
    }

    @Test
    void equals_conElMismoNombre_esVerdadero() {
        assertEquals(new Categoria("Vuelo"), new Categoria("Vuelo"));
    }

    @Test
    void equals_conDistintoNombre_esFalso() {
        assertNotEquals(new Categoria("Vuelo"), new Categoria("Excursión"));
    }

    @Test
    void equals_consigoMismo_esVerdadero() {
        Categoria categoria = new Categoria("Vuelo");

        assertEquals(categoria, categoria);
    }

    @Test
    void equals_conUnTipoDistinto_esFalso() {
        assertNotEquals(new Categoria("Vuelo"), "Vuelo");
    }

    @Test
    void seLlama_conNombreInternoNulo_esFalso() {
        assertFalse(new Categoria(null).seLlama("Vuelo"));
    }

    // equals y hashCode tienen que ser consistentes (contrato de Object).
    @Test
    void hashCode_conNombresIguales_deberiaSerIgual() {
        assertEquals(new Categoria("Vuelo").hashCode(), new Categoria("Vuelo").hashCode());
    }
}
