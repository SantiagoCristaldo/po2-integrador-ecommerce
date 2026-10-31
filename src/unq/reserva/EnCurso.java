package unq.reserva;

import unq.catalogo.Catalogo;

public class EnCurso extends EstadoDeReserva {

    public EnCurso(){
        this.nombreDeEstado = "EnCurso";
    }

    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("No se pueden agregar items a una reserva en curso");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("No se pueden quitar items de una reserva en curso");
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva ya esta confirmada");
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        throw new ExcepcionPorInvalido("El check-in ya fue realizado");
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva ya esta en curso");
    }

    @Override
    public void finalizar(Reserva reserva) {
        reserva.cambiarEstado(new Finalizada());
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.reembolsarServicios();
        reserva.cambiarEstado(new Cancelada());
    }

    @Override 
    public boolean debeNotificarEmail(){
        return true;
    }
}
