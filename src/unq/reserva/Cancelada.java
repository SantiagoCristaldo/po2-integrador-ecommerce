package unq.reserva;

import unq.catalogo.ItemCatalogo;

public class Cancelada extends EstadoDeReserva {

    public Cancelada(){
        this.nombreDeEstado = "CANCELADA";
    }

    @Override
    public void agregarItem(Reserva reserva, ItemCatalogo item) {
        throw new ExcepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void quitarItem(Reserva reserva, ItemCatalogo item) {
        throw new ExcepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void cancelar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva ya esta cancelada");
    }

    @Override
    public boolean esCancelada(){
        return true;
    }

    @Override
    public boolean cuentaComoVenta(){
        return false;
    }
}
