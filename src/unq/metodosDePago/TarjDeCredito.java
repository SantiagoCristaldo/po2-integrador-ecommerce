package unq.metodosDePago;

import java.time.LocalDateTime;

public class TarjDeCredito extends MedioDePago {
    private int nroTarjeta;
    private int CVV;
    private LocalDateTime vencimiento;
    private APITarjetaDeCredito api;

    public TarjDeCredito(int nroTarjeta, int cvv, LocalDateTime vencimiento, float monto, String codigoDeTransaccion,
            APITarjetaDeCredito api) {
        super(monto, codigoDeTransaccion);
        this.nroTarjeta = nroTarjeta;
        this.CVV = cvv;
        this.vencimiento = vencimiento;
        this.api = api;
    }

    public int getCVV(){
        return this.CVV;
    }

    public LocalDateTime getVencimiento(){
        return this.vencimiento;
    }

    @Override
    public boolean validarDatos() {
        return api.validarDatos(this);
    }

    @Override
    public void reservarFondos() {
        api.preAutorizar(nroTarjeta, monto);
    }

    @Override
    public void ejecutarTransaccion() {
        api.transferirInmediata(monto);
    }

    @Override
    public void notificarResultado() {

    }
}
