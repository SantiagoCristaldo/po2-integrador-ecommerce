package unq.metodosDePago;

import java.time.LocalDateTime;

public class PagoTransferencia extends MedioDePago {
    private int CBU;
    private String alias;
    private LocalDateTime fechaProgramada;
    private APIBancaria api;

    public PagoTransferencia(int CBU, String alias, LocalDateTime fechaProgramada, APIBancaria api, float monto, String codigoTransaccion){
        super(monto, codigoTransaccion);
        this.CBU = CBU;
        this.alias = alias;
        this.fechaProgramada = fechaProgramada;
        this.api = api;
    }

    public LocalDateTime getFechaProgramada(){
        return this.fechaProgramada;
    }

    public String getAlias(){
        return this.alias;
    }

    @Override 
    public boolean validarDatos(){
        return api.validarDatos(this);
    }

    @Override 
    public void reservarFondos(){

    }

    @Override
    public void ejecutarTransaccion(){
        api.transferirDeInmediato(CBU, monto);
    } 

    @Override 
    public void notificarResultado(){
        // hay que hacerlo
    }
}
