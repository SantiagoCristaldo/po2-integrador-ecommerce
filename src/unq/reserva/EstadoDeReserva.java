package unq.reserva;

import unq.catalogo.Catalogo;

public abstract class EstadoDeReserva {
    public String nombreDeEstado;

    public String nombre(){
        return this.nombreDeEstado;
    }

    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("No se pueden agregar items en el estado " + this.nombre());
    }

    public void quitarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("No se pueden quitar items en el estado " + this.nombre());
    }

    public void confirmar(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede confirmar en el estado " + this.nombre());
    }

    public void hacerCheckin(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede hacer check-in en el estado " + this.nombre());
    }

    public void iniciar(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede iniciar en el estado " + this.nombre());
    }

    public void finalizar(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede finalizar en el estado " + this.nombre());
    }

    public void cancelar(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede cancelar en el estado " + this.nombre());
    }

    public boolean debeNotificarEmail(){
        return false;
    }
    
    public boolean debeGenerarVoucher(){
        return false;
    }

    public boolean esCancelada(){
        return false;
    }
    
    public boolean cuentaComoVenta(){
        return true;
    }
}
