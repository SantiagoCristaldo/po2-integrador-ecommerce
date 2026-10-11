public class solicitudPendiente implements estadoDeReserva {
    public Object field;

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        reserva.getItemsCatalago().add(item);
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
        reserva.getItemsCatalago().remove(item);
    }

    @Override
    public void confirmar(Reserva reserva) {
        reserva.cambiarEstado(new pagado());
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        throw new exepcionPorInvalido("No se puede hacer check-in en una solicitud pendiente");
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new exepcionPorInvalido("No se puede iniciar una solicitud pendiente");
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new exepcionPorInvalido("No se puede finalizar una solicitud pendiente");
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.cambiarEstado(new cancelada());
    }

    public Object method() {
        return null;
    }
}
