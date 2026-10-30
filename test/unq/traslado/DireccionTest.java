package unq.traslado;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DireccionTest {

    private Direccion direccion;

    @BeforeEach
    void setUp() {
        direccion = new Direccion("Calle Falsa", 123, "Quilmes", "1878", 2);
    }

    @Test
    void getters_devuelvenLosDatosDelConstructor() {
        assertEquals("Calle Falsa", direccion.getCalle());
        assertEquals(123, direccion.getNumero());
        assertEquals("Quilmes", direccion.getCiudad());
        assertEquals("1878", direccion.getCodigoPostal());
    }

    @Test
    void getCuposDisponibles_devuelveLosCuposDeGuiasIniciales() {
        assertEquals(2, direccion.getCuposDisponibles());
    }

    @Test
    void asignarGuia_descuentaUnCupo() {
        direccion.asignarGuia();

        assertEquals(1, direccion.getCuposDisponibles());
    }

    @Test
    void asignarGuia_variasVeces_descuentaUnCupoPorCadaUna() {
        direccion.asignarGuia();
        direccion.asignarGuia();

        assertEquals(0, direccion.getCuposDisponibles());
    }
}
