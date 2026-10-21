package unq.buscador;

import  java.util.ArrayList;
import java.util.List;
import unq.catalogo.Catalogo;

public class BuscadorCatalogo {
    private List<Catalogo> items = new ArrayList<>();

    public BuscadorCatalogo(List<Catalogo> items) {
        this.items = items;
    }
    
    public void agregarItem(Catalogo item){
        items.add(item);
    }

    public List<Catalogo> buscar(Filtro filtro) {
        List<Catalogo> resultados = new ArrayList<>();

        for (Catalogo item : items){
            if (filtro.cumple(item)){
                resultados.add(item);
            }
        }
        return  resultados;
    }
}