package unq.reportes;

public class ExportadorTextoPlano implements ExportadorReporte {

    @Override
    public String visitar(ReporteServiciosMasReservados reporte) {
        return Formato.conFilas(
                Formato.encabezadoConPeriodo("SERVICIOS MAS RESERVADOS", reporte.getPeriodo()), reporte.getFilas(),
                fila -> "- " + fila.getItem().nombre()
                        + ": " + fila.getUnidadesVendidas() + " vendidos"
                        + ", precio promedio $" + Formato.numero(fila.getPrecioPromedioCobrado()));
    }

    @Override
    public String visitar(ReporteOcupacion reporte) {
        return Formato.conFilas(
                Formato.encabezadoConPeriodo("OCUPACION POR CATEGORIA", reporte.getPeriodo()), reporte.getFilas(),
                fila -> "- " + fila.getCategoria().getNombre()
                        + ": " + fila.getCuposReservados() + "/" + fila.getCuposTotales()
                        + " cupos (" + fila.getCuposLibres() + " libres)");
    }

    @Override
    public String visitar(ReporteExposicionProveedor reporte) {
        return Formato.conFilas("EXPOSICION POR PROVEEDOR\n", reporte.getFilas(),
                fila -> "- " + fila.getOperador()
                        + ": " + fila.getCantidadItems() + " items"
                        + " (" + Formato.numero(fila.getPorcentajeCatalogo()) + "% del catalogo)");
    }
}
