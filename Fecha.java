import java.time.LocalDate;

public class Fecha implements tipoValor {
    public Object field;

    @Override
    public boolean validate(Object valor) {
        return valor instanceof LocalDate;
    }
}
