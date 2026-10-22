package unq.buscador;

import unq.catalogo.Catalogo;

public class FiltroNombre implements Filtro {
    private String texto;

    public FiltroNombre(String texto) {
        this.texto = texto;
    }

    @Override 
    public  boolean cumple(Catalogo item){
        return item.contieneEnNombre(texto);
    }
} 