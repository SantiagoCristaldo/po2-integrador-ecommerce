package unq.catalogo;

import java.util.ArrayList;
import java.util.List;

import unq.excepciones.ExcepcionDominio;

public class PaqueteTuristico implements Catalogo {
    private String nombre;
    private String descripcion;
    private int descuento;
    private List<Catalogo> items = new ArrayList<>();
    private int cuposDisponibles;

    public PaqueteTuristico(String nombre, String descripcion, int descuento, int cuposDisponibles) {
        if (descuento < 0 || descuento > 100) {
            throw new ExcepcionDominio("El descuento debe estar entre 0 y 100");
        }
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.cuposDisponibles = cuposDisponibles;
    }

    public void agregarItem(Catalogo item) {
        items.add(item);
        item.decrementarCupo();
    }

    public void decrementarCupo() {
        this.cuposDisponibles--;
    }

    public int getCuposDisponibles() {
        return this.cuposDisponibles;
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
        return sumaFinales - (sumaFinales * (descuento / 100));
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

    @Override
    public double factorDeDescuento() {
        return 1 - descuento / 100;
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
