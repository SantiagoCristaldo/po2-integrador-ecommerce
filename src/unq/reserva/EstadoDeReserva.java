package unq.reserva;

import unq.catalogo.Catalogo;

public abstract class EstadoDeReserva {
    public void agregarItem(Reserva reserva, Catalogo item){}

    public void quitarItem(Reserva reserva, Catalogo item){}

    public void confirmar(Reserva reserva){}

    public void hacerCheckin(Reserva reserva){}

    public void iniciar(Reserva reserva){}

    public void finalizar(Reserva reserva){}

    public void cancelar(Reserva reserva){}

    public boolean debeNotificarEmail(){
        return false;
    }
    
    public boolean debeGenerarVoucher(){
        return false;
    }
}
