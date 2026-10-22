package unq.traslado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.reserva.Reserva;

class PuntoEncuentroTest {

    private PuntoEncuentro punto;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        punto = new PuntoEncuentro("Aeropuerto Ezeiza", 3);
        reserva = mock(Reserva.class);
    }

    @Test
    void calcularCosto_esSiempreCero() {
        assertEquals(0f, punto.calcularCosto(reserva), 0.001f);
    }

    @Test
    void estaDisponible_conCuposDeGuias_esVerdadero() {
        assertTrue(punto.estaDisponible(reserva));
    }

    @Test
    void estaDisponible_sinCuposDeGuias_esFalso() {
        PuntoEncuentro sinCupos = new PuntoEncuentro("Aeropuerto Ezeiza", 0);

        assertFalse(sinCupos.estaDisponible(reserva));
    }

    @Test
    void estaDisponible_noDependeDeLaCantidadDePasajerosDeLaReserva() {
        when(reserva.cantidadDePasajeros()).thenReturn(50);

        assertTrue(punto.estaDisponible(reserva));
    }
}
