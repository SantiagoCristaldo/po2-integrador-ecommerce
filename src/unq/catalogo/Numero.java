package unq.catalogo;

public class Numero implements TipoValor {
    @Override
    public boolean esValido(Object valor) {
        return valor instanceof Number;
    }
}
