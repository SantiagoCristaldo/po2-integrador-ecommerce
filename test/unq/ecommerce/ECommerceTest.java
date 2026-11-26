package unq.ecommerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.buscador.FiltroCategoria;
import unq.buscador.FiltroNombre;
import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ItemCatalogo;
import unq.catalogo.PaqueteTuristico;
import unq.catalogo.ServicioIndividual;
import unq.excepciones.ExcepcionDominio;
import unq.reportes.ExportadorReporte;
import unq.reportes.Reporte;
import unq.metodosDePago.MedioDePago;
import unq.reserva.Cancelada;
import unq.reserva.Pagado;
import unq.reserva.Reserva;
import unq.reserva.SolicitudPendiente;
import unq.traslado.Direccion;
import unq.traslado.MetodoTraslado;

class ECommerceTest {

    private final Categoria alojamiento = new Categoria("Alojamiento");
    private final Categoria excursion = new Categoria("Excursion");
    private final Direccion destino = mock(Direccion.class);
    private final LocalDate inicio = LocalDate.of(2026, 1, 10);
    private final LocalDate fin = LocalDate.of(2026, 1, 12);

    private ECommerce ecommerce;
    private ServicioIndividual hotel;
    private ServicioIndividual glaciar;

    @BeforeEach
    void setUp() {
        ecommerce = new ECommerce();
        hotel = ecommerce.crearServicio("H1", "Hotel Llao Llao", "5 estrellas",
                "Llao Llao SA", alojamiento, 1000, 10);
        glaciar = ecommerce.crearServicio("E1", "Excursion Glaciar", "Día completo",
                "Hielo y Aventura", excursion, 500, 20);
    }

    @Test
    void constructor_sinCatalogo_arrancaConCatalogoVacio() {
        ECommerce vacio = new ECommerce();

        assertTrue(vacio.itemsDelCatalogo().isEmpty());
        assertTrue(vacio.getReservas().isEmpty());
    }

    @Test
    void constructor_conCatalogo_usaEseCatalogo() {
        ItemCatalogo item = mock(ItemCatalogo.class);
        Catalogo catalogo = new Catalogo(item);

        ECommerce conCatalogo = new ECommerce(catalogo);

        assertSame(catalogo, conCatalogo.getCatalogo());
        assertEquals(List.of(item), conCatalogo.itemsDelCatalogo());
    }

    @Test
    void crearServicio_loAgregaAlCatalogo() {
        assertEquals(List.of(hotel, glaciar), ecommerce.itemsDelCatalogo());
        assertEquals("Hotel Llao Llao", hotel.nombre());
        assertEquals(1000, hotel.precioBase());
    }

    @Test
    void crearServicio_sinAtributoObligatorio_fallaYNoLoAgrega() {
        assertThrows(ExcepcionDominio.class, () -> ecommerce.crearServicio("H2", "", "desc",
                "operador", alojamiento, 100, 5));
        assertEquals(2, ecommerce.itemsDelCatalogo().size());
    }

    @Test
    void crearPaquete_conItems_loAgregaAlCatalogoConDescuento() {
        PaqueteTuristico paquete = ecommerce.crearPaquete("Bariloche", "Hotel + glaciar", 10,
                hotel, glaciar);

        assertTrue(ecommerce.estaEnCatalogo(paquete));
        assertEquals(List.of(hotel, glaciar), paquete.componentes());
        assertEquals(1350, paquete.precioFinal(), 0.001);
    }

    @Test
    void agregarAlCatalogo_itemNuevo_loAgrega() {
        ItemCatalogo item = mock(ItemCatalogo.class);

        ecommerce.agregarAlCatalogo(item);

        assertTrue(ecommerce.estaEnCatalogo(item));
    }

    @Test
    void agregarAlCatalogo_itemRepetido_falla() {
        assertThrows(ExcepcionDominio.class, () -> ecommerce.agregarAlCatalogo(hotel));
    }

    @Test
    void quitarDelCatalogo_loSaca() {
        ecommerce.quitarDelCatalogo(hotel);

        assertFalse(ecommerce.estaEnCatalogo(hotel));
        assertEquals(List.of(glaciar), ecommerce.itemsDelCatalogo());
    }

    @Test
    void buscar_filtraLosItemsDelCatalogo() {
        assertEquals(List.of(glaciar), ecommerce.buscar(new FiltroCategoria(excursion)));
        assertEquals(List.of(hotel), ecommerce.buscar(new FiltroNombre("llao")));
    }

    @Test
    void crearReserva_conItemsDelCatalogo_laRegistra() {
        Reserva reserva = ecommerce.crearReserva("cliente@mail.com", destino, 2, inicio, fin,
                hotel, glaciar);

        assertEquals(List.of(hotel, glaciar), reserva.getItems());
        assertEquals(List.of(reserva), ecommerce.getReservas());
        assertEquals(1500, reserva.valorServicios(), 0.001);
    }

