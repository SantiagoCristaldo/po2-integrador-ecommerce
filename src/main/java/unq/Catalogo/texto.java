package unq.Catalogo;

public class texto implements TipoValor {
    public Object field;

    @Override
    public boolean esValido(Object valor) {
        return valor instanceof String && !((String) valor).isBlank();
    }
}
