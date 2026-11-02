package unq.reportes;

import java.util.ArrayList;
import java.util.List;

public class ExportadorCSV implements ExportadorReporte {

    @Override
    public String visitar(ReporteServiciosMasReservados reporte) {
        List<String> lineas = new ArrayList<>();
        for (FilaVentas fila : reporte.getFilas()) {
            lineas.add(campo(fila.getItem().nombre()) + ","
                    + fila.getUnidadesVendidas() + ","
                    + Formato.numero(fila.getPrecioPromedioCobrado()));
        }
        return Formato.conFilas("item,unidadesVendidas,precioPromedioCobrado\n", "", lineas);
    }

    @Override
    public String visitar(ReporteOcupacion reporte) {
        List<String> lineas = new ArrayList<>();
        for (FilaOcupacion fila : reporte.getFilas()) {
            lineas.add(campo(fila.getCategoria().getNombre()) + ","
                    + fila.getCuposTotales() + ","
                    + fila.getCuposReservados() + ","
                    + fila.getCuposLibres());
        }
        return Formato.conFilas("categoria,cuposTotales,cuposReservados,cuposLibres\n", "", lineas);
    }

    @Override
    public String visitar(ReporteExposicionProveedor reporte) {
        List<String> lineas = new ArrayList<>();
        for (FilaExposicion fila : reporte.getFilas()) {
            lineas.add(campo(fila.getOperador()) + ","
                    + fila.getCantidadItems() + ","
                    + Formato.numero(fila.getPorcentajeCatalogo()));
        }
        return Formato.conFilas("operador,cantidadItems,porcentajeCatalogo\n", "", lineas);
    }

    // Si el texto tiene comas o comillas, va entre comillas (regla de CSV)
    private String campo(String valor) {
        if (valor.contains(",") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
