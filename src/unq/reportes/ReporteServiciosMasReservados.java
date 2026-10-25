package unq.reportes;

import java.util.ArrayList;
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

        for (Catalogo item : catalogo) {
            buscarFila(item);
        }

        for (Reserva reserva : reservas) {
            if (reserva.cuentaComoVenta() && periodo.contiene(reserva.getFecha())) {
                for (Catalogo item : reserva.getItems()) {
                    registrarVenta(item, 1.0);
                }
            }
        }

        filas.sort((a, b) -> b.getUnidadesVendidas() - a.getUnidadesVendidas());
    }

    private void registrarVenta(Catalogo item, double descuentoAcumulado) {
        buscarFila(item).registrarVenta(item.precioFinal() * descuentoAcumulado);

        for (Catalogo componente : item.componentes()) {
            registrarVenta(componente, descuentoAcumulado * item.factorDeDescuento());
        }
    }

    private FilaVentas buscarFila(Catalogo item) {
        for (FilaVentas fila : filas) {
            if (fila.getItem() == item) {
                return fila;
            }
        }
        FilaVentas nueva = new FilaVentas(item);
        filas.add(nueva);
        return nueva;
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
