package unq.reportes;

public class FilaExposicon {
    private String operador;
    private int cantidadItems;
    private double porcentajeCatalogo;

    public FilaExposicon(String operador, int cantidadItems, double porcentajeCatalogo) {
        this.operador = operador;
        this.cantidadItems = cantidadItems;
        this.porcentajeCatalogo = porcentajeCatalogo;
    }

    public String getOperador() {
        return operador;
    }

    public int getCantidadItems() {
        return cantidadItems;
    }

    public double getPorcentajeCatalogo() {
        return porcentajeCatalogo;
    }
}