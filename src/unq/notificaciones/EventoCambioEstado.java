package unq.notificaciones;

import unq.reserva.EstadoDeReserva;

public class EventoCambioEstado {
    private EstadoDeReserva estadoActual;
    private EstadoDeReserva estadoAnterior;

    public EventoCambioEstado(EstadoDeReserva estadoActual, EstadoDeReserva estadoAnterior){
        this.estadoActual = estadoActual;
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoDeReserva getEstadoAnterior(){
        return this.estadoAnterior;
    }

    public EstadoDeReserva getEstadoActual(){
        return this.estadoActual;
    }
}
