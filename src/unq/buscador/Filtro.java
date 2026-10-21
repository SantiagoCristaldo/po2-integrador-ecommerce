package unq.buscador;

import unq.catalogo.Catalogo;

public interface Filtro {
    boolean cumple(Catalogo item);
}