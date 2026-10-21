package unq.buscador;

import  java.util.ArrayList;
import java.util.List;
import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;

public class FiltroOr extends FiltroCompuesto {

    public FiltroOr(Filtro... criterios) {
        super(criterios);
    }
    
    @Override 
    public  boolean cumple(Catalogo item) {
        for (Filtro criterio : criterios) {
            if (criterio.cumple(item)){
                return True;
            }
        }
        return false;
    }
} 