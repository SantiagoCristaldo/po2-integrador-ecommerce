package unq.buscador;

import  java.util.ArrayList;
import java.util.List;
import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;

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