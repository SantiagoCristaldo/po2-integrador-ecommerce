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
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;
import unq.reserva.Reserva;

class ReporteOcupacionTest {

    private Periodo periodo;
    private ReporteOcupacion reporte;
    private Categoria alojamiento;
    private Categoria excursion;
    private ServicioIndividual hotel;
    private ServicioIndividual hostel;
    private ServicioIndividual tour;

    @BeforeEach
    void setUp() {
        periodo = new Periodo(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        reporte = new ReporteOcupacion(periodo);
        alojamiento = new Categoria("Alojamiento");
        excursion = new Categoria("Excursion");
        hotel = new ServicioIndividual("H1", "Hotel", "d", "op", alojamiento, 1000.0, 10);
        hostel = new ServicioIndividual("H2", "Hostel", "d", "op", alojamiento, 500.0, 5);
        tour = new ServicioIndividual("T1", "Tour", "d", "op", excursion, 300.0, 20);
    }

    private Reserva reserva(boolean cuentaComoVenta, LocalDate fecha, int pasajeros, Catalogo... items) {
        Reserva reserva = mock(Reserva.class);
        when(reserva.cuentaComoVenta()).thenReturn(cuentaComoVenta);
        when(reserva.getFecha()).thenReturn(fecha);
        when(reserva.cantidadDePasajeros()).thenReturn(pasajeros);
        when(reserva.getItems()).thenReturn(List.of(items));
        return reserva;
    }

    private FilaOcupacion filaDe(Categoria categoria) {
        return reporte.getFilas().stream()
                .filter(f -> f.getCategoria().equals(categoria)).findFirst().orElseThrow();
    }

    @Test
    void generar_sumaLosCuposTotalesPorCategoria() {
        reporte.generar(List.of(), List.of(hotel, hostel, tour));

        assertEquals(2, reporte.getFilas().size());
        assertEquals(15, filaDe(alojamiento).getCuposTotales());
        assertEquals(20, filaDe(excursion).getCuposTotales());
    }

    @Test
    void generar_noCuentaDosVecesUnServicioQueTambienEstaEnUnPaquete() {
        Catalogo paquete = mock(Catalogo.class);
        when(paquete.servicios()).thenReturn(List.of(hotel, tour));

        reporte.generar(List.of(), List.of(hotel, tour, paquete));

        assertEquals(10, filaDe(alojamiento).getCuposTotales());
        assertEquals(20, filaDe(excursion).getCuposTotales());
    }

    @Test
    void generar_sumaLosPasajerosDeLasReservasComoCuposReservados() {
        LocalDate enero = LocalDate.of(2026, 1, 10);

        reporte.generar(List.of(reserva(true, enero, 3, hotel), reserva(true, enero, 2, hostel)),
                List.of(hotel, hostel, tour));

        assertEquals(5, filaDe(alojamiento).getCuposReservados());
        assertEquals(10, filaDe(alojamiento).getCuposLibres());
        assertEquals(0, filaDe(excursion).getCuposReservados());
    }

    @Test
    void generar_conPaqueteReservado_ocupaCuposEnCadaCategoriaDeSusServicios() {
        Catalogo paquete = mock(Catalogo.class);
        when(paquete.servicios()).thenReturn(List.of(hotel, tour));

        reporte.generar(List.of(reserva(true, LocalDate.of(2026, 1, 10), 4, paquete)),
                List.of(hotel, tour, paquete));

        assertEquals(4, filaDe(alojamiento).getCuposReservados());
        assertEquals(4, filaDe(excursion).getCuposReservados());
    }

    @Test
    void generar_ignoraLasReservasQueNoCuentanComoVenta() {
        reporte.generar(List.of(reserva(false, LocalDate.of(2026, 1, 10), 3, hotel)), List.of(hotel));

        assertEquals(0, filaDe(alojamiento).getCuposReservados());
    }

    @Test
    void generar_ignoraLasReservasFueraDelPeriodo() {
        reporte.generar(List.of(reserva(true, LocalDate.of(2025, 12, 31), 3, hotel)), List.of(hotel));

        assertEquals(0, filaDe(alojamiento).getCuposReservados());
    }

    @Test
    void generar_dosVeces_noAcumulaLasFilasAnteriores() {
        reporte.generar(List.of(), List.of(hotel));
        reporte.generar(List.of(), List.of(hotel));

        assertEquals(10, filaDe(alojamiento).getCuposTotales());
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
