package unq.reportes;

import java.util.ArrayList;
import java.util.List;

public class ExportadorHTML implements ExportadorReporte {

    @Override
    public String visitar(ReporteServiciosMasReservados reporte) {
        List<String> filas = new ArrayList<>();
        for (FilaVentas fila : reporte.getFilas()) {
            filas.add(fila(
                    fila.getItem().nombre(),
                    String.valueOf(fila.getUnidadesVendidas()),
                    Formato.numero(fila.getPrecioPromedioCobrado())));
        }
        return Formato.conFilas(encabezadoDeVentas(), "</table>\n", filas);
    }

    @Override
    public String visitar(ReporteOcupacion reporte) {
        List<String> filas = new ArrayList<>();
        for (FilaOcupacion fila : reporte.getFilas()) {
            filas.add(fila(
                    fila.getCategoria().getNombre(),
                    String.valueOf(fila.getCuposTotales()),
                    String.valueOf(fila.getCuposReservados()),
                    String.valueOf(fila.getCuposLibres())));
        }
        return Formato.conFilas(encabezadoDeOcupacion(), "</table>\n", filas);
    }

    @Override
    public String visitar(ReporteExposicionProveedor reporte) {
        List<String> filas = new ArrayList<>();
        for (FilaExposicion fila : reporte.getFilas()) {
            filas.add(fila(
                    fila.getOperador(),
                    String.valueOf(fila.getCantidadItems()),
                    Formato.numero(fila.getPorcentajeCatalogo())));
        }
        return Formato.conFilas(encabezadoDeExposicion(), "</table>\n", filas);
    }

    private String encabezadoDeVentas() {
        return "<h1>Servicios mas reservados</h1>\n<table>\n"
                + filaEncabezado("Item", "Unidades vendidas", "Precio promedio") + "\n";
    }

    private String encabezadoDeOcupacion() {
        return "<h1>Ocupacion por categoria</h1>\n<table>\n"
                + filaEncabezado("Categoria", "Cupos totales", "Cupos reservados", "Cupos libres") + "\n";
    }

    private String encabezadoDeExposicion() {
        return "<h1>Exposicion por proveedor</h1>\n<table>\n"
                + filaEncabezado("Operador", "Cantidad de items", "% del catalogo") + "\n";
    }

    private String filaEncabezado(String... columnas) {
        StringBuilder html = new StringBuilder("<tr>");
        for (String columna : columnas) {
            html.append("<th>").append(escapar(columna)).append("</th>");
        }
        return html.append("</tr>").toString();
    }

    private String fila(String... celdas) {
        StringBuilder html = new StringBuilder("<tr>");
        for (String celda : celdas) {
            html.append("<td>").append(escapar(celda)).append("</td>");
        }
        return html.append("</tr>").toString();
    }

    private String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
