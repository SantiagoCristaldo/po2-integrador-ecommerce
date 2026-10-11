public class exepcionPorInvalido extends RuntimeException implements estadoDeReserva {
    public Object field;

    public exepcionPorInvalido(String mensaje) {
        super(mensaje);
    }

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
    }

    @Override
    public void confirmar(Reserva reserva) {
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
    }

    @Override
    public void iniciar(Reserva reserva) {
    }

    @Override
    public void finalizar(Reserva reserva) {
    }

    @Override
    public void cancelar(Reserva reserva) {
    }

    public Object method() {
        return null;
    }
}
