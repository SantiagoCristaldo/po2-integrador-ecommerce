package unq.notificaciones;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.reserva.EstadoDeReserva;
import unq.reserva.Reserva;

/**
 * Enunciado 2.5: el módulo de reservas sólo comunica el cambio (estado anterior y nuevo) y
 * "nuevos subsistemas podrían ser agregados a futuro": cualquier observador registrado se entera.
 */
class NotificadorTest {

    private Notificador notificador;
    private ObservadorReserva observador;
    private Reserva reserva;
    private EstadoDeReserva anterior;
    private EstadoDeReserva actual;

    @BeforeEach
    void setUp() {
        notificador = new Notificador();
        observador = mock(ObservadorReserva.class);
        reserva = mock(Reserva.class);
        anterior = mock(EstadoDeReserva.class);
        actual = mock(EstadoDeReserva.class);
    }

    @Test
    void actualizarEstados_avisaAlObservadorConElEstadoAnteriorYElNuevo() {
        notificador.agregarObservador(observador);

        notificador.actualizarEstados(reserva, anterior, actual);

        verify(observador).actualizar(reserva, anterior, actual);
    }

    @Test
    void actualizarEstados_avisaATodosLosObservadores() {
        ObservadorReserva otro = mock(ObservadorReserva.class);
        notificador.agregarObservador(observador);
        notificador.agregarObservador(otro);

        notificador.actualizarEstados(reserva, anterior, actual);

        verify(observador).actualizar(reserva, anterior, actual);
        verify(otro).actualizar(reserva, anterior, actual);
    }

    @Test
    void quitarObservador_dejaDeAvisarle() {
        notificador.agregarObservador(observador);
        notificador.quitarObservador(observador);

        notificador.actualizarEstados(reserva, anterior, actual);

        verify(observador, never()).actualizar(reserva, anterior, actual);
    }
}
