package unq;

public class booleano implements tipoValor {
    public Object field;

    @Override
    public boolean validate(Object valor) {
        return valor instanceof Boolean;
    }
}
