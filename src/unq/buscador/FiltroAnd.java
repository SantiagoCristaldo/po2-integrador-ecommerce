package unq.buscador;

import unq.catalogo.ItemCatalogo;

public class FiltroAnd extends FiltroCompuesto {
    public FiltroAnd(Filtro... criterios){
        super(criterios);
    }

    @Override 
    public boolean cumple(ItemCatalogo item){
        for (Filtro criterio : criterios){
            if (!criterio.cumple(item)) {
                return false;
            }
        }
        return true;
    }
}