package unq;

import java.util.ArrayList;
import java.util.List;

public class ServicioIndividual implements Catalogo {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String operador;
    private Categoria categoria;
    private double precioBase;
    private double precioFinal;
    private List<AtributoDinamico> atributoDinamico = new ArrayList<>();

    public ServicioIndividual(String codigo, String nombre, String operador,
                              Categoria categoria, double precioBase) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.operador = operador;
        this.categoria = categoria;
        this.precioBase = precioBase;
        this.precioFinal = precioBase;
    }

    @Override
    public double precioBase() {
        return precioBase;
    }

    @Override
    public double precioFinal() {
        return precioFinal;
    }


    public void agregarAtributoDinamico(AtributoDinamico atributo) {
        if (atributoDinamico.stream().anyMatch(a -> a.mismoNombreQue(atributo))){
            throw new ExcepcionDominio("Ya existe un atributo dinámico con ese nombre");
    }
        atributoDinamico.add(atributo);

    }

    public void setValorAtributo(String nombreAtributo, Object valor) {
        AtributoDinamico atributo = buscarAtributo(nombreAtributo);
        if (atributo == null) {
            throw new ExcepcionDominio(
                "No existe el atributo dinámico '" + nombreAtributo + "'");
        }
        atributo.setValor(valor);
    }

    private AtributoDinamico buscarAtributo(String nombreAtributo) {
    return atributoDinamico.stream()
            .filter(a -> a.tieneNombre(nombreAtributo))
            .findFirst()
            .orElse(null);
}

    // ---------- validación ----------

    public void validar() {
        validarAtributosFijos();
        validarAtributosDinamicos();
    }

    private void validarAtributosFijos() {
        exigir(codigo, "codigo");
        exigir(nombre, "nombre");
    }

    private void exigir(String valor, String atributo) {
        if (valor == null || valor.isBlank()) {
            throw new ExcepcionDominio(
                "El atributo obligatorio '" + atributo + "' no tiene valor");
        }
    }

   
    private void validarAtributosDinamicos() {
        for (AtributoDinamico atributo : atributoDinamico) {
            if (!atributo.validar()) {
                throw new ExcepcionDominio("Hay un atributo dinámico inválido");
            }
        }
    }
}

