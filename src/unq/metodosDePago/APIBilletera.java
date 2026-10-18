package unq.metodosDePago;

public interface  APIBilletera {
    
    public boolean validarDatos(BilleteraVirtual api);

    public void bloquearElSaldo(String idCuenta, float monto);

    public void acreditarVendedor(String idCuenta, float monto);

    public void notificarPush(String idCuenta, float monto);
}
