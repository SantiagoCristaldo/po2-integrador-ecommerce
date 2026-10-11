package unq;

public class enChecking implements estadoDeReserva {
    public Object field;

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("No se pueden agregar items durante el check-in");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("No se pueden quitar items durante el check-in");
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
        reserva.cambiarEstado(new enCurso());
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new exepcionPorInvalido("No se puede finalizar una reserva en check-in");
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.cambiarEstado(new cancelada());
    }

    public Object method() {
        return null;
    }
}
