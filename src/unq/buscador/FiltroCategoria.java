package unq.buscador;

import  java.util.ArrayList;
import java.util.List;
import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;

public class FiltroCategoria implements Filtro {
    private Categoria categoria;

    public FiltroCategoria(Categoria categoria){
        this.categoria = categoria;
    }

    @Override 
    public boolean cumple(Catalogo item){
        return  item.esDeCategoria(categoria);
    }
}