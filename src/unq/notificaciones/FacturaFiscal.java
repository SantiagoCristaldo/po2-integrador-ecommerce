package unq.notificaciones;

import unq.reserva.*;

public class FacturaFiscal {
    private int numero;
    private double total;
    private Reserva reserva;

    public FacturaFiscal(int numero, double total, Reserva reserva) {
        this.numero = numero;
        this.total = total;
        this.reserva = reserva;
    }

    public int getNumero() {
        return numero;
    }

    public double getTotal() {
        return this.total;
    }

    public Reserva getReserva() {
        return reserva;
    }
}
