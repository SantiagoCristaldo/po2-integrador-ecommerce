package unq.metodosDePago;

import java.util.ArrayList;
import java.util.List;

import unq.excepciones.ExcepcionPagoInvalido;

public abstract class MedioDePago {
    private double monto;
    private String codigoTransaccion;
    private List<ComprobantePago> comprobantes = new ArrayList<>();

    public MedioDePago(double monto, String codigoTransaccion){

        this.monto = monto;
        this.codigoTransaccion = codigoTransaccion;
    }

    public String getCodigoTransaccion(){
        return this.codigoTransaccion;
    }

    public double getMonto(){
        return this.monto;
    }

    public final void procesoDePago(){
        if (this.validarDatos()){
            this.reservarFondos();
            this.ejecutarTransaccion();
            this.notificarResultado();
        }else{
            throw new ExcepcionPagoInvalido("los datos no fueron validados correctamente");
        }
    }

    public void setMonto(double monto){
        this.monto = monto;
    }

    public abstract boolean validarDatos();
    public abstract void reservarFondos();
    public abstract void ejecutarTransaccion();

    // por defecto registra el codigo de transaccion; cada medio puede sobrescribirlo
    public void notificarResultado(){
        registrar(new ComprobantePago("Registro de transaccion", codigoTransaccion, monto));
    }

    protected void registrar(ComprobantePago comprobante){
        comprobantes.add(comprobante);
    }

    public List<ComprobantePago> getComprobantes(){
        return new ArrayList<>(comprobantes);
    }

}
