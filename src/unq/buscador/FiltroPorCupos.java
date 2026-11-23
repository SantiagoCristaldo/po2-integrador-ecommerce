package unq.buscador;

import unq.catalogo.ItemCatalogo;

public class FiltroPorCupos implements Filtro {
    @Override
    public boolean cumple(ItemCatalogo item){
        return item.getCuposDisponibles() > 0;
    }
}
