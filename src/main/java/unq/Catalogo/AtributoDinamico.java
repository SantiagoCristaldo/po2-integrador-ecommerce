package unq;

public class AtributoDinamico {
    private String nombre;
    private boolean obligatorio;
    private Object valor;
    private TipoValor tipoValor;

    public AtributoDinamico(String nombre, boolean obligatorio, TipoValor tipoValor) {
        this.nombre = nombre;
        this.obligatorio = obligatorio;
        this.tipoValor = tipoValor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public boolean validar() {
        if (valor == null) {
            return !obligatorio;
        }
        return tipoValor.esValido(valor);
    }

    public boolean tieneNombre(String nombre) {
        return this.nombre.equals(nombre);
    }

    public boolean mismoNombreQue(AtributoDinamico otro) {
        return otro.tieneNombre(this.nombre);
    }
}
