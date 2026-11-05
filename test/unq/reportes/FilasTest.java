package unq.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;

class FilasTest {

    @Test
    void filaVentas_sinVentas_tieneCeroUnidadesYPromedioCero() {
        FilaVentas fila = new FilaVentas(mock(Catalogo.class));

        assertEquals(0, fila.getUnidadesVendidas());
        assertEquals(0.0, fila.getPrecioPromedioCobrado(), 0.001);
    }

    @Test
    void filaVentas_registrarVenta_sumaUnidadesYPromediaLoCobrado() {
        Catalogo item = mock(Catalogo.class);
        FilaVentas fila = new FilaVentas(item);

        fila.registrarVenta(1000.0);
        fila.registrarVenta(2000.0);

        assertSame(item, fila.getItem());
        assertEquals(2, fila.getUnidadesVendidas());
        assertEquals(1500.0, fila.getPrecioPromedioCobrado(), 0.001);
    }

    @Test
    void filaOcupacion_sinCupos_tieneTodoEnCero() {
        FilaOcupacion fila = new FilaOcupacion(new Categoria("Alojamiento"));

        assertEquals(0, fila.getCuposTotales());
        assertEquals(0, fila.getCuposReservados());
        assertEquals(0, fila.getCuposLibres());
    }

    @Test
    void filaOcupacion_acumulaCuposYCalculaLibres() {
        Categoria alojamiento = new Categoria("Alojamiento");
        FilaOcupacion fila = new FilaOcupacion(alojamiento);

        fila.sumar(10, 5);
        fila.sumar(10, 0);

        assertEquals(alojamiento, fila.getCategoria());
        assertEquals(20, fila.getCuposTotales());
        assertEquals(5, fila.getCuposReservados());
        assertEquals(15, fila.getCuposLibres());
    }

    @Test
    void filaExposicion_guardaOperadorCantidadYPorcentaje() {
        FilaExposicion fila = new FilaExposicion("Operador SA", 3, 75.0);

        assertEquals("Operador SA", fila.getOperador());
        assertEquals(3, fila.getCantidadItems());
        assertEquals(75.0, fila.getPorcentajeCatalogo(), 0.001);
    }
}
