package unq;

public class finalizada implements estadoDeReserva {
    private Reserva reserva;

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
        throw new exepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva ya esta finalizada");
    }

    @Override
    public void cancelar(Reserva reserva) {
        throw new exepcionPorInvalido("La reserva esta finalizada");
    }

}
