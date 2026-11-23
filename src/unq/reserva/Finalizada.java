package unq.reserva;

import unq.catalogo.ItemCatalogo;

public class Finalizada extends EstadoDeReserva {

    public Finalizada(){
        this.nombreDeEstado = "Finalizada";
    }

    @Override
    public void agregarItem(Reserva reserva, ItemCatalogo item) {
        throw new ExcepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void quitarItem(Reserva reserva, ItemCatalogo item) {
        throw new ExcepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void iniciar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva ya esta finalizada");
    }

    @Override
    public void cancelar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva esta finalizada");
    }

    @Override 
    public boolean debeNotificarEmail(){
        return true;
    }

    @Override 
    public boolean debeGenerarVoucher(){
        return true;
    } 
}
