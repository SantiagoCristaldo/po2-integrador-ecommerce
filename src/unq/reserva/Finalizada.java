package unq.reserva;

import unq.catalogo.Catalogo;

public class Finalizada extends EstadoDeReserva {
    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("La reserva esta finalizada");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
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
