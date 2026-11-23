package unq.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;
import unq.catalogo.ItemCatalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;
import unq.reserva.Reserva;

/**
 * Un caso de punta a punta del módulo de reportes: reservas -> generar() -> exportar (Visitor).
 * El detalle de cada reporte y de cada formato está en sus propios tests.
 */
class ReportesPuntaAPuntaTest {

    private static final Periodo ENERO =
            new Periodo(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
    private static final LocalDate EN_ENERO = LocalDate.of(2026, 1, 10);

    private final ExportadorTextoPlano textoPlano = new ExportadorTextoPlano();

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

    private static ItemCatalogo paquete(String nombre, double precioFinal, double factorDeDescuento,
            ServicioIndividual... servicios) {
        ItemCatalogo paquete = mock(ItemCatalogo.class);
        when(paquete.nombre()).thenReturn(nombre);
        when(paquete.precioFinal()).thenReturn(precioFinal);
        when(paquete.factorDeDescuento()).thenReturn(factorDeDescuento);
        when(paquete.servicios()).thenReturn(List.of(servicios));
        when(paquete.componentes()).thenReturn(List.<ItemCatalogo>of(servicios));
        return paquete;
    }

    private static Reserva reserva(boolean cuentaComoVenta, LocalDate fecha, int pasajeros,
            ItemCatalogo... items) {
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
        ItemCatalogo pack = paquete("Pack & Spa", 1260.0, 0.9, hotel, tour);

        ReporteServiciosMasReservados reporte = new ReporteServiciosMasReservados(ENERO);
        reporte.generar(
                List.of(reserva(true, EN_ENERO, 2, pack),
                        reserva(true, LocalDate.of(2026, 2, 10), 3, hotel),
                        reserva(false, EN_ENERO, 1, tour)),
                new Catalogo(hotel, tour, pack));
        return reporte;
    }

    @Test
    void reporteDeVentas_generadoYExportadoATextoPlano() {
        assertEquals("SERVICIOS MAS RESERVADOS (2026-01-01 a 2026-01-31)\n"
                + "- Hotel Central: 1 vendidos, precio promedio $900.00\n"
                + "- Tour, \"Guía\" <VIP>: 1 vendidos, precio promedio $360.00\n"
                + "- Pack & Spa: 1 vendidos, precio promedio $1260.00\n",
                reporteDeVentas().aceptar(textoPlano));
    }
}
