package unq.catalogo;

public interface Catalogo {
    String nombre();

    String descripcion();

    double precioBase();

    double precioFinal();

    boolean contieneEnNombre(String texto);
}
