package unq.reserva;

public class ExcepcionPorInvalido extends RuntimeException {
    public ExcepcionPorInvalido(String mensaje) {
        super(mensaje);
    }
}
