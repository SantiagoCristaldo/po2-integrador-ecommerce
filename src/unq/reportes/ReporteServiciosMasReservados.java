package unq.reportes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import unq.catalogo.Catalogo;
import unq.reserva.Reserva;

public class ReporteServiciosMasReservados implements Reporte {
    private Periodo periodo;
    private List<FilaVentas> filas = new ArrayList<>();

    public ReporteServiciosMasReservados(Periodo periodo) {
        this.periodo = periodo;
    }

    @Override
    public void generar(List<Reserva> reservas, List<Catalogo> catalogo) {
        filas = new ArrayList<>();
        catalogo.forEach(this::filaDe);
        reservas.stream()
                .filter(reserva -> reserva.cuentaComoVenta() && periodo.contiene(reserva.getFecha()))
                .forEach(reserva -> registrarVentas(reserva.getItems(), 1.0));
        filas.sort(Comparator.comparingInt(FilaVentas::getUnidadesVendidas).reversed());
    }

    // Cada componente de un paquete hereda el descuento que ya trae la jerarquía.
    private void registrarVentas(List<Catalogo> items, double factorAcumulado) {
        for (Catalogo item : items) {
            filaDe(item).registrarVenta(item.precioFinal() * factorAcumulado);
            registrarVentas(item.componentes(), factorAcumulado * item.factorDeDescuento());
        }
    }

    // La fila se busca por identidad: dos ítems distintos con los mismos datos son ventas distintas.
    private FilaVentas filaDe(Catalogo item) {
        return filas.stream()
                .filter(fila -> fila.getItem() == item)
                .findFirst()
                .orElseGet(() -> crearFila(item));
    }

    private FilaVentas crearFila(Catalogo item) {
        FilaVentas fila = new FilaVentas(item);
        filas.add(fila);
        return fila;
    }

    public List<FilaVentas> getFilas() {
        return filas;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    @Override
    public String aceptar(ExportadorReporte exportador) {
        return exportador.visitar(this);
    }
}
