package unq.traslado;

import unq.reserva.*;

public class PuntoEncuentro implements MetodoTranslado {
    private String ubicacion;
    private int cuposGuias;

    public PuntoEncuentro(String ubicacion, int cuposGuias) {
        this.ubicacion = ubicacion;
        this.cuposGuias = cuposGuias;
    }

    public double calcularCosto(Reserva reserva) {
        return 0; // no tiene costo
    }

    public boolean estaDisponible(Reserva reserva) {
        return cuposGuias > 0;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public asignarGuia(Reserva reserva){
        if(cuposGuias <= 0){
            throw new ExcepcionDominio("No hay mas guias para asignar");
        }else{
            cuposGuias -= reserva.cantidadDePasajeros();
        }
    }
}
