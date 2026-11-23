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
import unq.catalogo.ItemCatalogo;
import unq.catalogo.Categoria;
import unq.catalogo.PaqueteTuristico;
import unq.catalogo.ServicioIndividual;
import unq.reserva.Reserva;

class ReporteServiciosMasReservadosTest {

    private Periodo periodo;
    private ReporteServiciosMasReservados reporte;
    private ItemCatalogo hotel;
    private ItemCatalogo tour;

    @BeforeEach
    void setUp() {
        periodo = new Periodo(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        reporte = new ReporteServiciosMasReservados(periodo);
        hotel = mock(ItemCatalogo.class);
        tour = mock(ItemCatalogo.class);
        when(hotel.precioFinal()).thenReturn(1000.0);
        when(tour.precioFinal()).thenReturn(400.0);
    }

    private Reserva reserva(boolean cuentaComoVenta, LocalDate fecha, ItemCatalogo... items) {
        Reserva reserva = mock(Reserva.class);
        when(reserva.cuentaComoVenta()).thenReturn(cuentaComoVenta);
        when(reserva.getFecha()).thenReturn(fecha);
        when(reserva.getItems()).thenReturn(List.of(items));
        return reserva;
    }

    private FilaVentas filaDe(ItemCatalogo item) {
        return reporte.getFilas().stream().filter(f -> f.getItem() == item).findFirst().orElseThrow();
    }

    @Test
    void generar_sinReservas_listaTodoElCatalogoConCeroVentas() {
        reporte.generar(List.of(), new Catalogo(hotel, tour));

        assertEquals(2, reporte.getFilas().size());
        assertEquals(0, filaDe(hotel).getUnidadesVendidas());
        assertEquals(0, filaDe(tour).getUnidadesVendidas());
    }

    @Test
    void generar_cuentaLasUnidadesVendidasPorItem() {
        LocalDate enero = LocalDate.of(2026, 1, 10);

        reporte.generar(List.of(reserva(true, enero, hotel), reserva(true, enero, hotel, tour)),
                new Catalogo(hotel, tour));

        assertEquals(2, filaDe(hotel).getUnidadesVendidas());
        assertEquals(1, filaDe(tour).getUnidadesVendidas());
    }

    @Test
    void generar_ordenaDeMayorAMenorPorUnidadesVendidas() {
        LocalDate enero = LocalDate.of(2026, 1, 10);

        reporte.generar(List.of(reserva(true, enero, tour), reserva(true, enero, tour)),
                new Catalogo(hotel, tour));

        assertSame(tour, reporte.getFilas().get(0).getItem());
        assertSame(hotel, reporte.getFilas().get(1).getItem());
    }

    @Test
    void generar_ignoraLasReservasQueNoCuentanComoVenta() {
        reporte.generar(List.of(reserva(false, LocalDate.of(2026, 1, 10), hotel)), new Catalogo(hotel));

        assertEquals(0, filaDe(hotel).getUnidadesVendidas());
    }

    @Test
    void generar_ignoraLasReservasFueraDelPeriodo() {
        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 2, 1), hotel)), new Catalogo(hotel));

        assertEquals(0, filaDe(hotel).getUnidadesVendidas());
    }

    @Test
    void generar_registraElPrecioFinalCobrado() {
        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), hotel)), new Catalogo(hotel));

        assertEquals(1000.0, filaDe(hotel).getPrecioPromedioCobrado(), 0.001);
    }

    @Test
    void generar_conPaquete_registraLaVentaDeSusComponentesConElDescuentoDelPaquete() {
        ItemCatalogo paquete = mock(ItemCatalogo.class);
        when(paquete.precioFinal()).thenReturn(1260.0);
        when(paquete.factorDeDescuento()).thenReturn(0.9);
        when(paquete.componentes()).thenReturn(List.of(hotel, tour));

        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), paquete)),
                new Catalogo(paquete, hotel, tour));

        assertEquals(1, filaDe(paquete).getUnidadesVendidas());
        assertEquals(1260.0, filaDe(paquete).getPrecioPromedioCobrado(), 0.001);
        assertEquals(1, filaDe(hotel).getUnidadesVendidas());
        assertEquals(900.0, filaDe(hotel).getPrecioPromedioCobrado(), 0.001);
        assertEquals(360.0, filaDe(tour).getPrecioPromedioCobrado(), 0.001);
    }

    // --- Enunciado: "precio promedio efectivamente cobrado, que puede diferir del precio base si se
    // aplicaron descuentos de paquete" (con el Pack Escapada Urbana de la tabla 2.1) ---

    private ServicioIndividual servicio(String nombre, String categoria, double precio) {
        return new ServicioIndividual(nombre, nombre, "d", "op", new Categoria(categoria), precio, 100);
    }

    @Test
    void generar_conElPackEscapadaUrbana_cobraLosServiciosConEl10PorCientoDeDescuento() {
        ServicioIndividual noche = servicio("Noche de Hotel Central", "Alojamiento", 15000.0);
        ServicioIndividual museo = servicio("Excursion Guiada Museo", "Excursion", 4000.0);
        ServicioIndividual traslado = servicio("Traslado Aeropuerto-Hotel", "Traslado", 2000.0);
        PaqueteTuristico pack = new PaqueteTuristico("Pack Escapada Urbana", "d", 10, 100);
        pack.agregarItem(noche);
        pack.agregarItem(museo);
        pack.agregarItem(traslado);

        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), pack)),
                new Catalogo(pack, noche, museo, traslado));

        assertEquals(18900.0, filaDe(pack).getPrecioPromedioCobrado(), 0.001);
        assertEquals(13500.0, filaDe(noche).getPrecioPromedioCobrado(), 0.001);
        assertEquals(3600.0, filaDe(museo).getPrecioPromedioCobrado(), 0.001);
        assertEquals(1800.0, filaDe(traslado).getPrecioPromedioCobrado(), 0.001);
    }

    @Test
    void generar_conPaquetesAnidados_acumulaLosDescuentosDeTodaLaJerarquia() {
        ServicioIndividual noche = servicio("Noche de Hotel Central", "Alojamiento", 15000.0);
        ServicioIndividual gastronomico = servicio("Tour Gastronomico", "Excursion", 5000.0);
        PaqueteTuristico pack = new PaqueteTuristico("Pack Escapada Urbana", "d", 10, 100);
        pack.agregarItem(noche);
        PaqueteTuristico kit = new PaqueteTuristico("Kit Gran Tour", "d", 20, 100);
        kit.agregarItem(pack);
        kit.agregarItem(gastronomico);

        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), kit)), new Catalogo(kit));

        assertEquals(10800.0, filaDe(noche).getPrecioPromedioCobrado(), 0.001);
        assertEquals(4000.0, filaDe(gastronomico).getPrecioPromedioCobrado(), 0.001);
    }

    @Test
    void generar_servicioVendidoSueltoYDentroDeUnPaquete_promediaAmbosPrecios() {
        ServicioIndividual noche = servicio("Noche de Hotel Central", "Alojamiento", 15000.0);
        PaqueteTuristico pack = new PaqueteTuristico("Pack", "d", 10, 100);
        pack.agregarItem(noche);
        LocalDate enero = LocalDate.of(2026, 1, 10);

        reporte.generar(List.of(reserva(true, enero, noche), reserva(true, enero, pack)), new Catalogo(noche, pack));

        assertEquals(2, filaDe(noche).getUnidadesVendidas());
        assertEquals(14250.0, filaDe(noche).getPrecioPromedioCobrado(), 0.001);
    }

    @Test
    void generar_dosVeces_noAcumulaLasFilasAnteriores() {
        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), hotel)), new Catalogo(hotel));
        reporte.generar(List.of(), new Catalogo(hotel));

        assertEquals(0, filaDe(hotel).getUnidadesVendidas());
    }

    @Test
    void generar_conCatalogoVacio_soloListaLosItemsVendidos() {
        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), hotel)), new Catalogo());

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
