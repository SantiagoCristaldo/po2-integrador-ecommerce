package unq.metodosDePago;

public class ComprobantePago {
    private String tipo;
    private String codigoTransaccion;
    private double monto;

    public ComprobantePago(String tipo, String codigoTransaccion, double monto) {
        this.tipo = tipo;
        this.codigoTransaccion = codigoTransaccion;
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }

    public double getMonto() {
        return monto;
    }
}
