package unq.catalogo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Fecha implements TipoValor {
    /** Espera el formato ISO aaaa-mm-dd, por ejemplo "2026-11-12". */
    @Override
    public boolean esValido(String valor) {
        try {
            LocalDate.parse(valor);
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }
}
