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

    @Override
    public double precioBase() {
        return precioBase;
    }

    @Override
    public double precioFinal() {
        return precioFinal;
    }

    public void validar() {
        for (AtributoDinamico atributo : atributoDinamico) {
            atributo.validate();
        }
    }
}
