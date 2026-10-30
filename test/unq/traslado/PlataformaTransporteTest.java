package unq.traslado;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlataformaTransporteTest {

    @Test
    void estimarTraslado_devuelveCeroPorqueEsUnStubDeLaAPIExterna() {
        Direccion destino = new Direccion("Calle Falsa", 123, "Quilmes", "1878", 1);

        assertEquals(0f, new PlataformaTransporte().estimarTraslado(3, destino), 0.001f);
    }
}
