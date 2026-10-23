package unq.traslado;

import unq.reserva.*;

public class TrasladoEstandar implements MetodoTraslado {
    private PlataformaTransporte plataforma;

    public TrasladoEstandar(PlataformaTransporte plataforma) {
        this.plataforma = plataforma;
    }

    public double calcularCosto(Reserva reserva) {

        int cantClientes = reserva.cantidadDePasajeros();
        Direccion direccionDestino = reserva.getDestino();

        return plataforma.estimarTraslado(cantClientes, direccionDestino);
    }

}
