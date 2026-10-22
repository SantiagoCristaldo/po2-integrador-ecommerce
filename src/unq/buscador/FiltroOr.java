package unq.buscador;


import unq.catalogo.Catalogo;

public class FiltroOr extends FiltroCompuesto {

    public FiltroOr(Filtro... criterios) {
        super(criterios);
    }
    
    @Override 
    public  boolean cumple(Catalogo item) {
        for (Filtro criterio : criterios) {
            if (criterio.cumple(item)){
                return true;
            }
        }
        return false;
    }
} 