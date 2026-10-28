package unq.traslado;

import unq.reserva.Reserva;

public class TrasladoChauffeur implements MetodoTraslado {
    private TrasladoVIP servicioVIP;

    public TrasladoChauffeur(TrasladoVIP servicioVIP) {
        this.servicioVIP = servicioVIP;
    }
    @Override 
    public double calcularCosto(Reserva reserva) {
        return servicioVIP.calcularCosto(reserva.valorServicios());
    }
}
