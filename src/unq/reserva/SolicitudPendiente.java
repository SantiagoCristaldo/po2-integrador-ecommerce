package unq.reserva;

import unq.catalogo.ItemCatalogo;

public class SolicitudPendiente extends EstadoDeReserva {

    public SolicitudPendiente(){
        this.nombreDeEstado = "SolicitudPendiente";
    }

    @Override
    public void agregarItem(Reserva reserva, ItemCatalogo item) {
        reserva.agregarItemDirecto(item);
    }

    @Override
    public void quitarItem(Reserva reserva, ItemCatalogo item) {
        reserva.quitarItemDirecto(item);
    }

    @Override
    public void confirmar(Reserva reserva) {
        reserva.bloquearCupos();
        reserva.cambiarEstado(new Pagado());
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede hacer check-in en una solicitud pendiente");
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede iniciar una solicitud pendiente");
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede finalizar una solicitud pendiente");
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.cambiarEstado(new Cancelada());
    }

    @Override 
    public boolean cuentaComoVenta(){
        return false;
    }
}
