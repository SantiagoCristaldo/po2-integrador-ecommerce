package unq.reportes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import unq.catalogo.ItemCatalogo;
import unq.catalogo.Categoria;

/** Arma reportes mockeados con filas conocidas para probar los exportadores. */
class ReportesDeEjemplo {

    static final Periodo ENERO = new Periodo(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

    static ItemCatalogo item(String nombre) {
        ItemCatalogo item = mock(ItemCatalogo.class);
        when(item.nombre()).thenReturn(nombre);
        return item;
    }

    static FilaVentas filaVentas(String nombre, double... preciosCobrados) {
        FilaVentas fila = new FilaVentas(item(nombre));
        for (double precio : preciosCobrados) {
            fila.registrarVenta(precio);
        }
        return fila;
    }

    static FilaOcupacion filaOcupacion(String categoria, int totales, int reservados) {
        FilaOcupacion fila = new FilaOcupacion(new Categoria(categoria));
        fila.sumar(totales, reservados);
        return fila;
    }

    static ReporteServiciosMasReservados ventas(FilaVentas... filas) {
        ReporteServiciosMasReservados reporte = mock(ReporteServiciosMasReservados.class);
        when(reporte.getFilas()).thenReturn(List.of(filas));
        when(reporte.getPeriodo()).thenReturn(ENERO);
        return reporte;
    }

    static ReporteOcupacion ocupacion(FilaOcupacion... filas) {
        ReporteOcupacion reporte = mock(ReporteOcupacion.class);
        when(reporte.getFilas()).thenReturn(List.of(filas));
        when(reporte.getPeriodo()).thenReturn(ENERO);
        return reporte;
    }

    static ReporteExposicionProveedor exposicion(FilaExposicion... filas) {
        ReporteExposicionProveedor reporte = mock(ReporteExposicionProveedor.class);
        when(reporte.getFilas()).thenReturn(List.of(filas));
        return reporte;
    }
}
