package unq.catalogo;

import java.time.LocalDate;
import java.util.List;

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

}
