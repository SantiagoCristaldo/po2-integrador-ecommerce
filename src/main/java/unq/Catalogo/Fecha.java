package unq;

import java.time.LocalDate;

public class Fecha implements TipoValor {
    public Object field;

    @Override
    public boolean esValido(Object valor) {
        return valor instanceof LocalDate;
    }
}
