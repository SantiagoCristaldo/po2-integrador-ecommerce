package unq.reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import unq.traslado.*;
import unq.catalogo.Catalogo;
import unq.notificaciones.*;
import unq.excepciones.*;
import unq.metodosDePago.*;

public class Reserva {
    private String email;
    private EstadoDeReserva estado;
    private List<Catalogo> items = new ArrayList<>();
    private int cantidadDePasajeros;
    private MetodoTraslado metodoTraslado;
    private Notificador notificacion = new Notificador();
    private LocalDate fecha;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private MedioDePago medioDePago;
    private List<VoucherReembolso> vouchersDeReembolso = new ArrayList<>();

    public Reserva(String email, Direccion destino, int cantidadDePasajeros,
            LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaFin.isBefore(fechaInicio)) {
            throw new ExcepcionDominio("La fecha final no puede ser anterior a la fecha de inicio");
        }
        this.email = email;
        this.estado = new SolicitudPendiente();
        this.cantidadDePasajeros = cantidadDePasajeros;
        this.fecha = LocalDate.now();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void agregarObservador(ObservadorReserva observador) {
        notificacion.agregarObservador(observador);
    }

    public void quitarObservador(ObservadorReserva observador) {
        notificacion.quitarObservador(observador);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public List<Catalogo> getItems() {
        return new ArrayList<>(items);
    }

    public boolean cuentaComoVenta() {
        return estado.cuentaComoVenta();
    }

    public EstadoDeReserva getEstadoActual() {
        return this.estado;
    }

    public double getCostoEnvio() {
        if (this.metodoTraslado == null)
            throw new ExcepcionDominio("Método de envío no seleccionado");
        return this.metodoTraslado.calcularCosto(this);
    }

    public int cantidadDePasajeros() {
        return cantidadDePasajeros;
    }

    public double valorServicios() {
        return items.stream()
                .mapToDouble(Catalogo::precioFinal)
                .sum();
    }

    public double valorTotal() {
        double costoEnvio = this.getCostoEnvio();
        return this.valorServicios() + costoEnvio;
    }

    public void agregarItem(Catalogo item) {
        estado.agregarItem(this, item);
    }

    public void quitarItem(Catalogo item) {
        estado.quitarItem(this, item);
    }

    public void confirmar() {
        estado.confirmar(this);
    }

    public void hacerCheckin() {
        estado.hacerCheckin(this);
    }

    public void iniciar() {
        estado.iniciar(this);
    }

    public void finalizar() {
        estado.finalizar(this);
    }

    public void cancelar() {
        estado.cancelar(this);
    }

    public void cambiarEstado(EstadoDeReserva nuevoEstado) {
        EstadoDeReserva anterior = this.estado;
        this.estado = nuevoEstado;
        notificacion.actualizarEstados(this, anterior, nuevoEstado);
    }

    public void setMetodoTraslado(MetodoTraslado metodoTraslado) {
        this.metodoTraslado = metodoTraslado;
    }

    public void setMetodoPago(MedioDePago medioDePago) {
        this.medioDePago = medioDePago;
    }

    void agregarItemDirecto(Catalogo item) {
        items.add(item);
    }

    void quitarItemDirecto(Catalogo item) {
        items.remove(item);
    }

    public void bloquearCupos() {
        List<Catalogo> bloqueados = new ArrayList<>();
        try {
            for (Catalogo item : items) {
                item.decrementarCupo(fechaInicio, fechaFin, cantidadDePasajeros);
                bloqueados.add(item);
            }
        } catch (ExcepcionDominio e) {
            bloqueados.forEach(item -> item.incrementarCupo(fechaInicio, fechaFin, cantidadDePasajeros));
            throw e;
        }
    }

    public void liberarCupos() {
        items.forEach(item -> item.incrementarCupo(fechaInicio, fechaFin, cantidadDePasajeros));
    }

    public void reembolsarServiciosYTasas() {
        registrarReembolso(this.valorServicios() + this.costoTasas());
    }

    public void reembolsarServicios() {
        registrarReembolso(this.valorServicios());
    }

    private double costoTasas() {
        return metodoTraslado == null ? 0 : metodoTraslado.calcularCosto(this);
    }

    private void registrarReembolso(double monto) {
        vouchersDeReembolso.add(new VoucherReembolso(LocalDate.now(), this, monto));
    }

    public List<VoucherReembolso> getVouchersDeReembolso() {
        return new ArrayList<>(vouchersDeReembolso);
    }

    public void procesarPago() {
        if (this.medioDePago == null) {
            throw new ExcepcionDominio("No se puede procesar el pago sin un metodo de pago asignado");
        }
        this.medioDePago.setMonto(this.valorTotal());
        this.medioDePago.procesoDePago();
    }
}
