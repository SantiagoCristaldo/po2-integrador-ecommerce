package unq.metodosDePago;

import unq.excepciones.ExcepcionDominio;

public abstract class MedioDePago {
    public  float monto;
    private String codigoTransaccion;
    
    public MedioDePago(float monto, String codigoTransaccion){
        
        this.monto = monto;
        this.codigoTransaccion = codigoTransaccion;
    }

    public String getCodigoTransaccion(){
        return this.codigoTransaccion;
    }
    
    public void procesoDePago(){
        if (this.validarDatos()){
            this.reservarFondos();
            this.ejecutarTransaccion();
            this.notificarResultado();
        }else{
            throw new ExcepcionDominio("los datos no fueron validados correctamente");
        }     
    }

    public abstract boolean validarDatos();
    public abstract void reservarFondos();   
    public abstract void ejecutarTransaccion();
    public abstract void notificarResultado();

}
