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
import unq.catalogo.PaqueteTuristico;
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

    // --- Ejemplos de consultas anidadas del enunciado (2.6), con items reales ---

    private final Categoria catAlojamiento = new Categoria("Alojamiento");
    private final Categoria catExcursion = new Categoria("Excursion");

    private ServicioIndividual servicio(String nombre, Categoria categoria, double precio, int cupos) {
        return new ServicioIndividual(nombre, nombre, "d", "op", categoria, precio, cupos);
    }

    @Test
    void buscar_porNombre_noDistingueMayusculas() {
        ServicioIndividual hotel = servicio("HOTEL Central", catAlojamiento, 15000.0, 5);
        BuscadorCatalogo real = new BuscadorCatalogo(new ArrayList<>(List.of(hotel)));

        assertEquals(List.of(hotel), real.buscar(new FiltroNombre("hotel")));
    }

    @Test
    void buscar_alojamientoYDisponibleOOferta() {
        ServicioIndividual hotelDisponible = servicio("Hotel Central", catAlojamiento, 15000.0, 5);
        ServicioIndividual hotelLleno = servicio("Hotel Lleno", catAlojamiento, 15000.0, 0);
        ServicioIndividual tourOferta = servicio("Tour en oferta", catExcursion, 3000.0, 0);
        ServicioIndividual tourComun = servicio("Tour comun", catExcursion, 3000.0, 5);
        BuscadorCatalogo real = new BuscadorCatalogo(
                new ArrayList<>(List.of(hotelDisponible, hotelLleno, tourOferta, tourComun)));

        Filtro criterio = new FiltroOr(
                new FiltroAnd(new FiltroCategoria(catAlojamiento), new FiltroPorCupos()),
                new FiltroNombre("oferta"));

        assertEquals(List.of(hotelDisponible, tourOferta), real.buscar(criterio));
    }

    @Test
    void buscar_noMasCaroQue15000YAlojamientoOExcursion() {
        ServicioIndividual hotelBarato = servicio("Hotel", catAlojamiento, 15000.0, 5);
        ServicioIndividual hotelCaro = servicio("Resort", catAlojamiento, 15000.01, 5);
        ServicioIndividual tour = servicio("Tour", catExcursion, 4000.0, 5);
        ServicioIndividual vuelo = servicio("Vuelo", new Categoria("Vuelo"), 9000.0, 5);
        BuscadorCatalogo real = new BuscadorCatalogo(
                new ArrayList<>(List.of(hotelBarato, hotelCaro, tour, vuelo)));

        Filtro precioMayorA15000 = new FiltroNot(new FiltroPrecioMaximo(15000.0));
        Filtro criterio = new FiltroAnd(
                new FiltroNot(precioMayorA15000),
                new FiltroOr(new FiltroCategoria(catAlojamiento), new FiltroCategoria(catExcursion)));

        assertEquals(List.of(hotelBarato, tour), real.buscar(criterio));
    }

    @Test
    void buscar_packQueNoEsExcursionYCuestaHasta25000() {
        PaqueteTuristico packAlojamiento = new PaqueteTuristico("Pack Hoteles", "d", 0, 5);
        packAlojamiento.agregarItem(servicio("Hotel", catAlojamiento, 20000.0, 5));
        PaqueteTuristico packExcursiones = new PaqueteTuristico("Pack Aventura", "d", 0, 5);
        packExcursiones.agregarItem(servicio("Rafting", catExcursion, 5000.0, 5));
        PaqueteTuristico packCaro = new PaqueteTuristico("Pack Lujo", "d", 0, 5);
        packCaro.agregarItem(servicio("Suite", catAlojamiento, 40000.0, 5));
        ServicioIndividual hotelSuelto = servicio("Hotel sin paquete", catAlojamiento, 1000.0, 5);
        BuscadorCatalogo real = new BuscadorCatalogo(
                new ArrayList<>(List.of(packAlojamiento, packExcursiones, packCaro, hotelSuelto)));

        Filtro criterio = new FiltroAnd(
                new FiltroNombre("pack"),
                new FiltroNot(new FiltroCategoria(catExcursion)),
                new FiltroPrecioMaximo(25000.0));

        assertEquals(List.of(packAlojamiento), real.buscar(criterio));
    }

    @Test
    void buscar_unPaqueteEsDeLaCategoriaDeAlgunoDeSusItems() {
        PaqueteTuristico pack = new PaqueteTuristico("Pack", "d", 0, 5);
        pack.agregarItem(servicio("Hotel", catAlojamiento, 1000.0, 5));
        BuscadorCatalogo real = new BuscadorCatalogo(new ArrayList<>(List.of(pack)));

        assertEquals(List.of(pack), real.buscar(new FiltroCategoria(catAlojamiento)));
    }
}
