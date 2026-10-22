package unq.traslado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.reserva.Reserva;

class TrasladoEstandarTest {

    private PlataformaTransporte plataforma;
    private TrasladoEstandar traslado;
    private Reserva reserva;
    private Direccion destino;

    @BeforeEach
    void setUp() {
        plataforma = mock(PlataformaTransporte.class);
        traslado = new TrasladoEstandar(plataforma);
        reserva = mock(Reserva.class);
        destino = new Direccion("Calle Falsa", 123, "Quilmes", "1878");
        when(reserva.cantidadDePasajeros()).thenReturn(3);
        when(reserva.getDestino()).thenReturn(destino);
    }

    @Test
    void calcularCosto_devuelveElCostoEstimadoPorLaPlataforma() {
        when(plataforma.estimarTraslado(3, destino)).thenReturn(1500f);

        assertEquals(1500f, traslado.calcularCosto(reserva), 0.001f);
    }

    @Test
    void calcularCosto_pasaLaCantidadDePasajerosYElDestinoALaPlataforma() {
        traslado.calcularCosto(reserva);

        verify(plataforma).estimarTraslado(3, destino);
    }
}
