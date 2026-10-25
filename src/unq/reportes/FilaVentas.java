package unq.reportes;

import unq.catalogo.Catalogo;

public class FilaVentas {
    private Catalogo item;
    private int unidadesVendidas;
    private double totalCobrado;

    public FilaVentas(Catalogo item) {
        this.item = item;
    }

    void registrarVenta(double precioCobrado) {
        unidadesVendidas++;
        totalCobrado = totalCobrado +  precioCobrado;
    }

    public Catalogo getItem() {
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