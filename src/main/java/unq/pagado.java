package unq;

public class pagado implements estadoDeReserva {
    private Reserva reserva;

    public pagado(Reserva reserva) {
        this.reserva = reserva;
    }

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("No se pueden agregar items a una reserva pagada");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("No se pueden quitar items de una reserva pagada");
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva ya esta confirmada");
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        reserva.cambiarEstado(new enChecking());
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new exepcionPorInvalido("No se puede iniciar una reserva pagada sin check-in");
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new exepcionPorInvalido("No se puede finalizar una reserva pagada");
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.cambiarEstado(new cancelada());
    }

}
