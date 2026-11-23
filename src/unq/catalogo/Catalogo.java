package unq.catalogo;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenedor de los ítems que ofrece la empresa: servicios individuales y paquetes turísticos.
 */
public class Catalogo {
    private List<ItemCatalogo> items = new ArrayList<>();

    public Catalogo(ItemCatalogo... items) {
        this.items.addAll(List.of(items));
    }

    public void agregarItem(ItemCatalogo item) {
        items.add(item);
    }

    public void quitarItem(ItemCatalogo item) {
        items.remove(item);
    }

    public List<ItemCatalogo> getItems() {
        return new ArrayList<>(items);
    }

    public int cantidadDeItems() {
        return items.size();
    }
}
