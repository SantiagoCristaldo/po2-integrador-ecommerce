package unq.reportes;

import java.util.Locale;

public class ExportadorCSV implements ExportadorReporte {

    @Override
    public String visitar(ReporteServiciosMasReservados reporte) {
        StringBuilder csv = new StringBuilder("item,unidadesVendidas,precioPromedioCobrado\n");
        for (FilaVentas fila : reporte.getFilas()) {
            csv.append(campo(fila.getItem().nombre())).append(",")
               .append(fila.getUnidadesVendidas()).append(",")
               .append(numero(fila.getPrecioPromedioCobrado())).append("\n");
        }
        return csv.toString();
    }

    @Override
    public String visitar(ReporteOcupacion reporte) {
        StringBuilder csv = new StringBuilder("categoria,cuposTotales,cuposReservados,cuposLibres\n");
        for (FilaOcupacion fila : reporte.getFilas()) {
            csv.append(campo(fila.getCategoria().getNombre())).append(",")
               .append(fila.getCuposTotales()).append(",")
               .append(fila.getCuposReservados()).append(",")
               .append(fila.getCuposLibres()).append("\n");
        }
        return csv.toString();
    }

    @Override
    public String visitar(ReporteExposicionProveedor reporte) {
        StringBuilder csv = new StringBuilder("operador,cantidadItems,porcentajeCatalogo\n");
        for (FilaExposicon fila : reporte.getFilas()) {
            csv.append(campo(fila.getOperador())).append(",")
               .append(fila.getCantidadItems()).append(",")
               .append(numero(fila.getPorcentajeCatalogo())).append("\n");
        }
        return csv.toString();
    }

    // Si el texto tiene comas o comillas, va entre comillas (regla de CSV)
    private String campo(String valor) {
        if (valor.contains(",") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    private String numero(double valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }
}
