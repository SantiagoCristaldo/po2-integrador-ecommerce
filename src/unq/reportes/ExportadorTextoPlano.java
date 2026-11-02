package unq.reportes;

import java.util.ArrayList;
import java.util.List;

public class ExportadorTextoPlano implements ExportadorReporte {

    @Override
    public String visitar(ReporteServiciosMasReservados reporte) {
        List<String> lineas = new ArrayList<>();
        for (FilaVentas fila : reporte.getFilas()) {
            lineas.add(lineaDeVentas(fila));
        }
        return Formato.conFilas(
                Formato.encabezadoConPeriodo("SERVICIOS MAS RESERVADOS", reporte.getPeriodo()),
                "", lineas);
    }

    @Override
    public String visitar(ReporteOcupacion reporte) {
        List<String> lineas = new ArrayList<>();
        for (FilaOcupacion fila : reporte.getFilas()) {
            lineas.add(lineaDeOcupacion(fila));
        }
        return Formato.conFilas(
                Formato.encabezadoConPeriodo("OCUPACION POR CATEGORIA", reporte.getPeriodo()),
                "", lineas);
    }

    @Override
    public String visitar(ReporteExposicionProveedor reporte) {
        List<String> lineas = new ArrayList<>();
        for (FilaExposicion fila : reporte.getFilas()) {
            lineas.add("- " + fila.getOperador()
                    + ": " + fila.getCantidadItems() + " items"
                    + " (" + Formato.numero(fila.getPorcentajeCatalogo()) + "% del catalogo)");
        }
        return Formato.conFilas("EXPOSICION POR PROVEEDOR\n", "", lineas);
    }

    private String lineaDeVentas(FilaVentas fila) {
        return "- " + fila.getItem().nombre()
                + ": " + fila.getUnidadesVendidas() + " vendidos"
                + ", precio promedio $" + Formato.numero(fila.getPrecioPromedioCobrado());
    }

    private String lineaDeOcupacion(FilaOcupacion fila) {
        return "- " + fila.getCategoria().getNombre()
                + ": " + fila.getCuposReservados() + "/" + fila.getCuposTotales()
                + " cupos (" + fila.getCuposLibres() + " libres)";
    }
}
