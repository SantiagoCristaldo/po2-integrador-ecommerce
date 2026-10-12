package unq;

public class booleano implements TipoValor {
    public Object field;

    @Override
    public boolean esValido(Object valor) {
        return valor instanceof Boolean;
    }
}
