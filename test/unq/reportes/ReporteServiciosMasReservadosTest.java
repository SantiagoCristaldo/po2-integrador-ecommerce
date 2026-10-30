package unq.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;
import unq.reserva.Reserva;

class ReporteServiciosMasReservadosTest {

    private Periodo periodo;
    private ReporteServiciosMasReservados reporte;
    private Catalogo hotel;
    private Catalogo tour;

    @BeforeEach
    void setUp() {
        periodo = new Periodo(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        reporte = new ReporteServiciosMasReservados(periodo);
        hotel = mock(Catalogo.class);
        tour = mock(Catalogo.class);
        when(hotel.precioFinal()).thenReturn(1000.0);
        when(tour.precioFinal()).thenReturn(400.0);
    }

    private Reserva reserva(boolean cuentaComoVenta, LocalDate fecha, Catalogo... items) {
        Reserva reserva = mock(Reserva.class);
        when(reserva.cuentaComoVenta()).thenReturn(cuentaComoVenta);
        when(reserva.getFecha()).thenReturn(fecha);
        when(reserva.getItems()).thenReturn(List.of(items));
        return reserva;
    }

    private FilaVentas filaDe(Catalogo item) {
        return reporte.getFilas().stream().filter(f -> f.getItem() == item).findFirst().orElseThrow();
    }

    @Test
    void generar_sinReservas_listaTodoElCatalogoConCeroVentas() {
        reporte.generar(List.of(), List.of(hotel, tour));

        assertEquals(2, reporte.getFilas().size());
        assertEquals(0, filaDe(hotel).getUnidadesVendidas());
        assertEquals(0, filaDe(tour).getUnidadesVendidas());
    }

    @Test
    void generar_cuentaLasUnidadesVendidasPorItem() {
        LocalDate enero = LocalDate.of(2026, 1, 10);

        reporte.generar(List.of(reserva(true, enero, hotel), reserva(true, enero, hotel, tour)),
                List.of(hotel, tour));

        assertEquals(2, filaDe(hotel).getUnidadesVendidas());
        assertEquals(1, filaDe(tour).getUnidadesVendidas());
    }

    @Test
    void generar_ordenaDeMayorAMenorPorUnidadesVendidas() {
        LocalDate enero = LocalDate.of(2026, 1, 10);

        reporte.generar(List.of(reserva(true, enero, tour), reserva(true, enero, tour)),
                List.of(hotel, tour));

        assertSame(tour, reporte.getFilas().get(0).getItem());
        assertSame(hotel, reporte.getFilas().get(1).getItem());
    }

    @Test
    void generar_ignoraLasReservasQueNoCuentanComoVenta() {
        reporte.generar(List.of(reserva(false, LocalDate.of(2026, 1, 10), hotel)), List.of(hotel));

        assertEquals(0, filaDe(hotel).getUnidadesVendidas());
    }

    @Test
    void generar_ignoraLasReservasFueraDelPeriodo() {
        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 2, 1), hotel)), List.of(hotel));

        assertEquals(0, filaDe(hotel).getUnidadesVendidas());
    }

    @Test
    void generar_registraElPrecioFinalCobrado() {
        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), hotel)), List.of(hotel));

        assertEquals(1000.0, filaDe(hotel).getPrecioPromedioCobrado(), 0.001);
    }

    @Test
    void generar_conPaquete_registraLaVentaDeSusComponentesConElDescuentoDelPaquete() {
        Catalogo paquete = mock(Catalogo.class);
        when(paquete.precioFinal()).thenReturn(1260.0);
        when(paquete.factorDeDescuento()).thenReturn(0.9);
        when(paquete.componentes()).thenReturn(List.of(hotel, tour));

        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), paquete)),
                List.of(paquete, hotel, tour));

        assertEquals(1, filaDe(paquete).getUnidadesVendidas());
        assertEquals(1260.0, filaDe(paquete).getPrecioPromedioCobrado(), 0.001);
        assertEquals(1, filaDe(hotel).getUnidadesVendidas());
        assertEquals(900.0, filaDe(hotel).getPrecioPromedioCobrado(), 0.001);
        assertEquals(360.0, filaDe(tour).getPrecioPromedioCobrado(), 0.001);
    }

    @Test
    void generar_dosVeces_noAcumulaLasFilasAnteriores() {
        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), hotel)), List.of(hotel));
        reporte.generar(List.of(), List.of(hotel));

        assertEquals(0, filaDe(hotel).getUnidadesVendidas());
    }

    @Test
    void generar_conCatalogoVacio_soloListaLosItemsVendidos() {
        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), hotel)), List.of());

        assertEquals(1, reporte.getFilas().size());
    }

    @Test
    void getFilas_antesDeGenerar_estaVacio() {
        assertTrue(reporte.getFilas().isEmpty());
    }

    @Test
    void getPeriodo_devuelveElPeriodoDelConstructor() {
        assertSame(periodo, reporte.getPeriodo());
    }

    @Test
    void aceptar_delegaEnElExportadorYDevuelveSuResultado() {
        ExportadorReporte exportador = mock(ExportadorReporte.class);
        when(exportador.visitar(reporte)).thenReturn("exportado");

        assertEquals("exportado", reporte.aceptar(exportador));
        verify(exportador).visitar(reporte);
    }
}
