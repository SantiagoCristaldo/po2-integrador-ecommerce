package unq.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;

class ReporteExposicionProveedorTest {

    private ReporteExposicionProveedor reporte;
    private ServicioIndividual hotel;
    private ServicioIndividual hostel;
    private ServicioIndividual tour;

    @BeforeEach
    void setUp() {
        reporte = new ReporteExposicionProveedor();
        Categoria categoria = new Categoria("Alojamiento");
        hotel = new ServicioIndividual("H1", "Hotel", "d", "Andes SA", categoria, 1000.0, 10);
        hostel = new ServicioIndividual("H2", "Hostel", "d", "Andes SA", categoria, 500.0, 5);
        tour = new ServicioIndividual("T1", "Tour", "d", "Sur Viajes", categoria, 300.0, 20);
    }

    private FilaExposicion filaDe(String operador) {
        return reporte.getFilas().stream()
                .filter(f -> f.getOperador().equals(operador)).findFirst().orElseThrow();
    }

    @Test
    void generar_cuentaLosItemsDeCadaOperador() {
        reporte.generar(List.of(), List.of(hotel, hostel, tour));

        assertEquals(2, filaDe("Andes SA").getCantidadItems());
        assertEquals(1, filaDe("Sur Viajes").getCantidadItems());
    }

    @Test
    void generar_calculaElPorcentajeSobreElTotalDelCatalogo() {
        reporte.generar(List.of(), List.of(hotel, hostel, tour, hotel));

        assertEquals(75.0, filaDe("Andes SA").getPorcentajeCatalogo(), 0.001);
        assertEquals(25.0, filaDe("Sur Viajes").getPorcentajeCatalogo(), 0.001);
    }

    @Test
    void generar_unPaqueteCuentaUnaSolaVezPorOperadorAunqueTengaVariosServiciosSuyos() {
        Catalogo paquete = mock(Catalogo.class);
        when(paquete.servicios()).thenReturn(List.of(hotel, hostel, tour));

        reporte.generar(List.of(), List.of(paquete));

        assertEquals(1, filaDe("Andes SA").getCantidadItems());
        assertEquals(1, filaDe("Sur Viajes").getCantidadItems());
        assertEquals(100.0, filaDe("Andes SA").getPorcentajeCatalogo(), 0.001);
    }

    @Test
    void generar_ordenaDeMayorAMenorPorCantidadDeItems() {
        reporte.generar(List.of(), List.of(tour, hotel, hostel));

        assertEquals("Andes SA", reporte.getFilas().get(0).getOperador());
        assertEquals("Sur Viajes", reporte.getFilas().get(1).getOperador());
    }

    @Test
    void generar_conCatalogoVacio_noTieneFilas() {
        reporte.generar(List.of(), List.of());

        assertTrue(reporte.getFilas().isEmpty());
    }

    @Test
    void generar_dosVeces_noAcumulaLasFilasAnteriores() {
        reporte.generar(List.of(), List.of(hotel));
        reporte.generar(List.of(), List.of(hotel));

        assertEquals(1, reporte.getFilas().size());
        assertEquals(1, filaDe("Andes SA").getCantidadItems());
    }

    @Test
    void aceptar_delegaEnElExportadorYDevuelveSuResultado() {
        ExportadorReporte exportador = mock(ExportadorReporte.class);
        when(exportador.visitar(reporte)).thenReturn("exportado");

        assertEquals("exportado", reporte.aceptar(exportador));
        verify(exportador).visitar(reporte);
    }
}
