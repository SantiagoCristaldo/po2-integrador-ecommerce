package unq.reserva;

import unq.excepciones.ExcepcionDominio;

public class ExcepcionPorInvalido extends ExcepcionDominio {
    public ExcepcionPorInvalido(String mensaje) {
        super(mensaje);
    }
}
