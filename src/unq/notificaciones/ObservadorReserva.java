package unq.notificaciones;

import unq.reserva.*;
public interface ObservadorReserva {

    public void actualizar(Reserva reserva, EstadoDeReserva estadoAnterior, EstadoDeReserva estadoActual);
}
