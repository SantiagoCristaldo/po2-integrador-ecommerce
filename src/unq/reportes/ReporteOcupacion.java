package unq.reportes;

import java.util.ArrayList;
import java.util.List;

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

        List<ServicioIndividual> yaContados = new ArrayList<>();
        for (Catalogo item : catalogo) {
            for (ServicioIndividual servicio : item.servicios()) {
                if (!yaContados.contains(servicio)) {
                    buscarFila(servicio.getCategoria()).sumarCuposTotales(servicio.getCupoTotal());
                    yaContados.add(servicio);
                }
            }
        }

        for (Reserva reserva : reservas) {
            if (reserva.cuentaComoVenta() && periodo.contiene(reserva.getFecha())) {
                for (Catalogo item : reserva.getItems()) {
                    for (ServicioIndividual servicio : item.servicios()) {
                        buscarFila(servicio.getCategoria()).sumarCuposReservados(reserva.cantidadDePasajeros());
                    }
                }
            }
        }
    }
    private FilaOcupacion buscarFila(Categoria categoria) {
        for (FilaOcupacion fila : filas) {
            if (fila.getCategoria().equals(categoria)) {
                return fila;
            }
        }
        FilaOcupacion nueva = new FilaOcupacion(categoria);
        filas.add(nueva);
        return nueva;
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
