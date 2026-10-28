package unq.traslado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.reserva.Reserva;

class TrasladoChauffeurTest {

    private TrasladoVIP servicioVIP;
    private TrasladoChauffeur chauffeur;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        servicioVIP = mock(TrasladoVIP.class);
        chauffeur = new TrasladoChauffeur(servicioVIP);
        reserva = mock(Reserva.class);
        when(reserva.valorTotal()).thenReturn(15000.0);
    }

    @Test
    void calcularCosto_devuelveElCostoCalculadoPorElServicioVIP() {
        when(servicioVIP.calcularCosto(15000.0)).thenReturn(2000.0);

        assertEquals(2000.0, chauffeur.calcularCosto(reserva), 0.001);
    }

    @Test
    void calcularCosto_pasaElValorTotalDeLaReservaAlServicioVIP() {
        chauffeur.calcularCosto(reserva);

        verify(servicioVIP).calcularCosto(15000f);
    }
}
