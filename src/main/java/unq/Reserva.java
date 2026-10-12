package unq;

import java.util.ArrayList;
import java.util.List;

public class Reserva {
    public estadoDeReserva estado;
    private List<Catalogo> itemsCatalago = new ArrayList<>();

    public Reserva() {
        this.estado = new solicitudPendiente();
    }

    public void agregarItem(Catalogo item) {
        estado.agregarItem(item);
    }

    public void quitarItem(Catalogo item) {
        estado.quitarItem(item);
    }

    public void confirmar() {
        estado.confirmar();
    }

    public void hacerCheckin() {
        estado.hacerCheckin();
    }

    public void iniciar() {
        estado.iniciar();
    }

    public void finalizar() {
        estado.finalizar();
    }

    public void cancelar() {
        estado.cancelar();
    }

    public void cambiarEstado(estadoDeReserva nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void bloquearCupos() {
        this.estado.bloquearCupos();
    }

    public void liberarCupos() {
        this.estado.liberarCupos();
    }

    public void reembolsar() {

    }
}
