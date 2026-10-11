package unq;

public interface tipoValor {
    Object field1 = null;
    Object field2 = null;

    boolean validate(Object valor);

    default Object method2(Object param1, Object param2) {
        return null;
    }
}
