package unq.buscador;


import unq.catalogo.Catalogo;

public class FiltroNot implements Filtro {
    private Filtro criterio;

    public FiltroNot(Filtro criterio) {
        this.criterio = criterio;
    }
    
    @Override 
    public  boolean cumple(Catalogo item) {
        return !criterio.cumple(item);
    }
} 