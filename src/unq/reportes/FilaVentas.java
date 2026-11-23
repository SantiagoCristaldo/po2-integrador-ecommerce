package unq.reportes;

import unq.catalogo.ItemCatalogo;

public class FilaVentas {
    private ItemCatalogo item;
    private int unidadesVendidas;
    private double totalCobrado;

    public FilaVentas(ItemCatalogo item) {
        this.item = item;
    }

    void registrarVenta(double precioCobrado) {
        unidadesVendidas++;
        totalCobrado = totalCobrado +  precioCobrado;
    }

    public ItemCatalogo getItem() {
        return item;
    }

    public int getUnidadesVendidas() {
        return unidadesVendidas;
    }

    public double getPrecioPromedioCobrado() {
        if (unidadesVendidas == 0) {
            return 0;
        }

        return totalCobrado / unidadesVendidas;
    }
}