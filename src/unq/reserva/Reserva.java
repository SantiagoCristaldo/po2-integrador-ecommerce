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
    private EstadoDeReserva estado;
    private List<Catalogo> items = new ArrayList<>();
    private int cantidadDePasajeros;
    private MetodoTraslado metodoTraslado;
    private Notificador notificacion = new Notificador();
    private LocalDate fecha;
    private MedioDePago medioDePago;


    public Reserva(String email, Direccion destino, int cantidadDePasajeros) {
        this.estado = new SolicitudPendiente();
        this.cantidadDePasajeros = cantidadDePasajeros;
        this.fecha = LocalDate.now();
    }

    public LocalDate getFecha(){
        return fecha;
    }
    public List<Catalogo> getItems(){
        return new ArrayList<>(items);
    }

    public  boolean cuentaComoVenta(){
        return estado.cuentaComoVenta();
    }
    public EstadoDeReserva getEstadoActual(){
        return this.estado;
    }

    public double getCostoEnvio() {
        if (this.metodoTraslado == null) throw new ExcepcionDominio("Método de envío no seleccionado");
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
        notificacion.actualizarEstados(this, this.estado, nuevoEstado);
        this.estado = nuevoEstado;
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
    }

    public void liberarCupos() {
    }

    public void reembolsar() {
        double montoAReembolsar = this.valorServicios() + metodoTraslado.calcularCosto(this); 
        new VoucherReembolso(this.fecha, this, montoAReembolsar); 
    }

    public void procesarPago() {
        if(this.medioDePago == null){
            throw new ExcepcionDominio("No se puede procesar el pago sin un metodo de pago asignado");  
        }
        this.medioDePago.setMonto(this.valorTotal());
        this.medioDePago.procesoDePago();
    }
}
