package unq.metodosDePago;

public class BilleteraVirtual extends MedioDePago {
    private String idCuenta;
    private APIBilletera api;

    public BilleteraVirtual(double monto, APIBilletera api, String idCuenta, String codigoTransaccion){
        super(monto, codigoTransaccion);
        this.api = api;
        this.idCuenta = idCuenta;
    }

    public String getIdCuenta(){
        return this.idCuenta;
    }

    @Override 
    public boolean validarDatos(){
       return api.validarDatos(this);
    }

    @Override 
    public void reservarFondos(){
        api.bloquearElSaldo(idCuenta, getMonto());
    }

    @Override 
    public void ejecutarTransaccion(){
        api.acreditarVendedor(idCuenta, getMonto());
    }

    @Override 
    public void notificarResultado(){
        api.notificarPush(idCuenta, getMonto());
    }
}
