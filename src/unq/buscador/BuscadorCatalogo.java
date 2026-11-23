package unq.buscador;

import  java.util.ArrayList;
import java.util.List;
import unq.catalogo.Catalogo;
import unq.catalogo.ItemCatalogo;

public class BuscadorCatalogo {
    private Catalogo catalogo;

    public BuscadorCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public void agregarItem(ItemCatalogo item){
        catalogo.agregarItem(item);
    }

    public List<ItemCatalogo> buscar(Filtro filtro) {
        List<ItemCatalogo> resultados = new ArrayList<>();

        for (ItemCatalogo item : catalogo.getItems()){
            if (filtro.cumple(item)){
                resultados.add(item);
            }
        }
        return  resultados;
    }
}
