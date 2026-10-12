package unq;

public class enCurso implements estadoDeReserva {
    private Reserva reserva;

    public enCurso(Reserva reserva) {
        this.reserva = reserva;
    }

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("No se pueden agregar items a una reserva en curso");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("No se pueden quitar items de una reserva en curso");
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva ya esta confirmada");
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        throw new exepcionPorInvalido("El check-in ya fue realizado");
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva ya esta en curso");
    }

    @Override
    public void finalizar(Reserva reserva) {
        reserva.cambiarEstado(new finalizada());
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.cambiarEstado(new cancelada());
    }

}
