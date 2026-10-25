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

    @Override
    public boolean esDeCategoria(Categoria categoria) {
        for (Catalogo item :items){
            if (item.esDeCategoria(categoria)){
                return true;
            }
        }
        return false;
    }
    
    @Override 
    public List<Catalogo> componentes(){
        return new ArrayList<>(items);
    }

    @Override 
    public double factorDeDescuento(){
        return 1 - descuento / 100;
    }

    @Override 
    public List<ServicioIndividual> servicios(){
        List<ServicioIndividual> servicios = new ArrayList<>();
        for (Catalogo item : items){
            servicios.addAll(item.servicios());
        }
        return servicios; 
    }
}
