package unq.buscador;

import unq.catalogo.Catalogo;

public class FiltroPrecioMaximo implements Filtro {

    private  double precioMaximo;
    
    public FiltroPrecioMaximo(double precioMaximo){
        this.precioMaximo = precioMaximo;
    }

    @Override 

    public  boolean cumple(Catalogo item) {
        
        return item.precioBase() <= precioMaximo;
    }
} 