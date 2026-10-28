package unq.traslado;

import unq.excepciones.*;
import unq.reserva.*;

public class PuntoEncuentro implements MetodoTraslado {
    private String ubicacion;
    private int cuposGuias;

    public PuntoEncuentro(String ubicacion, int cuposGuias) {
        this.ubicacion = ubicacion;
        this.cuposGuias = cuposGuias;
    }
    @Override 
    public double calcularCosto(Reserva reserva) {
        return 0; // no tiene costo
    }

    public boolean estaDisponible(Reserva reserva) { // preguntar que es mejor cupo de guias en reserva o aca.
        return cuposGuias > 0;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void asignarGuia(Reserva reserva){
        if(cuposGuias <= 0){
            throw new ExcepcionDominio("No hay mas guias para asignar");
        }else{
            cuposGuias -= reserva.cantidadDePasajeros(); // o puede ser menos 1 simplemente 
        }
    }
}
