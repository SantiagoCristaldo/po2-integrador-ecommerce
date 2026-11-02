package unq.reportes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import unq.catalogo.Catalogo;
import unq.reserva.Reserva;

public class ReporteServiciosMasReservados implements Reporte {
    private static final double SIN_DESCUENTO = 1.0;

    private Periodo periodo;
    private List<FilaVentas> filas = new ArrayList<>();
    private Map<Catalogo, FilaVentas> filasPorItem = new IdentityHashMap<>();

    public ReporteServiciosMasReservados(Periodo periodo) {
        this.periodo = periodo;
    }

    @Override
    public void generar(List<Reserva> reservas, List<Catalogo> catalogo) {
        filas = new ArrayList<>();
        filasPorItem = new IdentityHashMap<>();

        crearFilasDelCatalogo(catalogo);
        registrarVentasDelPeriodo(reservas);

        filas.sort(Comparator.comparingInt(FilaVentas::getUnidadesVendidas).reversed());
    }

    private void crearFilasDelCatalogo(List<Catalogo> catalogo) {
        for (Catalogo item : catalogo) {
            buscarFila(item);
        }
    }

    private void registrarVentasDelPeriodo(List<Reserva> reservas) {
        for (Reserva reserva : reservas) {
            if (esVentaDelPeriodo(reserva)) {
                registrarItems(reserva.getItems(), SIN_DESCUENTO);
            }
        }
    }

    private boolean esVentaDelPeriodo(Reserva reserva) {
        return reserva.cuentaComoVenta() && periodo.contiene(reserva.getFecha());
    }

    private void registrarItems(List<Catalogo> items, double factorAcumulado) {
        for (Catalogo item : items) {
            registrarVenta(item, factorAcumulado);
        }
    }

    private void registrarVenta(Catalogo item, double factorAcumulado) {
        buscarFila(item).registrarVenta(item.precioFinal() * factorAcumulado);

        // Cada componente de un paquete hereda el descuento que ya trae la jerarquía.
        registrarItems(item.componentes(), factorAcumulado * item.factorDeDescuento());
    }

    // La fila se busca por identidad: dos ítems distintos con los mismos datos son ventas distintas.
    private FilaVentas buscarFila(Catalogo item) {
        FilaVentas fila = filasPorItem.get(item);
        if (fila == null) {
            fila = new FilaVentas(item);
            filasPorItem.put(item, fila);
            filas.add(fila);
        }
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
