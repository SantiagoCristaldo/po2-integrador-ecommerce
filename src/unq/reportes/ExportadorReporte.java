package unq.reportes;


public interface ExportadorReporte {
    String visitar(ReporteServiciosMasReservados reporte);

    String visitar(ReporteOcupacion reporte);

    String visitar(ReporteExposicionProveedor reporte);
}
