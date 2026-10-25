package unq.reportes;

import java.util.Locale;

public class ExportadorTextoPlano implements ExportadorReporte {

    @Override
    public String visitar(ReporteServiciosMasReservados reporte) {
        StringBuilder texto = new StringBuilder("SERVICIOS MAS RESERVADOS ("
                + reporte.getPeriodo().getDesde() + " a " + reporte.getPeriodo().getHasta() + ")\n");
        for (FilaVentas fila : reporte.getFilas()) {
            texto.append("- ").append(fila.getItem().nombre())
                 .append(": ").append(fila.getUnidadesVendidas()).append(" vendidos")
                 .append(", precio promedio $").append(numero(fila.getPrecioPromedioCobrado()))
                 .append("\n");
        }
        return texto.toString();
    }

    @Override
    public String visitar(ReporteOcupacion reporte) {
        StringBuilder texto = new StringBuilder("OCUPACION POR CATEGORIA ("
                + reporte.getPeriodo().getDesde() + " a " + reporte.getPeriodo().getHasta() + ")\n");
        for (FilaOcupacion fila : reporte.getFilas()) {
            texto.append("- ").append(fila.getCategoria().getNombre())
                 .append(": ").append(fila.getCuposReservados()).append("/").append(fila.getCuposTotales())
                 .append(" cupos (").append(fila.getCuposLibres()).append(" libres)")
                 .append("\n");
        }
        return texto.toString();
    }

    @Override
    public String visitar(ReporteExposicionProveedor reporte) {
        StringBuilder texto = new StringBuilder("EXPOSICION POR PROVEEDOR\n");
        for (FilaExposicon fila : reporte.getFilas()) {
            texto.append("- ").append(fila.getOperador())
                 .append(": ").append(fila.getCantidadItems()).append(" items")
                 .append(" (").append(numero(fila.getPorcentajeCatalogo())).append("% del catalogo)")
                 .append("\n");
        }
        return texto.toString();
    }

    private String numero(double valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }
}
