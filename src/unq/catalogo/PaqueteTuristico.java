package unq.catalogo;

import java.util.ArrayList;
import java.util.List;

import unq.excepciones.ExcepcionDominio;

public class PaqueteTuristico implements Catalogo {
    private String nombre;
    private String descripcion;
    private int descuento;
    private List<Catalogo> items = new ArrayList<>();

    public PaqueteTuristico(String nombre, String descripcion, int descuento) {
        if (descuento < 0 || descuento > 100) {
            throw new ExcepcionDominio("El descuento debe estar entre 0 y 100");
        }
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.descuento = descuento;
    }

    public void agregarItem(Catalogo item) {
        items.add(item);
    }

    public void quitarItem(Catalogo item) {
        items.remove(item);
    }

    // TODO: consultar al docente. El enunciado (texto) dice que el precio base del
    // paquete es suma x (1 - descuento), pero la tabla muestra base = suma y final = suma x (1 - descuento).
    // Se implementa segun la tabla.
    @Override
    public double precioBase() {
        return items.stream()
                .mapToDouble(Catalogo::precioBase)
                .sum();
    }

    @Override
    public double precioFinal() {
        double sumaFinales = items.stream()
                .mapToDouble(Catalogo::precioFinal)
                .sum();
        return sumaFinales * (1 - descuento / 100.0);
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
}
