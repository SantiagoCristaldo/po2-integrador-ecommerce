package unq.buscador;

import  java.util.ArrayList;
import java.util.List;
import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;

public class FiltroPrecioMaximo implements Filtro {

    private  double precioMaximo;
    
    public FiltroPrecioMaximo(double precioMaximo){
        this.precioMaximo = precioMaximo;
    }

    @Override 

    public  boolean cumple(Catalogo item) {
        
        return item.precioFinal() <= precioMaximo;
    }
} 