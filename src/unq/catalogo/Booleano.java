package unq.catalogo;

public class Booleano implements TipoValor {
    @Override
    public boolean esValido(Object valor) {
        return valor instanceof Boolean;
    }
}
