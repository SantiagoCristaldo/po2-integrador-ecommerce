package unq.traslado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;
import unq.reserva.Reserva;

/**
 * Enunciado 2.3: "El costo de traslado es siempre cero. La estimación de disponibilidad depende de
 * si hay cupo de guías en dicho punto."
 */
class PuntoEncuentroTest {

    private Direccion ezeiza;
    private Direccion hotel;
    private PuntoEncuentro punto;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        ezeiza = new Direccion("Aeropuerto Ezeiza", 1, "Ezeiza", "1802", 3);
        hotel = new Direccion("Hotel Central", 10, "CABA", "1000", 5);
        punto = new PuntoEncuentro(ezeiza);
        reserva = mock(Reserva.class);
    }

    private PuntoEncuentro puntoSinGuias() {
        return new PuntoEncuentro(new Direccion("Aeroparque", 1, "CABA", "1425", 0));
    }

    @Test
    void calcularCosto_esSiempreCero() {
        assertEquals(0.0, punto.calcularCosto(reserva), 0.001);
    }

    @Test
    void getUbicacion_devuelveLaDireccionDelPunto() {
        assertSame(ezeiza, punto.getUbicacion());
    }

    @Test
    void estaDisponible_conCupoDeGuiasEnElPunto_esVerdadero() {
        assertTrue(punto.estaDisponible());
    }

    @Test
    void estaDisponible_sinCupoDeGuiasEnElPunto_esFalso() {
        assertFalse(puntoSinGuias().estaDisponible());
    }

    @Test
    void asignarGuia_descuentaUnCupoDelPunto() {
        punto.asignarGuia();

        assertEquals(2, ezeiza.getCuposDisponibles());
    }

    @Test
    void asignarGuia_noTocaElCupoDelDestino() {
        punto.asignarGuia();

        assertEquals(5, hotel.getCuposDisponibles());
    }

    @Test
    void asignarGuia_sinCupoDeGuiasEnElPunto_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionDominio.class, () -> puntoSinGuias().asignarGuia());
    }

    @Test
    void asignarGuia_hastaAgotarLosGuias_dejaElPuntoNoDisponible() {
        punto.asignarGuia();
        punto.asignarGuia();
        punto.asignarGuia();

        assertFalse(punto.estaDisponible());
        assertThrows(ExcepcionDominio.class, () -> punto.asignarGuia());
    }
}
