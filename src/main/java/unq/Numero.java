package unq;

public class Numero implements tipoValor {
    public Object field;

    @Override
    public boolean validate(Object valor) {
        return valor instanceof Number;
    }
}
