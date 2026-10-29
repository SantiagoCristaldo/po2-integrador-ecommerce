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

class PuntoEncuentroTest {

    private Direccion ezeiza;
    private PuntoEncuentro punto;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        ezeiza = new Direccion("Aeropuerto Ezeiza", 1, "Ezeiza", "1802", 3);
        punto = new PuntoEncuentro(ezeiza);
        reserva = mock(Reserva.class);
    }

    @Test
    void calcularCosto_esSiempreCero() {
        assertEquals(0f, punto.calcularCosto(reserva), 0.001f);
    }

    @Test
    void getUbicacion_devuelveLaDireccionDelPunto() {
        assertSame(ezeiza, punto.getUbicacion());
    }

    @Test
    void estaDisponible_conCuposDeGuias_esVerdadero() {
        assertTrue(punto.estaDisponible(ezeiza));
    }

    @Test
    void estaDisponible_sinCuposDeGuias_esFalso() {
        Direccion sinCupos = new Direccion("Aeropuerto Ezeiza", 1, "Ezeiza", "1802", 0);

        assertFalse(punto.estaDisponible(sinCupos));
    }

    @Test
    void asignarGuia_descuentaUnCupoDelDestino() {
        punto.asignarGuia(ezeiza);

        assertEquals(2, ezeiza.getCuposDisponibles());
    }

    @Test
    void asignarGuia_sinCuposDeGuias_lanzaExcepcionDeDominio() {
        Direccion sinCupos = new Direccion("Aeropuerto Ezeiza", 1, "Ezeiza", "1802", 0);

        assertThrows(ExcepcionDominio.class, () -> punto.asignarGuia(sinCupos));
    }
}
