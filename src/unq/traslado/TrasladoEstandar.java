package unq.traslado;

import unq.reserva.*;

public class TrasladoEstandar implements MetodoTraslado {
    private PlataformaTransporte plataforma;
    private Direccion destino;

    public TrasladoEstandar(PlataformaTransporte plataforma, Direccion destino) {
        this.plataforma = plataforma;
        this.destino = destino;
    }
    @Override 
    public double calcularCosto(Reserva reserva) {

        int cantClientes = reserva.cantidadDePasajeros();

        return plataforma.estimarTraslado(cantClientes, this.destino);
    }
}

