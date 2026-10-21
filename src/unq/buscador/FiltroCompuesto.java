package unq.buscador;

import  java.util.ArrayList;
import java.util.List;
import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;

public abstract class FiltroCompuesto implements Filtro {
    protected List<Filtro> criterios = new ArrayList<>();

    public FiltroCompuesto(Filtro... criterios) {
        for (Filtro criterio : criterios){
            this.criterios.add(criterio);
        }
    }

    public void agregar(Filtro criterio){
        criterios.add(criterio);
    }

    
} 