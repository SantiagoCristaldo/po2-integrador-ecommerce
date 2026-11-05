package unq.catalogo;

import unq.excepciones.ExcepcionDominio;

public class AtributoDinamico {
    private String nombre;
    private boolean obligatorio;
    private Object valor;
    private TipoValor tipoValor;

    public AtributoDinamico(String nombre, boolean obligatorio, TipoValor tipoValor, Object valor) {
        this.nombre = nombre;
        this.obligatorio = obligatorio;
        this.tipoValor = tipoValor;
        validar(valor);
        this.valor = valor;
    }

    public void setValor(Object valor) {
        validar(valor);
        this.valor = valor;
    }

    private void validar(Object valor) {
        if (valor == null && obligatorio) {
            throw new ExcepcionDominio("El atributo dinámico '" + nombre + "' es obligatorio");
        }
        if (!tipoValor.esValido(valor)) {
            throw new ExcepcionDominio("El valor del atributo dinámico '" + nombre + "' no es del tipo esperado");
        }
    }

    public boolean tieneNombre(String nombre) {
        return this.nombre != null && this.nombre.equals(nombre);
    }

    public boolean mismoNombreQue(AtributoDinamico otro) {
        return otro.tieneNombre(this.nombre);
    }
}
