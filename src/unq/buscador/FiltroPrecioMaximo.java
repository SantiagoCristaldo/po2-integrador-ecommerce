package unq.buscador;

import unq.catalogo.ItemCatalogo;

public class FiltroPrecioMaximo implements Filtro {

    private  double precioMaximo;
    
    public FiltroPrecioMaximo(double precioMaximo){
        this.precioMaximo = precioMaximo;
    }

    @Override 

    public  boolean cumple(ItemCatalogo item) {
        
        return item.precioBase() <= precioMaximo;
    }
} 