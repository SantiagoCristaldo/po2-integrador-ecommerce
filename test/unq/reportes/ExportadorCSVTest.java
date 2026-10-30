package unq.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unq.reportes.ReportesDeEjemplo.exposicion;
import static unq.reportes.ReportesDeEjemplo.filaOcupacion;
import static unq.reportes.ReportesDeEjemplo.filaVentas;
import static unq.reportes.ReportesDeEjemplo.ocupacion;
import static unq.reportes.ReportesDeEjemplo.ventas;

import org.junit.jupiter.api.Test;

class ExportadorCSVTest {

    private final ExportadorCSV exportador = new ExportadorCSV();

    @Test
    void visitarVentas_generaEncabezadoYUnaLineaPorFila() {
        String csv = exportador.visitar(ventas(
                filaVentas("Hotel", 1000.0, 2000.0),
                filaVentas("Tour", 400.0)));

        assertEquals("item,unidadesVendidas,precioPromedioCobrado\n"
                + "Hotel,2,1500.00\n"
                + "Tour,1,400.00\n", csv);
    }

    @Test
    void visitarVentas_sinFilas_soloGeneraElEncabezado() {
        assertEquals("item,unidadesVendidas,precioPromedioCobrado\n", exportador.visitar(ventas()));
    }

    @Test
    void visitarVentas_conComaEnElNombre_loEncierraEntreComillas() {
        String csv = exportador.visitar(ventas(filaVentas("Hotel, Centro", 1000.0)));

        assertEquals("item,unidadesVendidas,precioPromedioCobrado\n"
                + "\"Hotel, Centro\",1,1000.00\n", csv);
    }

    @Test
    void visitarVentas_conComillasEnElNombre_lasDuplica() {
        String csv = exportador.visitar(ventas(filaVentas("Hotel \"Sol\"", 1000.0)));

        assertEquals("item,unidadesVendidas,precioPromedioCobrado\n"
                + "\"Hotel \"\"Sol\"\"\",1,1000.00\n", csv);
    }

    @Test
    void visitarOcupacion_generaEncabezadoYCupos() {
        String csv = exportador.visitar(ocupacion(filaOcupacion("Alojamiento", 20, 5)));

        assertEquals("categoria,cuposTotales,cuposReservados,cuposLibres\n"
                + "Alojamiento,20,5,15\n", csv);
    }

    @Test
    void visitarExposicion_generaEncabezadoYPorcentajeConDosDecimales() {
        String csv = exportador.visitar(exposicion(new FilaExposicon("Andes SA", 3, 75.0)));

        assertEquals("operador,cantidadItems,porcentajeCatalogo\n"
                + "Andes SA,3,75.00\n", csv);
    }

    @Test
    void aceptar_desdeElReporte_usaElFormatoCSV() {
        ReporteExposicionProveedor reporte = new ReporteExposicionProveedor();

        assertEquals("operador,cantidadItems,porcentajeCatalogo\n", reporte.aceptar(exportador));
    }
}
