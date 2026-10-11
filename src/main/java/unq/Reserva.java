package unq;

import java.util.ArrayList;
import java.util.List;

public class Reserva {
    public estadoDeReserva estado;
    private List<Catalogo> itemsCatalago = new ArrayList<>();

    public Reserva() {
        this.estado = new solicitudPendiente();
    }

    public List<Catalogo> getItemsCatalago() {
        return itemsCatalago;
    }

    public void agregarItem(Catalogo item) {
        estado.agregarItem(this, item);
    }

    public void quitarItem(Catalogo item) {
        estado.quitarItem(this, item);
    }

    public void confirmar() {
        estado.confirmar(this);
    }

    public void hacerCheckin() {
        estado.hacerCheckin(this);
    }

    public void iniciar() {
        estado.iniciar(this);
    }

    public void finalizar() {
        estado.finalizar(this);
    }

    public void cancelar() {
        estado.cancelar(this);
    }

    public void cambiarEstado(estadoDeReserva nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void bloquearCupos() {
    }

    public void liberarCupos() {
    }

    public void reembolsar() {
    }
}
