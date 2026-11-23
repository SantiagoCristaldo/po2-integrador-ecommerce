package unq.buscador;

import unq.catalogo.ItemCatalogo;

public class FiltroNombre implements Filtro {
    private String texto;

    public FiltroNombre(String texto) {
        this.texto = texto;
    }

    @Override 
    public  boolean cumple(ItemCatalogo item){
        return item.contieneEnNombre(texto);
    }
} 