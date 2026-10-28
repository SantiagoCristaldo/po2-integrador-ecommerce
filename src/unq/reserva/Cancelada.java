package unq.reserva;

import unq.catalogo.Catalogo;

public class Cancelada extends EstadoDeReserva {

    public Cancelada(){
        this.nombreDeEstado = "CANCELADA";
    }

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("La reserva esta cancelada");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
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
