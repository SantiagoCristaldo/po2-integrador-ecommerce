package unq.catalogo;

import java.util.List;

public interface Catalogo {
    String nombre();

    String descripcion();

    double precioBase();

    double precioFinal();

    boolean contieneEnNombre(String texto);

    boolean esDeCategoria(Categoria categoria);

    void decrementarCupo();

    List<ServicioIndividual> servicios();

    List<Catalogo> componentes();

    double factorDeDescuento();
}
