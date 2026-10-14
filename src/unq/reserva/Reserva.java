package unq.reserva;

import java.util.ArrayList;
import java.util.List;

import unq.catalogo.Catalogo;

public class Reserva {
    private EstadoDeReserva estado;
    private List<Catalogo> items = new ArrayList<>();

    public Reserva() {
        this.estado = new SolicitudPendiente();
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

    public void cambiarEstado(EstadoDeReserva nuevoEstado) {
        this.estado = nuevoEstado;
    }

    void agregarItemDirecto(Catalogo item) {
        items.add(item);
    }

    void quitarItemDirecto(Catalogo item) {
        items.remove(item);
    }

    public void bloquearCupos() {
    }

    public void liberarCupos() {
    }

    public void reembolsar() {
    }
}
