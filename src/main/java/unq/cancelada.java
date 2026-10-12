package unq;

public class cancelada implements estadoDeReserva {
    private Reserva reserva;

    public cancelada(Reserva reserva) {
        this.reserva = reserva;
    }

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void cancelar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva ya esta cancelada");
    }

    public Reembolso generarReembolso() {
        return reserva.generarReembolso();
    }
}
