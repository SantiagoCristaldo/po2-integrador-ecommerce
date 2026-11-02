package unq.reportes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;
import unq.reserva.Reserva;

public class ReporteOcupacion implements Reporte {
    private Periodo periodo;
    private List<FilaOcupacion> filas = new ArrayList<>();

    public ReporteOcupacion(Periodo periodo) {
        this.periodo = periodo;
    }

    @Override
    public void generar(List<Reserva> reservas, List<Catalogo> catalogo) {
        filas = new ArrayList<>();
        sumarCuposTotales(catalogo);
        sumarCuposReservados(reservas);
    }

    private void sumarCuposTotales(List<Catalogo> catalogo) {
        for (ServicioIndividual servicio : serviciosSinRepetidos(catalogo)) {
            filaDe(servicio.getCategoria()).sumarCuposTotales(servicio.getCupoTotal());
        }
    }

    private void sumarCuposReservados(List<Reserva> reservas) {
        for (Reserva reserva : ventasDelPeriodo(reservas)) {
            sumarCuposReservadosDe(reserva);
        }
    }

    private void sumarCuposReservadosDe(Reserva reserva) {
        for (ServicioIndividual servicio : serviciosDe(reserva.getItems())) {
            filaDe(servicio.getCategoria()).sumarCuposReservados(reserva.cantidadDePasajeros());
        }
    }

    private List<Reserva> ventasDelPeriodo(List<Reserva> reservas) {
        return reservas.stream()
                .filter(r -> r.cuentaComoVenta() && periodo.contiene(r.getFecha()))
                .collect(Collectors.toList());
    }

    private List<ServicioIndividual> serviciosSinRepetidos(List<Catalogo> items) {
        return serviciosDe(items).stream().distinct().collect(Collectors.toList());
    }

    private List<ServicioIndividual> serviciosDe(List<Catalogo> items) {
        List<ServicioIndividual> servicios = new ArrayList<>();
        for (Catalogo item : items) {
            servicios.addAll(item.servicios());
        }
        return servicios;
    }

    // La fila se busca por equals: dos categorías con el mismo nombre son la misma categoría.
    private FilaOcupacion filaDe(Categoria categoria) {
        return filas.stream()
                .filter(fila -> fila.getCategoria().equals(categoria))
                .findFirst()
                .orElseGet(() -> crearFila(categoria));
    }

    private FilaOcupacion crearFila(Categoria categoria) {
        FilaOcupacion fila = new FilaOcupacion(categoria);
        filas.add(fila);
        return fila;
    }

    public List<FilaOcupacion> getFilas() {
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