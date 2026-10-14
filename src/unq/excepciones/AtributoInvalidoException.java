package unq.excepciones;

public class AtributoInvalidoException extends ExcepcionDominio {
    public AtributoInvalidoException(String atributo) {
        super("El atributo inválido '" + atributo + "' no tiene un valor válido");
    }
}
