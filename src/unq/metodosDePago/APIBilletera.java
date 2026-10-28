package unq.metodosDePago;

public interface  APIBilletera {
    
    public boolean validarDatos(BilleteraVirtual api);

    public void bloquearElSaldo(String idCuenta, double monto);

    public void acreditarVendedor(String idCuenta, double monto);

    public void notificarPush(String idCuenta, double monto);
}
