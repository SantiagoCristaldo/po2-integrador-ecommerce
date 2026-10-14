package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;

class PaqueteTuristicoTest {

    private ServicioIndividual hotel;
    private ServicioIndividual excursión;

    @BeforeEach
    void setUp() {
        hotel = new ServicioIndividual("HOT-001", "Hotel Central", "Un hotel",
                "Operador SA", new Categoria("Hotel"), 15000.0);
        excursión = new ServicioIndividual("EXC-001", "Excursión Lago", "Un paseo",
                "Operador SA", new Categoria("Excursión"), 6000.0);
    }

    @Test
    void precioBase_esLaSumaDeLosItems() {
        PaqueteTuristico paquete = new PaqueteTuristico("Escapada", "Dos servicios", 10);
        paquete.agregarItem(hotel);
        paquete.agregarItem(excursión);

        assertEquals(21000.0, paquete.precioBase());
    }

    @Test
    void precioFinal_aplicaElDescuentoDelPaquete() {
        PaqueteTuristico paquete = new PaqueteTuristico("Escapada", "Dos servicios", 10);
        paquete.agregarItem(hotel);
        paquete.agregarItem(excursión);

        assertEquals(18900.0, paquete.precioFinal());
    }

    @Test
    void descuentoFueraDeRango_lanzaExcepcion() {
        assertThrows(ExcepcionDominio.class, () -> new PaqueteTuristico("Escapada", "x", 150));
        assertThrows(ExcepcionDominio.class, () -> new PaqueteTuristico("Escapada", "x", -5));
    }

    @Test
    void agregarUnPaqueteQueGeneraCiclo_lanzaExcepcion() {
        PaqueteTuristico a = new PaqueteTuristico("A", "a", 0);
        PaqueteTuristico b = new PaqueteTuristico("B", "b", 0);
        a.agregarItem(b);

        assertThrows(ExcepcionDominio.class, () -> b.agregarItem(a));
    }

    @Test
    void noSePuedeAgregarASiMismo() {
        PaqueteTuristico a = new PaqueteTuristico("A", "a", 0);

        assertThrows(ExcepcionDominio.class, () -> a.agregarItem(a));
    }

    @Test
    void contieneEnNombre_ignoraMayusculas() {
        PaqueteTuristico paquete = new PaqueteTuristico("Escapada de Otoño", "x", 0);

        assertTrue(paquete.contieneEnNombre("otoño"));
        assertFalse(paquete.contieneEnNombre("verano"));
    }

    @Test
    void exponeNombreYDescripcion() {
        PaqueteTuristico paquete = new PaqueteTuristico("Escapada", "Dos servicios", 10);

        assertEquals("Escapada", paquete.nombre());
        assertEquals("Dos servicios", paquete.descripcion());
    }

    @Test
    void paqueteVacio_tienePreciosCero() {
        PaqueteTuristico paquete = new PaqueteTuristico("Vacío", "x", 10);

        assertEquals(0.0, paquete.precioBase());
        assertEquals(0.0, paquete.precioFinal());
    }

    @Test
    void quitarItem_loSacaDelPaquete() {
        PaqueteTuristico paquete = new PaqueteTuristico("Escapada", "x", 0);
        paquete.agregarItem(hotel);
        paquete.quitarItem(hotel);

        assertEquals(0.0, paquete.precioBase());
    }

    @Test
    void paqueteAnidado_calculaElPrecioRecursivamente() {
        PaqueteTuristico interno = new PaqueteTuristico("Pack Urbana", "x", 10);
        interno.agregarItem(hotel);
        interno.agregarItem(excursión);

        PaqueteTuristico externo = new PaqueteTuristico("Kit Gran Tour", "x", 0);
        externo.agregarItem(interno);

        assertEquals(21000.0, externo.precioBase());
        assertEquals(18900.0, externo.precioFinal());
    }

    @Test
    void detectaCicloIndirecto() {
        PaqueteTuristico a = new PaqueteTuristico("A", "a", 0);
        PaqueteTuristico b = new PaqueteTuristico("B", "b", 0);
        PaqueteTuristico c = new PaqueteTuristico("C", "c", 0);
        a.agregarItem(b);
        b.agregarItem(c);

        assertThrows(ExcepcionDominio.class, () -> c.agregarItem(a));
    }
}
