package unq.metodosDePago;

import java.time.LocalDateTime;

import unq.excepciones.ExcepcionPagoInvalido;

public class TarjDeCredito extends MedioDePago {
    private int nroTarjeta;
    private int CVV;
    private LocalDateTime vencimiento;
    private APITarjetaDeCredito api;

    public TarjDeCredito(int nroTarjeta, int cvv, LocalDateTime vencimiento, double monto, String codigoDeTransaccion,
            APITarjetaDeCredito api) {
        super(monto, codigoDeTransaccion);
        this.nroTarjeta = nroTarjeta;
        this.CVV = cvv;
        this.vencimiento = vencimiento;
        this.api = api;
    }

    public int getNroTarjeta(){
        return this.nroTarjeta;
    }

    public int getCVV(){
        return this.CVV;
    }

    public LocalDateTime getVencimiento(){
        return this.vencimiento;
    }

    @Override
    public boolean validarDatos() {
        return api.validarCVV(nroTarjeta, CVV) && api.validarVencimiento(vencimiento);
    }

    @Override
    public void reservarFondos() {
        if (!api.preAutorizar(nroTarjeta, getMonto())) {
            throw new ExcepcionPagoInvalido("El banco emisor rechazo la pre-autorizacion");
        }
    }

    @Override
    public void ejecutarTransaccion() {
        api.transferirInmediata(getMonto());
    }

    @Override
    public void notificarResultado() {
        registrar(new ComprobantePago("Voucher", getCodigoTransaccion(), getMonto()));
    }
}
