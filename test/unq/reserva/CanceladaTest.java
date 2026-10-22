package unq.reserva;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;

class CanceladaTest {

    private Cancelada estado;
    private Reserva reserva;
    private Catalogo item;

    @BeforeEach
    void setUp() {
        estado = new Cancelada();
        reserva = mock(Reserva.class);
        item = new ServicioIndividual("HOT-001", "Hotel Central", "Un hotel",
                "Operador SA", new Categoria("Alojamiento"), 15000.0);
    }

    @Test
    void agregarItem_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.agregarItem(reserva, item));
    }

    @Test
    void quitarItem_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.quitarItem(reserva, item));
    }

    @Test
    void confirmar_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.confirmar(reserva));
    }

    @Test
    void hacerCheckin_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.hacerCheckin(reserva));
    }

    @Test
    void iniciar_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.iniciar(reserva));
    }

    @Test
    void finalizar_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.finalizar(reserva));
    }

    @Test
    void cancelar_lanzaExcepcionDeDominio() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.cancelar(reserva));
    }

    @Test
    void ningunaOperacionCambiaElEstadoNiLiberaCupos() {
        assertThrows(ExcepcionPorInvalido.class, () -> estado.agregarItem(reserva, item));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.quitarItem(reserva, item));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.confirmar(reserva));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.hacerCheckin(reserva));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.iniciar(reserva));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.finalizar(reserva));
        assertThrows(ExcepcionPorInvalido.class, () -> estado.cancelar(reserva));

        verify(reserva, never()).cambiarEstado(any());
        verify(reserva, never()).liberarCupos();
    }
}
