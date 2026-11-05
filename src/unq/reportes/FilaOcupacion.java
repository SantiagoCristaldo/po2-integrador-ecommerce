package unq.reportes;

import unq.catalogo.Categoria;

public class FilaOcupacion {
    private Categoria categoria;
    private int cuposTotales;
    private int cuposReservados;

    public FilaOcupacion(Categoria categoria) {
        this.categoria = categoria;
    }

    void sumar(int totales, int reservados) {
        cuposTotales += totales;
        cuposReservados += reservados;
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
        return cuposTotales - cuposReservados;
    }
}
