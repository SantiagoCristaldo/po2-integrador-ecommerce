package unq.reserva;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;
import unq.traslado.Direccion;

class ReservaTest {

    private Reserva reserva;
    private Catalogo item;
    private Direccion destino;

    @BeforeEach
    void setUp() {
        destino = new Direccion("Calle Falsa", 123, "Quilmes", "1878");
        reserva = new Reserva(destino, 2);
        item = new ServicioIndividual("HOT-001", "Hotel Central", "Un hotel",
                "Operador SA", new Categoria("Alojamiento"), 15000.0);
    }

    // --- Reserva es el Context: delega cada operación pública en el estado actual ---

    @Test
    void agregarItem_delegaEnElEstadoActualPasandoLaReservaYElItem() {
        EstadoDeReserva estado = mock(EstadoDeReserva.class);
        reserva.cambiarEstado(estado);

        reserva.agregarItem(item);

        verify(estado).agregarItem(reserva, item);
    }

    @Test
    void quitarItem_delegaEnElEstadoActualPasandoLaReservaYElItem() {
        EstadoDeReserva estado = mock(EstadoDeReserva.class);
        reserva.cambiarEstado(estado);

        reserva.quitarItem(item);

        verify(estado).quitarItem(reserva, item);
    }

    @Test
    void confirmar_delegaEnElEstadoActualPasandoLaReserva() {
        EstadoDeReserva estado = mock(EstadoDeReserva.class);
        reserva.cambiarEstado(estado);

        reserva.confirmar();

        verify(estado).confirmar(reserva);
    }

    @Test
    void hacerCheckin_delegaEnElEstadoActualPasandoLaReserva() {
        EstadoDeReserva estado = mock(EstadoDeReserva.class);
        reserva.cambiarEstado(estado);

        reserva.hacerCheckin();

        verify(estado).hacerCheckin(reserva);
    }

    @Test
    void iniciar_delegaEnElEstadoActualPasandoLaReserva() {
        EstadoDeReserva estado = mock(EstadoDeReserva.class);
        reserva.cambiarEstado(estado);

        reserva.iniciar();

        verify(estado).iniciar(reserva);
    }

    @Test
    void finalizar_delegaEnElEstadoActualPasandoLaReserva() {
        EstadoDeReserva estado = mock(EstadoDeReserva.class);
        reserva.cambiarEstado(estado);

        reserva.finalizar();

        verify(estado).finalizar(reserva);
    }

    @Test
    void cancelar_delegaEnElEstadoActualPasandoLaReserva() {
        EstadoDeReserva estado = mock(EstadoDeReserva.class);
        reserva.cambiarEstado(estado);

        reserva.cancelar();

        verify(estado).cancelar(reserva);
    }

    @Test
    void cambiarEstado_reemplazaElEstadoAnteriorPorElNuevo() {
        EstadoDeReserva anterior = mock(EstadoDeReserva.class);
        EstadoDeReserva nuevo = mock(EstadoDeReserva.class);
        reserva.cambiarEstado(anterior);

        reserva.cambiarEstado(nuevo);
        reserva.confirmar();

        verify(nuevo).confirmar(reserva);
        verify(anterior, never()).confirmar(any());
    }

    // --- Reserva con los estados reales (spy) ---

    @Test
    void nuevaReserva_empiezaEnSolicitudPendiente() {
        assertDoesNotThrow(() -> reserva.agregarItem(item));
        assertThrows(ExcepcionPorInvalido.class, reserva::hacerCheckin);
    }

    @Test
    void agregarItem_enSolicitudPendiente_llegaALosItemsDeLaReserva() {
        Reserva espia = spy(reserva);

        espia.agregarItem(item);

        verify(espia).agregarItemDirecto(item);
    }

    @Test
    void quitarItem_enSolicitudPendiente_llegaALosItemsDeLaReserva() {
        Reserva espia = spy(reserva);

        espia.quitarItem(item);

        verify(espia).quitarItemDirecto(item);
    }

    @Test
    void confirmar_bloqueaElCupoDeLasUnidades() {
        Reserva espia = spy(reserva);

        espia.confirmar();

        verify(espia).bloquearCupos();
    }

    @Test
    void cancelar_desdeSolicitudPendiente_noLiberaCuposNiReembolsa() {
        Reserva espia = spy(reserva);

        espia.cancelar();

        verify(espia, never()).liberarCupos();
        verify(espia, never()).reembolsar();
    }

    @Test
    void cancelar_desdeEnChecking_liberaElCupoDeLasUnidades() {
        Reserva espia = spy(reserva);
        espia.confirmar();
        espia.hacerCheckin();

        espia.cancelar();

        verify(espia).liberarCupos();
    }

    @Test
    void cancelar_desdeEnChecking_reembolsaLosServiciosYTasasDeReserva() {
        Reserva espia = spy(reserva);
        espia.confirmar();
        espia.hacerCheckin();

        espia.cancelar();

        verify(espia).reembolsar();
    }

    // --- Datos que consume el módulo de traslado ---

    @Test
    void cantidadDePasajeros_devuelveLaCantidadDelConstructor() {
        assertEquals(2, reserva.cantidadDePasajeros());
    }

    @Test
    void getDestino_devuelveElDestinoDelConstructor() {
        assertSame(destino, reserva.getDestino());
    }

    @Test
    void valorTotal_devuelveCero_mientrasLaFormulaNoEsteDefinida() {
        assertEquals(0f, reserva.valorTotal());
    }
}
