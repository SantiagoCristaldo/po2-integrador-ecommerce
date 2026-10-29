package unq.catalogo;

public class Numero implements TipoValor {
    @Override
    public boolean esValido(Object valor) {
        return Number.class.isInstance(valor);
    }
}
