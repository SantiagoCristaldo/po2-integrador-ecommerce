package unq.catalogo;

import java.util.ArrayList;
import java.util.List;

import unq.excepciones.ExcepcionDominio;

public class ServicioIndividual implements Catalogo {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String operador;
    private Categoria categoria;
    private double precioBase;
    private double descuentoPromocional;
    private int cuposDisponibles;
    private List<AtributoDinamico> atributosDinamicos = new ArrayList<>();

    public ServicioIndividual(String codigo, String nombre, String descripcion,
                              String operador, Categoria categoria, double precioBase, int cuposDisponibles) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.operador = operador;
        this.categoria = categoria;
        this.precioBase = precioBase;
        this.descuentoPromocional = 0;
        this.cuposDisponibles = cuposDisponibles;
    }

    public void aplicarDescuentoPromocional(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new ExcepcionDominio("El descuento promocional debe estar entre 0 y 100");
        }
        this.descuentoPromocional = porcentaje;
    }

    public void decrementarCupo(){
        this.cuposDisponibles--;
    }
    
    public int getCuposDisponibles(){
        return this.cuposDisponibles;
    }

    @Override
    public double precioBase() {
        return precioBase;
    }

    @Override
    public double precioFinal() {
        return precioBase * (1 - descuentoPromocional / 100.0);
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public String descripcion() {
        return descripcion;
    }

    @Override
    public boolean contieneEnNombre(String texto) {
        return nombre != null && nombre.toLowerCase().contains(texto.toLowerCase());
    }

    @Override 
    public boolean esDeCategoria(Categoria categoria) {
        return this.categoria.equals(categoria);
    }

    @Override
    public List<ServicioIndividual> servicios() {
        return List.of(this);
    }

    @Override
    public List<Catalogo> componentes() {
        return List.of();
    }

    @Override
    public double factorDeDescuento() {
        return 1.0;
    }

    public String getOperador() {
        return operador;
    }

    public Categoria getCategoria() {
        return categoria;
    }
 
    public void agregarAtributoDinamico(AtributoDinamico atributo) {
        if (atributosDinamicos.stream().anyMatch(a -> a.mismoNombreQue(atributo))) {
            throw new ExcepcionDominio("Ya existe un atributo dinámico con ese nombre");
        }
        atributosDinamicos.add(atributo);
    }

    public void setValorAtributo(String nombreAtributo, Object valor) {
        AtributoDinamico atributo = buscarAtributo(nombreAtributo);
        if (atributo == null) {
            throw new ExcepcionDominio("No existe el atributo dinámico '" + nombreAtributo + "'");
        }
        atributo.setValor(valor);
    }

    private AtributoDinamico buscarAtributo(String nombreAtributo) {
        return atributosDinamicos.stream()
                .filter(a -> a.tieneNombre(nombreAtributo))
                .findFirst()
                .orElse(null);
    }

    public void validar() {
        exigirTexto(codigo, "codigo");
        exigirTexto(nombre, "nombre");
        exigirTexto(operador, "operador");
        exigirNoNulo(categoria, "categoria");
        validarAtributosDinamicos();
    }

    private void validarAtributosDinamicos() {
        for (AtributoDinamico atributo : atributosDinamicos) {
            if (!atributo.validar()) {
                throw new ExcepcionDominio("Hay un atributo dinámico inválido");
            }
        }
    }

    private void exigirTexto(String valor, String atributo) {
        if (valor == null || valor.isBlank()) {
            throw new ExcepcionDominio("El atributo obligatorio '" + atributo + "' no tiene valor");
        }
    }

    private void exigirNoNulo(Object valor, String atributo) {
        if (valor == null) {
            throw new ExcepcionDominio("El atributo obligatorio '" + atributo + "' no tiene valor");
        }
    }
}
