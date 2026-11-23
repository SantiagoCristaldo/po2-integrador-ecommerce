package unq.catalogo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import unq.excepciones.ExcepcionDominio;

public class ServicioIndividual implements ItemCatalogo {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String operador;
    private Categoria categoria;
    private double precioBase;
    private double descuentoPromocional;
    private Cronograma cronograma;
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
        this.cronograma = new Cronograma(cuposDisponibles);
    }

    public void aplicarDescuentoPromocional(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new ExcepcionDominio("El descuento promocional debe estar entre 0 y 100");
        }
        this.descuentoPromocional = porcentaje;
    }

    @Override
    public int valorMinimoDeLosCuposDisponibles() {
        return this.getCuposDisponibles();
    }

    @Override
    public boolean tieneCupo(LocalDate inicio, LocalDate fin, int cantidad) {
        return cronograma.tieneCupo(inicio, fin, cantidad);
    }

    @Override
    public void decrementarCupo(LocalDate inicio, LocalDate fin, int cantidad) {
        cronograma.decrementar(inicio, fin, cantidad);
    }

    @Override
    public void incrementarCupo(LocalDate inicio, LocalDate fin, int cantidad) {
        cronograma.incrementar(inicio, fin, cantidad);
    }

    @Override
    public int getCuposDisponibles() {
        return cronograma.cupoMaximo();
    }

    public int getCuposDisponiblesEn(LocalDate fecha) {
        return cronograma.cupoEn(fecha);
    }

    public void definirCupo(LocalDate fecha, int cupo) {
        cronograma.definirCupo(fecha, cupo);
    }

    public int cuposTotalesEntre(LocalDate inicio, LocalDate fin) {
        return cronograma.cuposTotalesEntre(inicio, fin);
    }

    public int cuposReservadosEntre(LocalDate inicio, LocalDate fin) {
        return cronograma.cuposReservadosEntre(inicio, fin);
    }

    @Override
    public double precioBase() {
        return precioBase;
    }

    @Override
    public double precioFinal() {
        return precioBase;
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
    public List<ItemCatalogo> componentes() {
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

    public void setValorAtributo(String nombreAtributo, String valor) {
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
        atributosDinamicos.forEach(AtributoDinamico::validar);
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
