package unq.reportes;

import java.util.List;
import java.util.function.Function;

public class ExportadorHTML implements ExportadorReporte {

    @Override
    public String visitar(ReporteServiciosMasReservados reporte) {
        return tabla("Servicios mas reservados",
                List.of("Item", "Unidades vendidas", "Precio promedio"),
                reporte.getFilas(),
                fila -> List.of(
                        fila.getItem().nombre(),
                        String.valueOf(fila.getUnidadesVendidas()),
                        Formato.numero(fila.getPrecioPromedioCobrado())));
    }

    @Override
    public String visitar(ReporteOcupacion reporte) {
        return tabla("Ocupacion por categoria",
                List.of("Categoria", "Cupos totales", "Cupos reservados", "Cupos libres"),
                reporte.getFilas(),
                fila -> List.of(
                        fila.getCategoria().getNombre(),
                        String.valueOf(fila.getCuposTotales()),
                        String.valueOf(fila.getCuposReservados()),
                        String.valueOf(fila.getCuposLibres())));
    }

    @Override
    public String visitar(ReporteExposicionProveedor reporte) {
        return tabla("Exposicion por proveedor",
                List.of("Operador", "Cantidad de items", "% del catalogo"),
                reporte.getFilas(),
                fila -> List.of(
                        fila.getOperador(),
                        String.valueOf(fila.getCantidadItems()),
                        Formato.numero(fila.getPorcentajeCatalogo())));
    }

    private <F> String tabla(String titulo, List<String> columnas, List<F> filas, Function<F, List<String>> celdas) {
        String encabezado = "<h1>" + titulo + "</h1>\n<table>\n" + fila("th", columnas) + "\n";
        return Formato.conFilas(encabezado, filas, f -> fila("td", celdas.apply(f))) + "</table>\n";
    }

    private String fila(String etiqueta, List<String> celdas) {
        StringBuilder html = new StringBuilder("<tr>");
        for (String celda : celdas) {
            html.append("<").append(etiqueta).append(">")
                    .append(escapar(celda))
                    .append("</").append(etiqueta).append(">");
        }
        return html.append("</tr>").toString();
    }

    private String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
