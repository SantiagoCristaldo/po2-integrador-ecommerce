package unq.reportes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import unq.catalogo.Catalogo;
import unq.catalogo.ServicioIndividual;
import unq.reserva.Reserva;

public class ReporteExposicionProveedor implements Reporte {
    private List<FilaExposicion> filas = new ArrayList<>();

    /** Las reservas no se usan: el reporte mide qué parte del catálogo depende de cada operador. */
    @Override
    public void generar(List<Reserva> reservas, List<Catalogo> catalogo) {
        Map<String, Long> itemsPorOperador = catalogo.stream()
                .flatMap(item -> item.servicios().stream().map(ServicioIndividual::getOperador).distinct())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        filas = new ArrayList<>();
        itemsPorOperador.forEach((operador, cantidad) -> filas.add(
                new FilaExposicion(operador, cantidad.intValue(), cantidad * 100.0 / catalogo.size())));
        filas.sort(Comparator.comparingInt(FilaExposicion::getCantidadItems).reversed());
    }

    public List<FilaExposicion> getFilas() {
        return filas;
    }

    @Override
    public String aceptar(ExportadorReporte exportador) {
        return exportador.visitar(this);
    }
}
