package unq.catalogo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import unq.excepciones.ExcepcionDominio;

public class PaqueteTuristico implements Catalogo {
    private String nombre;
    private String descripcion;
    private int descuento;
    private List<Catalogo> items = new ArrayList<>();
    // tendriamos que sacar esto y poner q se calcule a partir de los items.

    public PaqueteTuristico(String nombre, String descripcion, int descuento, int cuposDisponibles) {
        if (descuento < 0 || descuento > 100) {
            throw new ExcepcionDominio("El descuento debe estar entre 0 y 100");
        }
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.descuento = descuento;
    }

    public void agregarItem(Catalogo item) {
        if (item == this || contieneA(item, this)) {
            throw new ExcepcionDominio("Un paquete no puede contenerse a sí mismo");
        }
        items.add(item);
    }

    @Override
    public boolean tieneCupo(LocalDate inicio, LocalDate fin, int cantidad) {
        return items.stream().allMatch(i -> i.tieneCupo(inicio, fin, cantidad));
    }

    @Override
    public void decrementarCupo(LocalDate inicio, LocalDate fin, int cantidad) {
        List<Catalogo> decrementados = new ArrayList<>();
        try {
            for (Catalogo item : items) {
                item.decrementarCupo(inicio, fin, cantidad);
                decrementados.add(item);
            }
        } catch (ExcepcionDominio e) {
            decrementados.forEach(item -> item.incrementarCupo(inicio, fin, cantidad));
            throw e;
        }
    }

    @Override
    public void incrementarCupo(LocalDate inicio, LocalDate fin, int cantidad) {
        items.forEach(i -> i.incrementarCupo(inicio, fin, cantidad));
    }

    @Override
    public int valorMinimoDeLosCuposDisponibles() {
        if (items.isEmpty()) {
            return 0; // O si el paquete está vacío
        }

        return items.stream()
                .mapToInt(Catalogo::valorMinimoDeLosCuposDisponibles)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    public int getCuposDisponibles() {

        return this.valorMinimoDeLosCuposDisponibles();
    }

    public void quitarItem(Catalogo item) {
        items.remove(item);
    }

    @Override
    public double precioBase() {
        return items.stream()
                .mapToDouble(Catalogo::precioFinal)
                .sum();
    }

    @Override
    public double precioFinal() {
        double sumaFinales = this.precioBase();
        return sumaFinales - (sumaFinales * (descuento / 100.0));
    }

    @Override
    public double factorDeDescuento() {
        return 1 - descuento / 100.0;
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
        for (Catalogo item : items) {
            if (item.esDeCategoria(categoria)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Catalogo> componentes() {
        return new ArrayList<>(items);
    }

    private static boolean contieneA(Catalogo contenedor, Catalogo buscado) {
        for (Catalogo componente : contenedor.componentes()) {
            if (componente == buscado || contieneA(componente, buscado)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ServicioIndividual> servicios() {
        List<ServicioIndividual> servicios = new ArrayList<>();
        for (Catalogo item : items) {
            servicios.addAll(item.servicios());
        }
        return servicios;
    }
}
