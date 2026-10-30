package unq.buscador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;

class BuscadorCatalogoTest {

    private Catalogo hotel;
    private Catalogo excursion;
    private Filtro filtro;
    private BuscadorCatalogo buscador;

    @BeforeEach
    void setUp() {
        hotel = mock(Catalogo.class);
        excursion = mock(Catalogo.class);
        filtro = mock(Filtro.class);
        buscador = new BuscadorCatalogo(new ArrayList<>(List.of(hotel, excursion)));
    }

    @Test
    void buscar_devuelveSoloLosItemsQueCumplenElFiltro() {
        when(filtro.cumple(hotel)).thenReturn(true);
        when(filtro.cumple(excursion)).thenReturn(false);

        assertEquals(List.of(hotel), buscador.buscar(filtro));
    }

    @Test
    void buscar_sinItemsQueCumplan_devuelveListaVacia() {
        assertTrue(buscador.buscar(filtro).isEmpty());
    }

    @Test
    void buscar_conservaElOrdenDeLosItems() {
        when(filtro.cumple(hotel)).thenReturn(true);
        when(filtro.cumple(excursion)).thenReturn(true);

        assertEquals(List.of(hotel, excursion), buscador.buscar(filtro));
    }

    @Test
    void agregarItem_haceQueElItemParticipeDeLaBusqueda() {
        Catalogo traslado = mock(Catalogo.class);
        when(filtro.cumple(traslado)).thenReturn(true);

        buscador.agregarItem(traslado);

        assertEquals(List.of(traslado), buscador.buscar(filtro));
    }

    @Test
    void buscar_conFiltroNot_excluyeLosItemsQueCumplenElCriterio() {
        when(filtro.cumple(hotel)).thenReturn(true);
        when(filtro.cumple(excursion)).thenReturn(false);

        assertEquals(List.of(excursion), buscador.buscar(new FiltroNot(filtro)));
    }

    @Test
    void buscar_conFiltrosRealesCombinados_devuelveLosItemsCorrectos() {
        Categoria alojamiento = new Categoria("Alojamiento");
        ServicioIndividual hotelBarato = new ServicioIndividual("H1", "Hotel Sur", "d", "op",
                alojamiento, 8000.0, 5);
        ServicioIndividual hotelCaro = new ServicioIndividual("H2", "Hotel Lujo", "d", "op",
                alojamiento, 50000.0, 5);
        ServicioIndividual hotelSinCupo = new ServicioIndividual("H3", "Hotel Norte", "d", "op",
                alojamiento, 7000.0, 0);
        ServicioIndividual tour = new ServicioIndividual("T1", "Tour Hotelero", "d", "op",
                new Categoria("Excursion"), 3000.0, 5);
        BuscadorCatalogo real = new BuscadorCatalogo(
                new ArrayList<>(List.of(hotelBarato, hotelCaro, hotelSinCupo, tour)));

        Filtro criterio = new FiltroAnd(
                new FiltroNombre("hotel"),
                new FiltroCategoria(alojamiento),
                new FiltroPrecioMaximo(10000.0),
                new FiltroPorCupos());

        assertEquals(List.of(hotelBarato), real.buscar(criterio));
    }
}
