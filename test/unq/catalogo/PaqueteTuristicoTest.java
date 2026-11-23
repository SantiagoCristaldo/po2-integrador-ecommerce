package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;

class PaqueteTuristicoTest {

    private ItemCatalogo item(double precioBase, double precioFinal) {
        ItemCatalogo item = mock(ItemCatalogo.class);
        when(item.precioBase()).thenReturn(precioBase);
        when(item.precioFinal()).thenReturn(precioFinal);
        return item;
    }

    private PaqueteTuristico paquete(int descuento, ItemCatalogo... items) {
        PaqueteTuristico paquete = new PaqueteTuristico("Paquete", "Un paquete", descuento, 10);
        for (ItemCatalogo item : items) {
            paquete.agregarItem(item);
        }
        return paquete;
    }

    // --- Cupos por fecha ---

    private static final LocalDate INICIO = LocalDate.of(2027, 1, 10);
    private static final LocalDate FIN = LocalDate.of(2027, 1, 12);

    private ServicioIndividual servicioConCupo(int cupo) {
        return new ServicioIndividual("S1", "Servicio", "d", "op", new Categoria("Excursion"), 100.0, cupo);
    }

    @Test
    void tieneCupo_conCupoEnTodosLosItems_esVerdadero() {
        PaqueteTuristico paquete = paquete(0, servicioConCupo(2), servicioConCupo(5));

        assertTrue(paquete.tieneCupo(INICIO, FIN, 2));
    }

    @Test
    void tieneCupo_sinCupoEnUnItem_esFalso() {
        PaqueteTuristico paquete = paquete(0, servicioConCupo(1), servicioConCupo(5));

        assertFalse(paquete.tieneCupo(INICIO, FIN, 2));
    }

    @Test
    void incrementarCupo_devuelveElCupoASusItems() {
        ServicioIndividual servicio = servicioConCupo(5);
        PaqueteTuristico paquete = paquete(0, servicio);
        paquete.decrementarCupo(INICIO, FIN, 2);

        paquete.incrementarCupo(INICIO, FIN, 2);

        assertEquals(5, servicio.getCuposDisponiblesEn(INICIO));
    }

    @Test
    void decrementarCupo_enPaquetesAnidados_llegaALosServicios() {
        ServicioIndividual servicio = servicioConCupo(5);
        PaqueteTuristico paquete = paquete(0, paquete(0, servicio));

        paquete.decrementarCupo(INICIO, FIN, 2);

        assertEquals(3, servicio.getCuposDisponiblesEn(FIN));
    }

    @Test
    void decrementarCupo_conUnServicioRepetidoSinCupoParaAmbos_noDescuentaNada() {
        ServicioIndividual servicio = servicioConCupo(3);
        PaqueteTuristico paquete = paquete(0, servicio, servicio);

        assertThrows(ExcepcionDominio.class, () -> paquete.decrementarCupo(INICIO, FIN, 2));

        assertEquals(3, servicio.getCuposDisponiblesEn(INICIO));
    }

    @Test
    void factorDeDescuento_esElComplementoDelPorcentaje() {
        assertEquals(0.9, paquete(10).factorDeDescuento(), 0.001);
    }

    @Test
    void precioBase_esLaSumaDeLosPreciosFinalesDeSusHijos() {
        PaqueteTuristico paquete = paquete(10,
                item(15000, 1), item(4000, 1), item(2000, 1));

        assertEquals(3.0, paquete.precioBase(), 0.001);
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
        ItemCatalogo hotel = item(15000, 15000);
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
    void agregarItem_conElMismoPaquete_lanzaExcepcionDeDominio() {
        PaqueteTuristico paquete = paquete(10);

        assertThrows(ExcepcionDominio.class, () -> paquete.agregarItem(paquete));
    }

    @Test
    void agregarItem_conUnPaqueteQueYaLoContiene_lanzaExcepcionDeDominio() {
        PaqueteTuristico interno = paquete(10);
        PaqueteTuristico externo = paquete(10, interno);

        assertThrows(ExcepcionDominio.class, () -> interno.agregarItem(externo));
    }

    @Test
    void agregarItem_conUnPaqueteQueLoContieneEnProfundidad_lanzaExcepcionDeDominio() {
        PaqueteTuristico nivel1 = paquete(10);
        PaqueteTuristico nivel2 = paquete(10, nivel1);
        PaqueteTuristico nivel3 = paquete(10, nivel2);

        assertThrows(ExcepcionDominio.class, () -> nivel1.agregarItem(nivel3));
    }

    @Test
    void agregarItem_conPaquetesSinCiclo_noLanza() {
        PaqueteTuristico italia = paquete(10);
        PaqueteTuristico europa = paquete(5);

        assertDoesNotThrow(() -> europa.agregarItem(italia));
    }

    @Test
    void agregarItem_noConsumeElCupoDelItem() {
        ServicioIndividual hotel = new ServicioIndividual("H1", "Hotel", "d", "op",
                new Categoria("Alojamiento"), 15000.0, 10);

        paquete(10, hotel);

        assertEquals(10, hotel.getCuposDisponibles());
    }

    @Test
    void constructor_conDescuentoNegativo_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> new PaqueteTuristico("P", "d", -1, 10));
    }

    @Test
    void constructor_conDescuentoMayorACien_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> new PaqueteTuristico("P", "d", 101, 10));
    }

    @Test
    void contieneEnNombre_conNombreNulo_esFalso() {
        PaqueteTuristico paquete = new PaqueteTuristico(null, "d", 0, 10);

        assertFalse(paquete.contieneEnNombre("otoño"));
    }

    @Test
    void contieneEnNombre_sinDistinguirMayusculas() {
        PaqueteTuristico paquete = new PaqueteTuristico("Escapada de Otoño", "d", 0, 10);

        assertTrue(paquete.contieneEnNombre("otoño"));
    }

    @Test
    void contieneEnNombre_conTextoQueNoAparece_esFalso() {
        PaqueteTuristico paquete = new PaqueteTuristico("Escapada de Otoño", "d", 0, 10);

        assertFalse(paquete.contieneEnNombre("verano"));
    }
}
