package unq.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unq.reportes.ReportesDeEjemplo.exposicion;
import static unq.reportes.ReportesDeEjemplo.filaOcupacion;
import static unq.reportes.ReportesDeEjemplo.filaVentas;
import static unq.reportes.ReportesDeEjemplo.ocupacion;
import static unq.reportes.ReportesDeEjemplo.ventas;

import org.junit.jupiter.api.Test;

class ExportadorHTMLTest {

    private final ExportadorHTML exportador = new ExportadorHTML();

    @Test
    void visitarVentas_generaTituloTablaYFilas() {
        String html = exportador.visitar(ventas(filaVentas("Hotel", 1000.0, 2000.0)));

        assertEquals("<h1>Servicios mas reservados</h1>\n<table>\n"
                + "<tr><th>Item</th><th>Unidades vendidas</th><th>Precio promedio</th></tr>\n"
                + "<tr><td>Hotel</td><td>2</td><td>1500.00</td></tr>\n"
                + "</table>\n", html);
    }

    @Test
    void visitarVentas_escapaLosCaracteresEspecialesDeHTML() {
        String html = exportador.visitar(ventas(filaVentas("Hotel & <Spa>", 1000.0)));

        assertTrue(html.contains("<td>Hotel &amp; &lt;Spa&gt;</td>"));
    }

    @Test
    void visitarOcupacion_generaTituloTablaYFilas() {
        String html = exportador.visitar(ocupacion(filaOcupacion("Alojamiento", 20, 5)));

        assertEquals("<h1>Ocupacion por categoria</h1>\n<table>\n"
                + "<tr><th>Categoria</th><th>Cupos totales</th><th>Cupos reservados</th><th>Cupos libres</th></tr>\n"
                + "<tr><td>Alojamiento</td><td>20</td><td>5</td><td>15</td></tr>\n"
                + "</table>\n", html);
    }

    @Test
    void visitarExposicion_generaTituloTablaYFilas() {
        String html = exportador.visitar(exposicion(new FilaExposicon("Andes SA", 3, 75.0)));

        assertEquals("<h1>Exposicion por proveedor</h1>\n<table>\n"
                + "<tr><th>Operador</th><th>Cantidad de items</th><th>% del catalogo</th></tr>\n"
                + "<tr><td>Andes SA</td><td>3</td><td>75.00</td></tr>\n"
                + "</table>\n", html);
    }

    @Test
    void visitarExposicion_sinFilas_soloGeneraElEncabezado() {
        assertEquals("<h1>Exposicion por proveedor</h1>\n<table>\n"
                + "<tr><th>Operador</th><th>Cantidad de items</th><th>% del catalogo</th></tr>\n"
                + "</table>\n", exportador.visitar(exposicion()));
    }
}
