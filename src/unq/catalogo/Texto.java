package unq.catalogo;

public class Texto implements TipoValor {
    @Override
    public boolean esValido(String valor) {
        return valor != null;
    }
}
