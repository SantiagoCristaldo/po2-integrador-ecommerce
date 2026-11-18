package unq.metodosDePago;

import java.time.LocalDateTime;

public class PagoTransferencia extends MedioDePago {
    private int CBU;
    private String alias;
    private LocalDateTime fechaProgramada;
    private APIBancaria api;

    public PagoTransferencia(int CBU, String alias, LocalDateTime fechaProgramada, APIBancaria api, double monto, String codigoTransaccion){
        super(monto, codigoTransaccion);
        this.CBU = CBU;
        this.alias = alias;
        this.fechaProgramada = fechaProgramada;
        this.api = api;
    }

    public LocalDateTime getFechaProgramada(){
        return this.fechaProgramada;
    }

    public int getCBU(){
        return this.CBU;
    }

    public String getAlias(){
        return this.alias;
    }

    @Override 
    public boolean validarDatos(){
        return api.validarCBU(CBU) && api.validarAlias(alias);
    }

    @Override 
    public void reservarFondos(){

    }

    @Override
    public void ejecutarTransaccion(){
        if (fechaProgramada == null) {
            api.transferirDeInmediato(CBU, getMonto());
        } else {
            api.programarTransferencia(CBU, getMonto(), fechaProgramada);
        }
    } 

    @Override 
    public void notificarResultado(){
        registrar(new ComprobantePago("Comprobante CBU " + CBU, getCodigoTransaccion(), getMonto()));
    }
}
