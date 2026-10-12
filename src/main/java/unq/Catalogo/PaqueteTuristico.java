package unq;

import java.util.ArrayList;
import java.util.List;

public class PaqueteTuristico implements Catalogo {
    private List<Catalogo> items = new ArrayList<>();
    private int descuento; // porcentaje: 10 significa 10%

    public PaqueteTuristico(int descuento) {
        this.descuento = descuento;
    }

    public void agregarItem(Catalogo item) {
        items.add(item);
    }

    public void quitarItem(Catalogo item) {
        items.remove(item);
    }

    @Override
    public double precioBase() {
        return items.stream()
                    .mapToDouble(Catalogo::precioBase)
                    .sum();
    }

    @Override
    public double precioFinal() {
        double sumaFinales = items.stream()
                                  .mapToDouble(Catalogo::precioFinal)
                                  .sum();
        return sumaFinales * (1 - descuento / 100.0);
    }
}
