package unq.catalogo;

public class Numero implements TipoValor {
    @Override
    public boolean esValido(String valor) {
        try {
            Double.valueOf(valor);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
}
