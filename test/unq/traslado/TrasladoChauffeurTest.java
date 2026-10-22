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
        when(reserva.valorTotal()).thenReturn(15000f);
    }

    @Test
    void calcularCosto_devuelveElCostoCalculadoPorElServicioVIP() {
        when(servicioVIP.calcularCosto(15000f)).thenReturn(2000f);

        assertEquals(2000f, chauffeur.calcularCosto(reserva), 0.001f);
    }

    @Test
    void calcularCosto_pasaElValorTotalDeLaReservaAlServicioVIP() {
        chauffeur.calcularCosto(reserva);

        verify(servicioVIP).calcularCosto(15000f);
    }
}
