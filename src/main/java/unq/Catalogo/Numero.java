package unq;

public class Numero implements TipoValor {
    public Object field;

    @Override
    public boolean esValido(Object valor) {
        return valor instanceof Number;
    }
}
