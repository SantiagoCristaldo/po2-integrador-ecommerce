package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;

class PaqueteTuristicoTest {

    private Catalogo item(double precioBase, double precioFinal) {
        Catalogo item = mock(Catalogo.class);
        when(item.precioBase()).thenReturn(precioBase);
        when(item.precioFinal()).thenReturn(precioFinal);
        return item;
    }

    private PaqueteTuristico paquete(int descuento, Catalogo... items) {
        PaqueteTuristico paquete = new PaqueteTuristico("Paquete", "Un paquete", descuento);
        for (Catalogo item : items) {
            paquete.agregarItem(item);
        }
        return paquete;
    }

    @Test
    void precioBase_esLaSumaDeLosPreciosBaseDeSusHijos() {
        PaqueteTuristico paquete = paquete(10,
                item(15000, 1), item(4000, 1), item(2000, 1));

        assertEquals(21000.0, paquete.precioBase(), 0.001);
    }

    @Test
    void precioFinal_casoDelEnunciado_devuelve18900() {
        PaqueteTuristico paquete = paquete(10,
                item(15000, 15000), item(4000, 4000), item(2000, 2000));

        assertEquals(18900.0, paquete.precioFinal(), 0.001);
    }

    @Test
    void precioFinal_aplicaElDescuentoUnaSolaVezPorPaquete() {
        PaqueteTuristico paquete = paquete(10, item(10000, 10000));

        assertEquals(9000.0, paquete.precioFinal(), 0.001);
    }

    @Test
    void precioBase_deUnPaqueteVacio_esCero() {
        assertEquals(0.0, paquete(10).precioBase(), 0.001);
    }

    @Test
    void precioFinal_deUnPaqueteVacio_esCero() {
        assertEquals(0.0, paquete(10).precioFinal(), 0.001);
    }

    @Test
    void quitarItem_loSacaDeLaSumaDeSusPartes() {
        Catalogo hotel = item(15000, 15000);
        PaqueteTuristico paquete = paquete(0, hotel);

        paquete.quitarItem(hotel);

        assertEquals(0.0, paquete.precioBase(), 0.001);
    }

    @Test
    void paqueteAnidado_calculaElPrecioFinalRecursivamente() {
        PaqueteTuristico interno = paquete(10,
                item(15000, 15000), item(4000, 4000), item(2000, 2000));

        PaqueteTuristico externo = paquete(20, interno, item(5000, 5000));

        assertEquals(19120.0, externo.precioFinal(), 0.001);
    }

    @Test
    void agregarItem_conElMismoPaquete_noLanzaYNoSeValidaCiclo() {
        PaqueteTuristico paquete = paquete(10);

        assertDoesNotThrow(() -> paquete.agregarItem(paquete));
    }

    @Test
    void constructor_conDescuentoNegativo_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> new PaqueteTuristico("P", "d", -1));
    }

    @Test
    void constructor_conDescuentoMayorACien_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> new PaqueteTuristico("P", "d", 101));
    }

    @Test
    void contieneEnNombre_conNombreNulo_esFalso() {
        PaqueteTuristico paquete = new PaqueteTuristico(null, "d", 0);

        assertFalse(paquete.contieneEnNombre("otoño"));
    }

    @Test
    void contieneEnNombre_sinDistinguirMayusculas() {
        PaqueteTuristico paquete = new PaqueteTuristico("Escapada de Otoño", "d", 0);

        assertTrue(paquete.contieneEnNombre("otoño"));
    }

    @Test
    void contieneEnNombre_conTextoQueNoAparece_esFalso() {
        PaqueteTuristico paquete = new PaqueteTuristico("Escapada de Otoño", "d", 0);

        assertFalse(paquete.contieneEnNombre("verano"));
    }
}
