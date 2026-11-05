package unq.reportes;

public class ExportadorCSV implements ExportadorReporte {

    @Override
    public String visitar(ReporteServiciosMasReservados reporte) {
        return Formato.conFilas("item,unidadesVendidas,precioPromedioCobrado\n", reporte.getFilas(),
                fila -> campo(fila.getItem().nombre()) + ","
                        + fila.getUnidadesVendidas() + ","
                        + Formato.numero(fila.getPrecioPromedioCobrado()));
    }

    @Override
    public String visitar(ReporteOcupacion reporte) {
        return Formato.conFilas("categoria,cuposTotales,cuposReservados,cuposLibres\n", reporte.getFilas(),
                fila -> campo(fila.getCategoria().getNombre()) + ","
                        + fila.getCuposTotales() + ","
                        + fila.getCuposReservados() + ","
                        + fila.getCuposLibres());
    }

    @Override
    public String visitar(ReporteExposicionProveedor reporte) {
        return Formato.conFilas("operador,cantidadItems,porcentajeCatalogo\n", reporte.getFilas(),
                fila -> campo(fila.getOperador()) + ","
                        + fila.getCantidadItems() + ","
                        + Formato.numero(fila.getPorcentajeCatalogo()));
    }

    // Si el texto tiene comas o comillas, va entre comillas (regla de CSV)
    private String campo(String valor) {
        if (valor.contains(",") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
