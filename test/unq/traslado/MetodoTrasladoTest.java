package unq.traslado;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

/**
 * Verifica que los tres metodos de traslado del enunciado (estandar, chauffeur/VIP y punto de
 * encuentro) sean intercambiables a traves de la interfaz {@link MetodoTraslado}, es decir, que el
 * patron Strategy este efectivamente implementado.
 */
class MetodoTrasladoTest {

    @Test
    void trasladoEstandar_esUnMetodoDeTraslado() {
        TrasladoEstandar traslado = new TrasladoEstandar(mock(PlataformaTransporte.class), mock(Direccion.class));

        assertInstanceOf(MetodoTraslado.class, traslado);
    }

    @Test
    void trasladoChauffeur_esUnMetodoDeTraslado() {
        TrasladoChauffeur traslado = new TrasladoChauffeur(mock(TrasladoVIP.class));

        assertInstanceOf(MetodoTraslado.class, traslado);
    }

    @Test
    void puntoDeEncuentro_esUnMetodoDeTraslado() {
        assertInstanceOf(MetodoTraslado.class, new PuntoEncuentro("Aeropuerto Ezeiza", 3));
    }
}
