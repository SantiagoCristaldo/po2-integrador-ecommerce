package unq.ecommerce;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import unq.buscador.BuscadorCatalogo;
import unq.buscador.Filtro;
import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ItemCatalogo;
import unq.catalogo.PaqueteTuristico;
import unq.catalogo.ServicioIndividual;
import unq.excepciones.ExcepcionDominio;
import unq.reportes.ExportadorReporte;
import unq.reportes.Reporte;
import unq.reserva.Reserva;
import unq.traslado.Direccion;

/**
 * Punto de entrada del sistema: tiene el catálogo de la empresa y las reservas que se hicieron.
 * Permite crear servicios y paquetes (que quedan agregados al catálogo), buscar en el catálogo,
 * crear reservas con ítems del catálogo y generar reportes.
 */
public class ECommerce {
    private final Catalogo catalogo;
    private final BuscadorCatalogo buscador;
    private final List<Reserva> reservas = new ArrayList<>();

    public ECommerce() {
        this(new Catalogo());
    }

    public ECommerce(Catalogo catalogo) {
        this.catalogo = catalogo;
        this.buscador = new BuscadorCatalogo(catalogo);
    }

    // ---- Catálogo ----

    public ServicioIndividual crearServicio(String codigo, String nombre, String descripcion,
            String operador, Categoria categoria, double precioBase, int cuposDisponibles) {
        ServicioIndividual servicio = new ServicioIndividual(codigo, nombre, descripcion,
                operador, categoria, precioBase, cuposDisponibles);
        servicio.validar();
        catalogo.agregarItem(servicio);
        return servicio;
    }

    public PaqueteTuristico crearPaquete(String nombre, String descripcion, int descuento,
            ItemCatalogo... items) {
        PaqueteTuristico paquete = new PaqueteTuristico(nombre, descripcion, descuento, 0);
        for (ItemCatalogo item : items) {
            paquete.agregarItem(item);
        }
        catalogo.agregarItem(paquete);
        return paquete;
    }

    public void agregarAlCatalogo(ItemCatalogo item) {
        if (estaEnCatalogo(item)) {
            throw new ExcepcionDominio("El ítem '" + item.nombre() + "' ya está en el catálogo");
        }
        catalogo.agregarItem(item);
    }

    public void quitarDelCatalogo(ItemCatalogo item) {
        catalogo.quitarItem(item);
    }

    public boolean estaEnCatalogo(ItemCatalogo item) {
        return catalogo.getItems().contains(item);
    }

    public List<ItemCatalogo> itemsDelCatalogo() {
        return catalogo.getItems();
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public List<ItemCatalogo> buscar(Filtro filtro) {
        return buscador.buscar(filtro);
    }

    // ---- Reservas ----

    public Reserva crearReserva(String email, Direccion destino, int cantidadDePasajeros,
            LocalDate fechaInicio, LocalDate fechaFin, ItemCatalogo... items) {
        Reserva reserva = new Reserva(email, destino, cantidadDePasajeros, fechaInicio, fechaFin);
        for (ItemCatalogo item : items) {
            if (!estaEnCatalogo(item)) {
                throw new ExcepcionDominio("El ítem '" + item.nombre() + "' no está en el catálogo");
            }
            reserva.agregarItem(item);
        }
        reservas.add(reserva);
        return reserva;
    }

    public void registrarReserva(Reserva reserva) {
        for (ItemCatalogo item : reserva.getItems()) {
            if (!estaEnCatalogo(item)) {
                throw new ExcepcionDominio("El ítem '" + item.nombre() + "' no está en el catálogo");
            }
        }
        reservas.add(reserva);
    }

    /**
     * Cobra la reserva y la confirma (al confirmarse se bloquean los cupos de sus servicios).
     * Antes de cobrar verifica que haya cupo, para no cobrar una reserva que no se puede confirmar.
     */
    public void confirmarReserva(Reserva reserva) {
        verificarQueEstaRegistrada(reserva);
        if (!hayCupoPara(reserva)) {
            throw new ExcepcionDominio("No hay cupo disponible para confirmar la reserva");
        }
        reserva.procesarPago();
        reserva.confirmar();
    }

    /** Cancela la reserva: su estado decide si se liberan los cupos y qué se reembolsa. */
    public void cancelarReserva(Reserva reserva) {
        verificarQueEstaRegistrada(reserva);
        reserva.cancelar();
    }

    private boolean hayCupoPara(Reserva reserva) {
        return reserva.getItems().stream().allMatch(item -> item.tieneCupo(
                reserva.getFechaInicio(), reserva.getFechaFin(), reserva.cantidadDePasajeros()));
    }

    private void verificarQueEstaRegistrada(Reserva reserva) {
        if (!reservas.contains(reserva)) {
            throw new ExcepcionDominio("La reserva no está registrada en el e-commerce");
        }
    }

    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }

    public List<Reserva> reservasDe(String email) {
        return reservas.stream().filter(r -> r.getEmail().equals(email)).toList();
    }

    // ---- Reportes ----

    public <R extends Reporte> R generarReporte(R reporte) {
        reporte.generar(getReservas(), catalogo);
        return reporte;
    }

    public String exportarReporte(Reporte reporte, ExportadorReporte exportador) {
        return generarReporte(reporte).aceptar(exportador);
    }
}
