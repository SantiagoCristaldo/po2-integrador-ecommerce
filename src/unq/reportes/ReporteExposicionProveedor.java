package unq.reportes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.*;
import unq.catalogo.Catalogo;
import unq.catalogo.ServicioIndividual;
import unq.reserva.Reserva;

public class ReporteExposicionProveedor implements Reporte {
    private static final double PORCENTAJE_TOTAL = 100.0;

    private List<FilaExposicion> filas = new ArrayList<>();

    @Override
    public void generar(List<Reserva> reservas, List<Catalogo> catalogo) {
        // El reporte mide qué parte del catálogo depende de cada operador: las reservas no
        // cambian el resultado. El parámetro queda porque lo impone la interfaz Reporte.
        Map<String, Integer> cantidadPorOperador = contarItemsPorOperador(catalogo);

        filas = new ArrayList<>();
        for (String operador : cantidadPorOperador.keySet()) {
            filas.add(filaDelOperador(operador, cantidadPorOperador, catalogo.size()));
        }

        filas.sort(Comparator.comparingInt(FilaExposicion::getCantidadItems).reversed());
    }

    private Map<String, Integer> contarItemsPorOperador(List<Catalogo> catalogo) {
        return catalogo.stream()
                .flatMap(item -> operadoresDelItem(item).stream())
                .collect(Collectors.groupingBy(operador -> operador,
                                            Collectors.summingInt(operador -> 1)));
    }

    private List<String> operadoresDelItem(Catalogo item) {
        return item.servicios().stream()
                .map(ServicioIndividual::getOperador)
                .distinct()
                .collect(Collectors.toList());
    }

    private FilaExposicion filaDelOperador(String operador,
            Map<String, Integer> cantidadPorOperador, int tamañoDelCatalogo) {
        int cantidad = cantidadPorOperador.get(operador);
        return new FilaExposicion(operador, cantidad,
                porcentajeDelCatalogo(cantidad, tamañoDelCatalogo));
    }

    private double porcentajeDelCatalogo(int cantidad, int tamañoDelCatalogo) {
        return cantidad * PORCENTAJE_TOTAL / tamañoDelCatalogo;
    }

    public List<FilaExposicion> getFilas() {
        return filas;
    }

    @Override
    public String aceptar(ExportadorReporte exportador) {
        return exportador.visitar(this);
    }
}