    @Test
    void crearReserva_conItemFueraDelCatalogo_fallaYNoLaRegistra() {
        ItemCatalogo ajeno = mock(ItemCatalogo.class);
        when(ajeno.nombre()).thenReturn("Ajeno");

        assertThrows(ExcepcionDominio.class, () -> ecommerce.crearReserva("cliente@mail.com",
                destino, 2, inicio, fin, ajeno));
        assertTrue(ecommerce.getReservas().isEmpty());
    }

    @Test
    void registrarReserva_conItemsDelCatalogo_laAgrega() {
        Reserva reserva = new Reserva("ana@mail.com", destino, 1, inicio, fin);
        reserva.agregarItem(hotel);

        ecommerce.registrarReserva(reserva);

        assertEquals(List.of(reserva), ecommerce.getReservas());
    }

    @Test
    void registrarReserva_conItemFueraDelCatalogo_falla() {
        ServicioIndividual ajeno = new ServicioIndividual("X1", "Ajeno", "desc", "op",
                alojamiento, 100, 5);
        Reserva reserva = new Reserva("ana@mail.com", destino, 1, inicio, fin);
        reserva.agregarItem(ajeno);

        assertThrows(ExcepcionDominio.class, () -> ecommerce.registrarReserva(reserva));
        assertTrue(ecommerce.getReservas().isEmpty());
    }

    @Test
    void confirmarReserva_cobraConfirmaYBloqueaCupos() {
        Reserva reserva = ecommerce.crearReserva("ana@mail.com", destino, 2, inicio, fin, hotel);
        MedioDePago medio = mock(MedioDePago.class);
        reserva.setMetodoPago(medio);
        reserva.setMetodoTraslado(mock(MetodoTraslado.class));

        ecommerce.confirmarReserva(reserva);

        verify(medio).procesoDePago();
        assertInstanceOf(Pagado.class, reserva.getEstadoActual());
        assertEquals(8, hotel.getCuposDisponiblesEn(inicio));
    }

    @Test
    void confirmarReserva_sinCupo_fallaSinCobrar() {
        Reserva reserva = ecommerce.crearReserva("ana@mail.com", destino, 11, inicio, fin, hotel);
        MedioDePago medio = mock(MedioDePago.class);
        reserva.setMetodoPago(medio);

        assertThrows(ExcepcionDominio.class, () -> ecommerce.confirmarReserva(reserva));
        verify(medio, never()).procesoDePago();
        assertInstanceOf(SolicitudPendiente.class, reserva.getEstadoActual());
    }

    @Test
    void confirmarReserva_noRegistrada_falla() {
        Reserva reserva = new Reserva("ana@mail.com", destino, 1, inicio, fin);

        assertThrows(ExcepcionDominio.class, () -> ecommerce.confirmarReserva(reserva));
    }

    @Test
    void cancelarReserva_confirmada_liberaLosCupos() {
        Reserva reserva = ecommerce.crearReserva("ana@mail.com", destino, 2, inicio, fin, hotel);
        reserva.setMetodoPago(mock(MedioDePago.class));
        reserva.setMetodoTraslado(mock(MetodoTraslado.class));
        ecommerce.confirmarReserva(reserva);

        ecommerce.cancelarReserva(reserva);

        assertInstanceOf(Cancelada.class, reserva.getEstadoActual());
        assertEquals(10, hotel.getCuposDisponiblesEn(inicio));
    }

    @Test
    void cancelarReserva_noRegistrada_falla() {
        Reserva reserva = new Reserva("ana@mail.com", destino, 1, inicio, fin);

        assertThrows(ExcepcionDominio.class, () -> ecommerce.cancelarReserva(reserva));
    }

    @Test
    void reservasDe_devuelveSoloLasDelEmail() {
        Reserva deAna = ecommerce.crearReserva("ana@mail.com", destino, 1, inicio, fin, hotel);
        ecommerce.crearReserva("beto@mail.com", destino, 1, inicio, fin, glaciar);

        assertEquals(List.of(deAna), ecommerce.reservasDe("ana@mail.com"));
    }

    @Test
    void generarReporte_loGeneraConReservasYCatalogo() {
        Reserva reserva = ecommerce.crearReserva("ana@mail.com", destino, 1, inicio, fin, hotel);
        Reporte reporte = mock(Reporte.class);

        assertSame(reporte, ecommerce.generarReporte(reporte));
        verify(reporte).generar(List.of(reserva), ecommerce.getCatalogo());
    }

    @Test
    void exportarReporte_loGeneraYLoExporta() {
        Reporte reporte = mock(Reporte.class);
        ExportadorReporte exportador = mock(ExportadorReporte.class);
        when(reporte.aceptar(exportador)).thenReturn("contenido");

        assertEquals("contenido", ecommerce.exportarReporte(reporte, exportador));
        verify(reporte).generar(List.of(), ecommerce.getCatalogo());
    }
}
