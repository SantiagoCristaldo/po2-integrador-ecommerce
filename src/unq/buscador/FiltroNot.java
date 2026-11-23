package unq.buscador;


import unq.catalogo.ItemCatalogo;

public class FiltroNot implements Filtro {
    private Filtro criterio;

    public FiltroNot(Filtro criterio) {
        this.criterio = criterio;
    }
    
    @Override 
    public  boolean cumple(ItemCatalogo item) {
        return !criterio.cumple(item);
    }
} 