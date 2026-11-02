package unq.reportes;

import unq.catalogo.Categoria;

public class FilaOcupacion {
    private static final double PORCENTAJE_TOTAL = 100.0;

    private Categoria categoria;
    private int cuposTotales;
    private int cuposReservados;

    public FilaOcupacion(Categoria categoria) {
        this.categoria = categoria;
    }

    void sumarCuposTotales(int cantidad) {
        cuposTotales = cuposTotales + cantidad;
    }

    void sumarCuposReservados(int cantidad) {
        cuposReservados = cuposReservados + cantidad;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public int getCuposTotales() {
        return cuposTotales;
    }

    public int getCuposReservados() {
        return cuposReservados;
    }

    public int getCuposLibres() {
        return Math.max(0, cuposTotales - cuposReservados);
    }

    public double getPorcentajeOcupacion() {
        if (cuposTotales == 0) {
            return 0;
        }

        return cuposReservados * PORCENTAJE_TOTAL / cuposTotales;
    }
}
