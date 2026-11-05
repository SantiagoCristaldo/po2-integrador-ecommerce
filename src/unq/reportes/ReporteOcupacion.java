package unq.reportes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import unq.catalogo.Catalogo;
import unq.catalogo.Categoria;
import unq.catalogo.ServicioIndividual;
import unq.reserva.Reserva;

/**
 * Cupos por categoría en las fechas del período, leídos del cronograma de cada servicio.
 *
 * La unidad es el cupo por día: un hotel de 10 cupos tiene 310 cupos totales en un período de 31 días,
 * y una reserva de 2 pasajeros por 3 días ocupa 6.
 */
public class ReporteOcupacion implements Reporte {
    private Periodo periodo;
    private List<FilaOcupacion> filas = new ArrayList<>();

    public ReporteOcupacion(Periodo periodo) {
        this.periodo = periodo;
    }

    /** Las reservas no se usan: lo que ocupan ya está registrado en los cronogramas. */
    @Override
    public void generar(List<Reserva> reservas, List<Catalogo> catalogo) {
        Map<Categoria, FilaOcupacion> filasPorCategoria = new LinkedHashMap<>();
        catalogo.stream()
                .flatMap(item -> item.servicios().stream())
                .distinct()
                .forEach(servicio -> filasPorCategoria
                        .computeIfAbsent(servicio.getCategoria(), FilaOcupacion::new)
                        .sumar(cuposTotales(servicio), cuposReservados(servicio)));
        filas = new ArrayList<>(filasPorCategoria.values());
    }

    private int cuposTotales(ServicioIndividual servicio) {
        return servicio.cuposTotalesEntre(periodo.getDesde(), periodo.getHasta());
    }

    private int cuposReservados(ServicioIndividual servicio) {
        return servicio.cuposReservadosEntre(periodo.getDesde(), periodo.getHasta());
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
