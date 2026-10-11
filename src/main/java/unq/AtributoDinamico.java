package unq;

public class AtributoDinamico {
    private String nombre;
    private boolean obligatoriedad;
    private Object valor;
    private tipoValor tipoValor;

    public void setTipoValor(tipoValor tipoValor) {
        this.tipoValor = tipoValor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public boolean validate() {
        if (valor == null) {
            return !obligatoriedad;
        }
        return tipoValor.validate(valor);
    }
}
