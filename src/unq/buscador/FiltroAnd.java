package unq.buscador;

import  java.util.ArrayList;
import java.util.List;
import unq.catalogo.Catalogo;

public class FiltroAnd extends FiltroCompuesto {
    public FiltroAnd(Filtro... criterios){
        super(criterios);
    }

    @Override 
    public boolean cumple(Catalogo item){
        for (Filtro criterio : criterios){
            if (!criterio.cumple(item)) {
                return false;
            }
        }
    }
}