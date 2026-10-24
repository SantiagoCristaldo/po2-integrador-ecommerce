package unq.notificaciones;

import java.time.LocalDate;

public class CuponDescuento {
    private String codigo;
    private float porcentaje;
    private LocalDate vencimiento;

    public CuponDescuento(String codigo, float porcentaje, LocalDate vencimiento) {
        this.codigo = codigo;
        this.porcentaje = porcentaje;
        this.vencimiento = vencimiento;
    }

    public String getCodigo() {
        return codigo;
    }

    public float getPorcentaje() {
        return porcentaje;
    }

    public LocalDate getVencimiento() {
        return vencimiento;
    }
}
