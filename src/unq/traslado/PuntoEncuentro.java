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

    public boolean estaDisponible(Direccion destino) { // preguntar que es mejor cupo de guias en reserva o aca.
        return destino.getCuposDisponibles() > 0;
    }

    public Direccion getUbicacion() {
        return ubicacion;
    }

    public void asignarGuia(Direccion destino){
        if(destino.getCuposDisponibles() <= 0){
            throw new ExcepcionDominio("No hay mas guias para asignar");
        }else{
            destino.asignarGuia(); 
        }
    }
}
