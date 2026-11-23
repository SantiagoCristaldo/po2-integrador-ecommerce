package unq.buscador;

import unq.catalogo.ItemCatalogo;

public interface Filtro {
    boolean cumple(ItemCatalogo item);
}