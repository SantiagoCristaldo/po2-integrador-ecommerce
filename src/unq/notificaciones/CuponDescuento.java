package unq.notificaciones;

public class CuponDescuento {
    private String codigo;
    private float porcentaje;
    

    public CuponDescuento(String codigo, float porcentaje) {
        this.codigo = codigo;
        this.porcentaje = porcentaje;
        
    }

    public String getCodigo() {
        return codigo;
    }

    public float getPorcentaje() {
        return porcentaje;
    }
}
