import java.util.ArrayList;
import java.util.List;

public class PaqueteTuristico implements Catalogo {
    private double descuento;
    private List<Catalogo> items = new ArrayList<>();

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
        double base = precioBase();
        return base - (base * descuento);
    }

    public Object method(Object type) {
        return type;
    }
}
