package unq.notificaciones;

public interface ObservadorReserva {

    public void actualizar(EstadoDeReserva estadoAnterior, EstadoDeReserva estadoNuevo, Reserva reserva);
}
