package unq.metodosDePago;

import java.time.LocalDateTime;

public interface APITarjetaDeCredito {
    
    public boolean validarDatos(TarjDeCredito tarjeta); // contiene validarCVV(int numero, int cvv), validarVencimiento(LocalDateTime fecha)

    public boolean validarCVV(int numero, int cvv);

    public boolean validarVencimiento(LocalDateTime fecha);

    public boolean preAutorizar(int numero, double monto);

    public void transferirInmediata(double monto);    

}
