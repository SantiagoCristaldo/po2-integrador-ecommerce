package unq.translado;

import unq.reserva;

public interface MetodoTranslado {
    Reserva calcularCosto(Reserva reserva);

    Boolean estaDisponible(Reserva reserva);
}
