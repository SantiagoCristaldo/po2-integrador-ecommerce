package unq.catalogo;

import java.time.LocalDate;

public class Fecha implements TipoValor {
    @Override
    public boolean esValido(Object valor) {
        return valor instanceof LocalDate;
    }
}
