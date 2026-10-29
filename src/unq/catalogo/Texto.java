package unq.catalogo;

public class Texto implements TipoValor {
    @Override
    public boolean esValido(Object valor) {
        return String.class.isInstance(valor);
    }
}
