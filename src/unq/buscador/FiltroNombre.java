package unq.buscador;

import  java.util.ArrayList;
import java.util.List;
import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;

public class FiltroNombre implements Filtro {
    private String texto;

    public FiltroNombre(String texto) {
        this.texto = texto;
    }

    @Override 
    public  boolean cumple(Catalogo item)
    return item.contieneEnNombre(texto);
    
} 