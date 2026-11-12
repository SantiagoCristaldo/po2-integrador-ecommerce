package unq.metodosDePago;

import java.time.LocalDateTime;

public interface APIBancaria {
    
    public boolean validarCBU(int cbu);

    public boolean validarAlias(String alias);

    public boolean transferirDeInmediato(int CBU, double monto);

    public boolean programarTransferencia(int CBU, double monto, LocalDateTime fecha);

}