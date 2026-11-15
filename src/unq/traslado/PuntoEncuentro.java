package unq.traslado;

import unq.excepciones.*;
import unq.reserva.*;

public class PuntoEncuentro implements MetodoTraslado {
    private Direccion ubicacion;

    public PuntoEncuentro(Direccion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override 
    public double calcularCosto(Reserva reserva) {
        return 0; // no tiene costo
    }

    public boolean estaDisponible() {
        return ubicacion.getCuposDisponibles() > 0;
    }

    public Direccion getUbicacion() {
        return ubicacion;
    }

    public void asignarGuia(){
        if(!this.estaDisponible()){
            throw new ExcepcionDominio("No hay mas guias para asignar");
        }else{
            ubicacion.asignarGuia();
        }
    }
}
