package unq.buscador;

import unq.catalogo.Catalogo;

public class FiltroPorCupos implements Filtro {
    boolean cumple(Catalogo item){
        return item.destino.cuposGuias() > 0; 
    }
}
