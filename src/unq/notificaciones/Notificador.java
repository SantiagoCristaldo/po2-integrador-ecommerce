package unq.notificaciones;

import unq.reserva.*;

import java.util.ArrayList;
import java.util.List;

public class Notificador {
    private List<ObservadorReserva> observadores = new ArrayList<>();
    private Reserva reserva;

    public void agregarObservador(ObservadorReserva observador) {
        observadores.add(observador);
    }

    public void quitarObservador(ObservadorReserva observador) {
        observadores.remove(observador);
    }

    public void actualizarEstados(EstadoDeReserva estadoAnterior, EstadoDeReserva estadoNuevo, Reserva reserva) {
        for (ObservadorReserva observador : observadores) {
            observador.actualizar(estadoAnterior, estadoNuevo, this);
        }
    }
}
