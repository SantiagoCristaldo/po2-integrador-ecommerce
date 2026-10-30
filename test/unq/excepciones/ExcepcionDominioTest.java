package unq.excepciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class ExcepcionDominioTest {

    @Test
    void getMessage_devuelveElMensajeRecibido() {
        assertEquals("algo salio mal", new ExcepcionDominio("algo salio mal").getMessage());
    }

    @Test
    void esUnaExcepcionNoChequeada() {
        assertInstanceOf(RuntimeException.class, new ExcepcionDominio("x"));
    }
}
