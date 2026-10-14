package unq.reserva;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;

class ReservaTest {

    private Reserva reserva;
    private Catalogo servicio;

    @BeforeEach
    void setUp() {
        reserva = spy(new Reserva());
        servicio = new ServicioIndividual("HOT-001", "Hotel Central", "Un hotel",
                "Operador SA", new Categoria("Hotel"), 15000.0);
    }

    @Test
    void nuevaReserva_permiteAgregarYQuitarItems() {
        assertDoesNotThrow(() -> reserva.agregarItem(servicio));
        assertDoesNotThrow(() -> reserva.quitarItem(servicio));
    }

    @Test
    void solicitudPendiente_noPermiteHacerCheckin() {
        assertThrows(ExcepcionPorInvalido.class, reserva::hacerCheckin);
    }

    @Test
    void solicitudPendiente_noPermiteIniciarNiFinalizar() {
        assertThrows(ExcepcionPorInvalido.class, reserva::iniciar);
        assertThrows(ExcepcionPorInvalido.class, reserva::finalizar);
    }

    @Test
    void confirmar_bloqueaElCupo() {
        reserva.confirmar();

        verify(reserva).bloquearCupos();
    }

    @Test
    void desdePagado_noSePuedenAgregarNiQuitarItems() {
        reserva.confirmar();

        assertThrows(ExcepcionPorInvalido.class, () -> reserva.agregarItem(servicio));
        assertThrows(ExcepcionPorInvalido.class, () -> reserva.quitarItem(servicio));
    }

    @Test
    void desdePagado_noSePuedeConfirmarDeNuevoNiFinalizar() {
        reserva.confirmar();

        assertThrows(ExcepcionPorInvalido.class, reserva::confirmar);
        assertThrows(ExcepcionPorInvalido.class, reserva::finalizar);
    }

    @Test
    void cancelarDesdePagado_liberaElCupo() {
        reserva.confirmar();
        reserva.cancelar();

        verify(reserva).liberarCupos();
        verify(reserva, never()).reembolsar();
    }

    @Test
    void cancelarDesdeSolicitudPendiente_noTocaCupoNiReembolso() {
        reserva.cancelar();

        verify(reserva, never()).bloquearCupos();
        verify(reserva, never()).liberarCupos();
        verify(reserva, never()).reembolsar();
        assertThrows(ExcepcionPorInvalido.class, () -> reserva.agregarItem(servicio));
    }

    @Test
    void cancelarDesdeEnChecking_liberaElCupoYReembolsa() {
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.cancelar();

        verify(reserva).liberarCupos();
        verify(reserva).reembolsar();
    }

    @Test
    void enChecking_noSePuedeVolverAHacerCheckinNiFinalizar() {
        reserva.confirmar();
        reserva.hacerCheckin();

        assertThrows(ExcepcionPorInvalido.class, reserva::hacerCheckin);
        assertThrows(ExcepcionPorInvalido.class, reserva::finalizar);
        assertThrows(ExcepcionPorInvalido.class, reserva::confirmar);
    }

    @Test
    void cancelarDesdeEnCurso_soloReembolsa() {
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.iniciar();
        reserva.cancelar();

        verify(reserva).reembolsar();
        verify(reserva, never()).liberarCupos();
    }

    @Test
    void enCurso_noPermiteRetroceder() {
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.iniciar();

        assertThrows(ExcepcionPorInvalido.class, reserva::hacerCheckin);
        assertThrows(ExcepcionPorInvalido.class, reserva::iniciar);
        assertThrows(ExcepcionPorInvalido.class, reserva::confirmar);
    }

    @Test
    void flujoCompleto_hastaFinalizada() {
        reserva.agregarItem(servicio);
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.iniciar();
        reserva.finalizar();

        assertThrows(ExcepcionPorInvalido.class, reserva::cancelar);
        assertThrows(ExcepcionPorInvalido.class, reserva::finalizar);
        assertThrows(ExcepcionPorInvalido.class, reserva::iniciar);
        assertThrows(ExcepcionPorInvalido.class, reserva::hacerCheckin);
        assertThrows(ExcepcionPorInvalido.class, reserva::confirmar);
        assertThrows(ExcepcionPorInvalido.class, () -> reserva.agregarItem(servicio));
        assertThrows(ExcepcionPorInvalido.class, () -> reserva.quitarItem(servicio));
    }

    @Test
    void canceladaEsTerminal() {
        reserva.cancelar();

        assertThrows(ExcepcionPorInvalido.class, reserva::cancelar);
        assertThrows(ExcepcionPorInvalido.class, reserva::hacerCheckin);
    }

    @Test
    void noSePuedeIniciarUnaReservaPagadaSinCheckin() {
        reserva.confirmar();

        assertThrows(ExcepcionPorInvalido.class, reserva::iniciar);
    }
}
