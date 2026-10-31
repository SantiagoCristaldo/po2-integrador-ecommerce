package unq.metodosDePago;

public interface APIBancaria {
    
    public boolean validarCBU(int cbu);

    public boolean validarAlias(String alias);

    public boolean transferirDeInmediato(int CBU, double monto);
    
}