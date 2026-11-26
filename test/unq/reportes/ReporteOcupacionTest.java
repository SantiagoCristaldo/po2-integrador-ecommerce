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
import unq.traslado.Direccion;

/** El período tiene 10 días, así que un servicio de 10 cupos tiene 100 cupos totales. */
class ReporteOcupacionTest {

    private static final LocalDate DESDE = LocalDate.of(2026, 1, 1);
    private static final LocalDate HASTA = LocalDate.of(2026, 1, 10);

    private Periodo periodo;
    private ReporteOcupacion reporte;
    private Categoria alojamiento;
    private Categoria excursion;
    private ServicioIndividual hotel;
    private ServicioIndividual hostel;
    private ServicioIndividual tour;

    @BeforeEach
    void setUp() {
        periodo = new Periodo(DESDE, HASTA);
        reporte = new ReporteOcupacion(periodo);
        alojamiento = new Categoria("Alojamiento");
        excursion = new Categoria("Excursion");
        hotel = new ServicioIndividual("H1", "Hotel", "d", "op", alojamiento, 1000.0, 10);
        hostel = new ServicioIndividual("H2", "Hostel", "d", "op", alojamiento, 500.0, 5);
        tour = new ServicioIndividual("T1", "Tour", "d", "op", excursion, 300.0, 20);
    }

    private FilaOcupacion filaDe(Categoria categoria) {
        return reporte.getFilas().stream()
                .filter(f -> f.getCategoria().equals(categoria)).findFirst().orElseThrow();
    }

    private Reserva reservaDe(int pasajeros, LocalDate inicio, LocalDate fin, ItemCatalogo... items) {
        Reserva reserva = new Reserva("cliente@mail.com", mock(Direccion.class), pasajeros, inicio, fin);
        for (ItemCatalogo item : items) {
            reserva.agregarItem(item);
        }
        return reserva;
    }

    @Test
    void generar_sumaLosCuposTotalesPorCategoriaYPorDia() {
        reporte.generar(List.of(), new Catalogo(hotel, hostel, tour));

        assertEquals(2, reporte.getFilas().size());
        assertEquals(150, filaDe(alojamiento).getCuposTotales());
        assertEquals(200, filaDe(excursion).getCuposTotales());
    }

    @Test
    void generar_noCuentaDosVecesUnServicioQueTambienEstaEnUnPaquete() {
        ItemCatalogo paquete = mock(ItemCatalogo.class);
        when(paquete.servicios()).thenReturn(List.of(hotel, tour));

        reporte.generar(List.of(), new Catalogo(hotel, tour, paquete));

        assertEquals(100, filaDe(alojamiento).getCuposTotales());
        assertEquals(200, filaDe(excursion).getCuposTotales());
    }

    @Test
    void generar_cuentaDosCategoriasConElMismoNombreComoUnaSola() {
        ServicioIndividual otro = new ServicioIndividual("H3", "Otro", "d", "op", new Categoria("Alojamiento"), 1.0, 1);

        reporte.generar(List.of(), new Catalogo(hotel, otro));

        assertEquals(1, reporte.getFilas().size());
        assertEquals(110, filaDe(alojamiento).getCuposTotales());
    }

    @Test
    void generar_unaReservaConfirmadaOcupaUnCupoPorPasajeroYPorDia() {
        reservaDe(2, DESDE, DESDE.plusDays(2), hotel).confirmar();

        reporte.generar(List.of(), new Catalogo(hotel, tour));

        assertEquals(6, filaDe(alojamiento).getCuposReservados());
        assertEquals(94, filaDe(alojamiento).getCuposLibres());
        assertEquals(100, filaDe(alojamiento).getCuposTotales());
        assertEquals(0, filaDe(excursion).getCuposReservados());
    }

    @Test
    void generar_conPaqueteReservado_ocupaCuposEnCadaCategoriaDeSusServicios() {
        PaqueteTuristico paquete = new PaqueteTuristico("Pack", "d", 0, 0);
        paquete.agregarItem(hotel);
        paquete.agregarItem(tour);
        reservaDe(4, DESDE, DESDE, paquete).confirmar();

        reporte.generar(List.of(), new Catalogo(paquete));

        assertEquals(4, filaDe(alojamiento).getCuposReservados());
        assertEquals(4, filaDe(excursion).getCuposReservados());
    }

    @Test
    void generar_unaReservaPendienteNoOcupaCupos() {
        reservaDe(3, DESDE, HASTA, hotel);

        reporte.generar(List.of(), new Catalogo(hotel));

        assertEquals(0, filaDe(alojamiento).getCuposReservados());
    }

    @Test
    void generar_unaReservaCanceladaDevuelveLosCupos() {
        Reserva reserva = reservaDe(3, DESDE, HASTA, hotel);
        reserva.confirmar();
        reserva.cancelar();

        reporte.generar(List.of(), new Catalogo(hotel));

        assertEquals(0, filaDe(alojamiento).getCuposReservados());
        assertEquals(100, filaDe(alojamiento).getCuposTotales());
    }

    @Test
    void generar_unaReservaCanceladaEnCursoSigueOcupandoLosCupos() {
        Reserva reserva = reservaDe(3, DESDE, DESDE, hotel);
        reserva.confirmar();
        reserva.hacerCheckin();
        reserva.iniciar();
        reserva.cancelar();

        reporte.generar(List.of(), new Catalogo(hotel));

        assertEquals(3, filaDe(alojamiento).getCuposReservados());
    }

    @Test
    void generar_unaReservaFueraDelPeriodoNoOcupaCupos() {
        reservaDe(3, HASTA.plusDays(1), HASTA.plusDays(5), hotel).confirmar();

        reporte.generar(List.of(), new Catalogo(hotel));

        assertEquals(0, filaDe(alojamiento).getCuposReservados());
        assertEquals(100, filaDe(alojamiento).getCuposTotales());
    }

    @Test
    void generar_unaReservaQueCruzaElPeriodoSoloCuentaLosDiasDeAdentro() {
        reservaDe(2, DESDE.minusDays(5), DESDE.plusDays(1), hotel).confirmar();

        reporte.generar(List.of(), new Catalogo(hotel));

        assertEquals(4, filaDe(alojamiento).getCuposReservados());
    }

    @Test
    void generar_dosVeces_noAcumulaLasFilasAnteriores() {
        reporte.generar(List.of(), new Catalogo(hotel));
        reporte.generar(List.of(), new Catalogo(hotel));

        assertEquals(100, filaDe(alojamiento).getCuposTotales());
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
