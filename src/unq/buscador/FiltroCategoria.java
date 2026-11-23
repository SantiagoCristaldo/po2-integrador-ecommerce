package unq.buscador;

import unq.catalogo.ItemCatalogo;
import unq.catalogo.Categoria;

public class FiltroCategoria implements Filtro {
    private Categoria categoria;

    public FiltroCategoria(Categoria categoria){
        this.categoria = categoria;
    }

    @Override 
    public boolean cumple(ItemCatalogo item){
        return  item.esDeCategoria(categoria);
    }
}