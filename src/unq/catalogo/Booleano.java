package unq.catalogo;

public class Booleano implements TipoValor {
    /**
     * Boolean.valueOf nunca falla (todo lo que no sea "true" lo toma como false), así que el valor
     * es válido sólo si al convertirlo y volverlo a texto se obtiene lo mismo: "true" o "false".
     */
    @Override
    public boolean esValido(String valor) {
        return valor != null && String.valueOf(Boolean.valueOf(valor)).equalsIgnoreCase(valor);
    }
}
