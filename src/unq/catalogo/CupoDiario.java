package unq.catalogo;

/**
 * Los cupos de una fecha del cronograma: los que siguen libres y los que ya están reservados.
 * Se guardan juntos para que no puedan desincronizarse.
 */
public class CupoDiario {
    private int libres;
    private int reservados;

    public CupoDiario(int libres) {
        this.libres = libres;
        this.reservados = 0;
    }

    public int getLibres() {
        return libres;
    }

    public int getReservados() {
        return reservados;
    }

    public void definirLibres(int libres) {
        this.libres = libres;
    }

    /** Pasa cupos de libres a reservados; con una cantidad negativa los libera. */
    public void reservar(int cantidad) {
        libres -= cantidad;
        reservados += cantidad;
    }
}
