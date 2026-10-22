package unq.traslado;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

/**
 * Verifica que los tres metodos de traslado del enunciado (estandar, chauffeur/VIP y punto de
 * encuentro) sean intercambiables a traves de la interfaz {@link MetodoTraslado}, es decir, que el
 * patron Strategy este efectivamente implementado.
 *
 * <p>Los tests de {@code TrasladoChauffeur} y {@code PuntoEncuentro} fallan a proposito: esas clases
 * tienen el metodo {@code calcularCosto(Reserva)} con la firma correcta pero no declaran
 * {@code implements MetodoTraslado}. Ver H18 en HALLAZGOS.md.</p>
 */
class MetodoTrasladoTest {

    @Test
    void trasladoEstandar_esUnMetodoDeTraslado() {
        TrasladoEstandar traslado = new TrasladoEstandar(mock(PlataformaTransporte.class));

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
