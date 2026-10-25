package unq.reportes;

import java.util.Locale;

public class ExportadorHTML implements ExportadorReporte {

    @Override
    public String visitar(ReporteServiciosMasReservados reporte) {
        StringBuilder html = new StringBuilder("<h1>Servicios mas reservados</h1>\n<table>\n");
        html.append(filaEncabezado("Item", "Unidades vendidas", "Precio promedio"));
        for (FilaVentas fila : reporte.getFilas()) {
            html.append(fila(fila.getItem().nombre(),
                    String.valueOf(fila.getUnidadesVendidas()),
                    numero(fila.getPrecioPromedioCobrado())));
        }
        return html.append("</table>\n").toString();
    }

    @Override
    public String visitar(ReporteOcupacion reporte) {
        StringBuilder html = new StringBuilder("<h1>Ocupacion por categoria</h1>\n<table>\n");
        html.append(filaEncabezado("Categoria", "Cupos totales", "Cupos reservados", "Cupos libres"));
        for (FilaOcupacion fila : reporte.getFilas()) {
            html.append(fila(fila.getCategoria().getNombre(),
                    String.valueOf(fila.getCuposTotales()),
                    String.valueOf(fila.getCuposReservados()),
                    String.valueOf(fila.getCuposLibres())));
        }
        return html.append("</table>\n").toString();
    }

    @Override
    public String visitar(ReporteExposicionProveedor reporte) {
        StringBuilder html = new StringBuilder("<h1>Exposicion por proveedor</h1>\n<table>\n");
        html.append(filaEncabezado("Operador", "Cantidad de items", "% del catalogo"));
        for (FilaExposicon fila : reporte.getFilas()) {
            html.append(fila(fila.getOperador(),
                    String.valueOf(fila.getCantidadItems()),
                    numero(fila.getPorcentajeCatalogo())));
        }
        return html.append("</table>\n").toString();
    }

    private String filaEncabezado(String... columnas) {
        StringBuilder html = new StringBuilder("<tr>");
        for (String columna : columnas) {
            html.append("<th>").append(escapar(columna)).append("</th>");
        }
        return html.append("</tr>\n").toString();
    }

    private String fila(String... celdas) {
        StringBuilder html = new StringBuilder("<tr>");
        for (String celda : celdas) {
            html.append("<td>").append(escapar(celda)).append("</td>");
        }
        return html.append("</tr>\n").toString();
    }

    private String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String numero(double valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }
}
