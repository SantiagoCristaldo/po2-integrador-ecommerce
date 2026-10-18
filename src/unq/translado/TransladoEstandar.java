package unq.translado;

public class TransladoEstandar implements MetodoTranslado {
    private PlataformaTransporte plataforma;

    public TransladoEstandar(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public float calcularCosto(Reserva reserva) {
        int cantClientes = reserva.clientes();
        Direccion direccionDestino = reserva.destino();
    }

    public boolean estaDisponible(Reserva reserva) {

    }
}
