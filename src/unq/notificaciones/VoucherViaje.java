package unq.notificaciones;
import unq.reserva.Reserva;

public class VoucherViaje {
    private int codigo;
    private Reserva reserva;

    public VoucherViaje(int codigo,Reserva reserva) {
        this.codigo = codigo;
        this.reserva = reserva;
    }

    public int getCodigo() {
        return codigo;
    }

    public Reserva getReserva() {
        return reserva;
    }
}
