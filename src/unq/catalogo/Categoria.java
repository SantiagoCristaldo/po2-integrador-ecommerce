package unq.catalogo;

import java.util.Objects;

public class Categoria {
    private String nombre;

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public boolean seLlama(String nombre) {
        return this.nombre != null && this.nombre.equals(nombre);
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Categoria)) {
            return false;
        }
        Categoria categoria = (Categoria) otro;
        return Objects.equals(nombre, categoria.nombre);
    }
}
