package unq.buscador;


import unq.catalogo.ItemCatalogo;

public class FiltroOr extends FiltroCompuesto {

    public FiltroOr(Filtro... criterios) {
        super(criterios);
    }
    
    @Override 
    public  boolean cumple(ItemCatalogo item) {
        for (Filtro criterio : criterios) {
            if (criterio.cumple(item)){
                return true;
            }
        }
        return false;
    }
} 