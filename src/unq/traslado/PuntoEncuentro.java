package unq.traslado;

import unq.reserva.*;

public class PuntoEncuentro implements MetodoTranslado {
    private String ubicacion;
    private int cuposGuias;

    public PuntoEncuentro(String ubicacion, int cuposGuias) {
        this.ubicacion = ubicacion;
        this.cuposGuias = cuposGuias;
    }

    public float calcularCosto(Reserva reserva) {
        return 0; // no tiene costo
    }

    public boolean estaDisponible(Reserva reserva) {
        return cuposGuias > 0;
    }

    public String getUbicacion() {
        return ubicacion;
    }
}
