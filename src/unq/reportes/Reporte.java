package unq.reportes;

import java.util.List;
import unq.catalogo.Catalogo;
import unq.reserva.Reserva;

public interface Reporte {
    void generar(List<Reserva> reservas, List<Catalogo> catalogo);
    String aceptar(ExportadorReporte exportador);
   
}
