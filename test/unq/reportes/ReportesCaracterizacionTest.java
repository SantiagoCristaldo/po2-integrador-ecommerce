package unq.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.PaqueteTuristico;
import unq.catalogo.ServicioIndividual;
import unq.reserva.Reserva;
import unq.traslado.Direccion;

/**
 * Tests de caracterización del circuito completo: datos de ejemplo -> generar() -> exportar.
 *
 * <p>Fijan la salida exacta de los tres exportadores (texto plano, CSV y HTML) para que el
 * refactor de legibilidad pueda hacerse sin cambiar ni un carácter. Los colaboradores se doblan
 * con Mockito: {@link Reserva} y {@link Catalogo} son las entradas del reporte.</p>
 */
class ReportesCaracterizacionTest {

    private static final Periodo ENERO =
            new Periodo(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
    private static final LocalDate EN_ENERO = LocalDate.of(2026, 1, 10);

    private final ExportadorTextoPlano textoPlano = new ExportadorTextoPlano();
    private final ExportadorCSV csv = new ExportadorCSV();
    private final ExportadorHTML html = new ExportadorHTML();

    // --- Datos de ejemplo ---

    private static ServicioIndividual servicio(String nombre, String categoria, String operador,
            double precioFinal) {
        ServicioIndividual servicio = mock(ServicioIndividual.class);
        when(servicio.nombre()).thenReturn(nombre);
        when(servicio.getCategoria()).thenReturn(new Categoria(categoria));
        when(servicio.getOperador()).thenReturn(operador);
        when(servicio.precioFinal()).thenReturn(precioFinal);
        when(servicio.servicios()).thenReturn(List.of(servicio));
        when(servicio.componentes()).thenReturn(List.of());
        when(servicio.factorDeDescuento()).thenReturn(1.0);
        return servicio;
    }

    private static Catalogo paquete(String nombre, double precioFinal, double factorDeDescuento,
            ServicioIndividual... servicios) {
        Catalogo paquete = mock(Catalogo.class);
        when(paquete.nombre()).thenReturn(nombre);
        when(paquete.precioFinal()).thenReturn(precioFinal);
        when(paquete.factorDeDescuento()).thenReturn(factorDeDescuento);
        when(paquete.servicios()).thenReturn(List.of(servicios));
        when(paquete.componentes()).thenReturn(List.<Catalogo>of(servicios));
        return paquete;
    }

    private static Reserva reserva(boolean cuentaComoVenta, LocalDate fecha, int pasajeros,
            Catalogo... items) {
        Reserva reserva = mock(Reserva.class);
        when(reserva.cuentaComoVenta()).thenReturn(cuentaComoVenta);
        when(reserva.getFecha()).thenReturn(fecha);
        when(reserva.cantidadDePasajeros()).thenReturn(pasajeros);
        when(reserva.getItems()).thenReturn(List.of(items));
        return reserva;
    }

    private ReporteServiciosMasReservados reporteDeVentas() {
        ServicioIndividual hotel = servicio("Hotel Central", "Alojamiento", "Andes SA", 1000.0);
        ServicioIndividual tour = servicio("Tour, \"Guía\" <VIP>", "Excursion", "Sur & Viajes, SRL", 400.0);
        Catalogo pack = paquete("Pack & Spa", 1260.0, 0.9, hotel, tour);

        ReporteServiciosMasReservados reporte = new ReporteServiciosMasReservados(ENERO);
        reporte.generar(
                List.of(reserva(true, EN_ENERO, 2, pack),
                        reserva(true, LocalDate.of(2026, 2, 10), 3, hotel),
                        reserva(false, EN_ENERO, 1, tour)),
                List.of(hotel, tour, pack));
        return reporte;
    }

    // Enero tiene 31 días y la reserva ocupa 2 cupos durante 3 días de cada servicio.
    private ReporteOcupacion reporteDeOcupacion() {
        ServicioIndividual hotel = new ServicioIndividual("H1", "Hotel Central", "d", "Andes SA",
                new Categoria("Alojamiento"), 1000.0, 10);
        ServicioIndividual tour = new ServicioIndividual("T1", "Tour", "d", "Sur & Viajes",
                new Categoria("Excursion"), 400.0, 5);
        PaqueteTuristico pack = new PaqueteTuristico("Pack", "d", 10, 0);
        pack.agregarItem(hotel);
        pack.agregarItem(tour);
        Reserva reserva = new Reserva("cliente@mail.com", mock(Direccion.class), 2, EN_ENERO, EN_ENERO.plusDays(2));
        reserva.agregarItem(pack);
        reserva.confirmar();

        ReporteOcupacion reporte = new ReporteOcupacion(ENERO);
        reporte.generar(List.of(reserva), List.of(hotel, tour, pack));
        return reporte;
    }

    private ReporteExposicionProveedor reporteDeExposicion() {
        ServicioIndividual hotel = servicio("Hotel", "Alojamiento", "Andes SA", 1000.0);
        ServicioIndividual tour = servicio("Tour", "Excursion", "Sur & Viajes, SRL", 400.0);
        Catalogo pack = paquete("Pack", 1260.0, 0.9, hotel);

        ReporteExposicionProveedor reporte = new ReporteExposicionProveedor();
        reporte.generar(List.of(reserva(true, EN_ENERO, 2, tour)), List.of(hotel, tour, pack));
        return reporte;
    }

    // --- Servicios más reservados ---

    @Test
    void aceptar_enTextoPlano_fijaLaSalidaExactaDeVentas() {
        assertEquals("SERVICIOS MAS RESERVADOS (2026-01-01 a 2026-01-31)\n"
                + "- Hotel Central: 1 vendidos, precio promedio $900.00\n"
                + "- Tour, \"Guía\" <VIP>: 1 vendidos, precio promedio $360.00\n"
                + "- Pack & Spa: 1 vendidos, precio promedio $1260.00\n",
                reporteDeVentas().aceptar(textoPlano));
    }

    @Test
    void aceptar_enCSV_fijaLaSalidaExactaDeVentasConEscapes() {
        assertEquals("item,unidadesVendidas,precioPromedioCobrado\n"
                + "Hotel Central,1,900.00\n"
                + "\"Tour, \"\"Guía\"\" <VIP>\",1,360.00\n"
                + "Pack & Spa,1,1260.00\n",
                reporteDeVentas().aceptar(csv));
    }

    @Test
    void aceptar_enHTML_fijaLaSalidaExactaDeVentasConEscapes() {
        assertEquals("<h1>Servicios mas reservados</h1>\n"
                + "<table>\n"
                + "<tr><th>Item</th><th>Unidades vendidas</th><th>Precio promedio</th></tr>\n"
                + "<tr><td>Hotel Central</td><td>1</td><td>900.00</td></tr>\n"
                + "<tr><td>Tour, \"Guía\" &lt;VIP&gt;</td><td>1</td><td>360.00</td></tr>\n"
                + "<tr><td>Pack &amp; Spa</td><td>1</td><td>1260.00</td></tr>\n"
                + "</table>\n",
                reporteDeVentas().aceptar(html));
    }

    @Test
    void generar_conEmpateDeUnidades_mantieneElOrdenDelCatalogo() {
        ReporteServiciosMasReservados reporte = reporteDeVentas();

        assertEquals("Hotel Central", reporte.getFilas().get(0).getItem().nombre());
        assertEquals("Tour, \"Guía\" <VIP>", reporte.getFilas().get(1).getItem().nombre());
        assertEquals("Pack & Spa", reporte.getFilas().get(2).getItem().nombre());
    }

    // --- Ocupación ---

    @Test
    void aceptar_enTextoPlano_fijaLaSalidaExactaDeOcupacion() {
        assertEquals("OCUPACION POR CATEGORIA (2026-01-01 a 2026-01-31)\n"
                + "- Alojamiento: 6/310 cupos (304 libres)\n"
                + "- Excursion: 6/155 cupos (149 libres)\n",
                reporteDeOcupacion().aceptar(textoPlano));
    }

    @Test
    void aceptar_enCSV_fijaLaSalidaExactaDeOcupacion() {
        assertEquals("categoria,cuposTotales,cuposReservados,cuposLibres\n"
                + "Alojamiento,310,6,304\n"
                + "Excursion,155,6,149\n",
                reporteDeOcupacion().aceptar(csv));
    }

    @Test
    void aceptar_enHTML_fijaLaSalidaExactaDeOcupacion() {
        assertEquals("<h1>Ocupacion por categoria</h1>\n"
                + "<table>\n"
                + "<tr><th>Categoria</th><th>Cupos totales</th><th>Cupos reservados</th><th>Cupos libres</th></tr>\n"
                + "<tr><td>Alojamiento</td><td>310</td><td>6</td><td>304</td></tr>\n"
                + "<tr><td>Excursion</td><td>155</td><td>6</td><td>149</td></tr>\n"
                + "</table>\n",
                reporteDeOcupacion().aceptar(html));
    }

    // --- Exposición por proveedor ---

    @Test
    void aceptar_enTextoPlano_fijaLaSalidaExactaDeExposicion() {
        assertEquals("EXPOSICION POR PROVEEDOR\n"
                + "- Andes SA: 2 items (66.67% del catalogo)\n"
                + "- Sur & Viajes, SRL: 1 items (33.33% del catalogo)\n",
                reporteDeExposicion().aceptar(textoPlano));
    }

    @Test
    void aceptar_enCSV_fijaLaSalidaExactaDeExposicionConEscapes() {
        assertEquals("operador,cantidadItems,porcentajeCatalogo\n"
                + "Andes SA,2,66.67\n"
                + "\"Sur & Viajes, SRL\",1,33.33\n",
                reporteDeExposicion().aceptar(csv));
    }

    @Test
    void aceptar_enHTML_fijaLaSalidaExactaDeExposicionConEscapes() {
        assertEquals("<h1>Exposicion por proveedor</h1>\n"
                + "<table>\n"
                + "<tr><th>Operador</th><th>Cantidad de items</th><th>% del catalogo</th></tr>\n"
                + "<tr><td>Andes SA</td><td>2</td><td>66.67</td></tr>\n"
                + "<tr><td>Sur &amp; Viajes, SRL</td><td>1</td><td>33.33</td></tr>\n"
                + "</table>\n",
                reporteDeExposicion().aceptar(html));
    }
}
