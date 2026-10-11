package unq;

public class texto implements tipoValor {
    public Object field;

    @Override
    public boolean validate(Object valor) {
        return valor instanceof String && !((String) valor).isBlank();
    }
}
