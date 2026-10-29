package unq.buscador;

import unq.catalogo.Catalogo;

public class FiltroPorCupos implements Filtro {
    @Override
    public boolean cumple(Catalogo item){
        return item.getCuposDisponibles() > 0;
    }
}
