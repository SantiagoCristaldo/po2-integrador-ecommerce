package unq.reserva;

import unq.catalogo.Catalogo;

public interface EstadoDeReserva {
    void agregarItem(Reserva reserva, Catalogo item);

    void quitarItem(Reserva reserva, Catalogo item);

    void confirmar(Reserva reserva);

    void hacerCheckin(Reserva reserva);

    void iniciar(Reserva reserva);

    void finalizar(Reserva reserva);

    void cancelar(Reserva reserva);
}
