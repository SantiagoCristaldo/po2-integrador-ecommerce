package unq.reserva;

import unq.catalogo.Catalogo;

public class Pagado extends EstadoDeReserva {

    public Pagado(){
        this.nombreDeEstado = "Pagado";
    }

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("No se pueden agregar items a una reserva pagada");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("No se pueden quitar items de una reserva pagada");
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva ya esta confirmada");
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        reserva.cambiarEstado(new EnChecking());
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede iniciar una reserva pagada sin check-in");
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede finalizar una reserva pagada");
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.liberarCupos();
        reserva.cambiarEstado(new Cancelada());
    }

    @Override 
    public boolean debeNotificarEmail(){
        return true;
    }
}
