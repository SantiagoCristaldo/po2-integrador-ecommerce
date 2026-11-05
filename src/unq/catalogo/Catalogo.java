package unq.catalogo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import unq.excepciones.ExcepcionDominio;

public interface Catalogo {
    String nombre();

    String descripcion();

    double precioBase();

    double precioFinal();

    double factorDeDescuento();

    boolean contieneEnNombre(String texto);

    boolean esDeCategoria(Categoria categoria);

    void decrementarCupo(LocalDate inicio, LocalDate fin, int cantidad);

    void incrementarCupo(LocalDate inicio, LocalDate fin, int cantidad);

    int getCuposDisponibles();

    int valorMinimoDeLosCuposDisponibles();

    List<ServicioIndividual> servicios();

    List<Catalogo> componentes();

    boolean tieneCupo(LocalDate inicio, LocalDate fin, int cantidad);

    /**
     * Descuenta el cupo de todos los ítems, o de ninguno: si alguno no tiene cupo, devuelve lo que ya
     * había descontado y relanza la excepción. Preguntar antes con tieneCupo no alcanza, porque un
     * mismo servicio puede aparecer más de una vez en la lista (suelto y dentro de un paquete).
     */
    static void decrementarCupoDeTodos(List<Catalogo> items, LocalDate inicio, LocalDate fin, int cantidad) {
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
}
