package unq.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unq.reportes.ReportesDeEjemplo.exposicion;
import static unq.reportes.ReportesDeEjemplo.filaOcupacion;
import static unq.reportes.ReportesDeEjemplo.filaVentas;
import static unq.reportes.ReportesDeEjemplo.ocupacion;
import static unq.reportes.ReportesDeEjemplo.ventas;

import org.junit.jupiter.api.Test;

class ExportadorTextoPlanoTest {

    private final ExportadorTextoPlano exportador = new ExportadorTextoPlano();

    @Test
    void visitarVentas_incluyeElPeriodoYUnaLineaPorItem() {
        String texto = exportador.visitar(ventas(
                filaVentas("Hotel", 1000.0, 2000.0),
                filaVentas("Tour", 400.0)));

        assertEquals("SERVICIOS MAS RESERVADOS (2026-01-01 a 2026-01-31)\n"
                + "- Hotel: 2 vendidos, precio promedio $1500.00\n"
                + "- Tour: 1 vendidos, precio promedio $400.00\n", texto);
    }

    @Test
    void visitarOcupacion_muestraReservadosSobreTotalesYLibres() {
        String texto = exportador.visitar(ocupacion(filaOcupacion("Alojamiento", 20, 5)));

        assertEquals("OCUPACION POR CATEGORIA (2026-01-01 a 2026-01-31)\n"
                + "- Alojamiento: 5/20 cupos (15 libres)\n", texto);
    }

    @Test
    void visitarExposicion_muestraCantidadYPorcentajeDelCatalogo() {
        String texto = exportador.visitar(exposicion(new FilaExposicon("Andes SA", 3, 75.0)));

        assertEquals("EXPOSICION POR PROVEEDOR\n"
                + "- Andes SA: 3 items (75.00% del catalogo)\n", texto);
    }

    @Test
    void visitarExposicion_sinFilas_soloGeneraElTitulo() {
        assertEquals("EXPOSICION POR PROVEEDOR\n", exportador.visitar(exposicion()));
    }
}
