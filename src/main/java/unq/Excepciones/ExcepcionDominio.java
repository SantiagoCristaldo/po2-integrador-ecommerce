package unq;

public class ExcepcionDominio extends RuntimeException {
    public ExcepcionDominio(String mensaje) {
        super(mensaje);
    }
}