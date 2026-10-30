package unq.traslado;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TrasladoVIPTest {

    @Test
    void calcularCosto_devuelveElPrecioRecibidoPorqueEsUnStubDeLaAPIExterna() {
        assertEquals(25000.0, new TrasladoVIP().calcularCosto(25000.0), 0.001);
    }

    @Test
    void calcularCosto_conPrecioCero_devuelveCero() {
        assertEquals(0.0, new TrasladoVIP().calcularCosto(0.0), 0.001);
    }
}
