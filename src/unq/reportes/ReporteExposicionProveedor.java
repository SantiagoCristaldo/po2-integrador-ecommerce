package unq.reportes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unq.catalogo.Catalogo;
import unq.catalogo.ServicioIndividual;
import unq.reserva.Reserva;

public class ReporteExposicionProveedor implements Reporte {
    private List<FilaExposicon> filas = new ArrayList<>();

    @Override
    public void generar(List<Reserva> reservas, List<Catalogo> catalogo) {
        Map<String, Integer> cantidadPorOperador = new HashMap<>();

        for (Catalogo item : catalogo) {
            List<String> operadoresDelItem = new ArrayList<>();
            for (ServicioIndividual servicio : item.servicios()) {
                if (!operadoresDelItem.contains(servicio.getOperador())) {
                    operadoresDelItem.add(servicio.getOperador());
                }
            }

            for (String operador : operadoresDelItem) {
                int cantidad = cantidadPorOperador.getOrDefault(operador, 0);
                cantidadPorOperador.put(operador, cantidad + 1);
            }
        }

        filas = new ArrayList<>();
        for (String operador : cantidadPorOperador.keySet()) {
            int cantidad = cantidadPorOperador.get(operador);
            double porcentaje = cantidad * 100.0 / catalogo.size();
            filas.add(new FilaExposicon(operador, cantidad, porcentaje));
        }

        filas.sort((a, b) -> b.getCantidadItems() - a.getCantidadItems());
    }

    public List<FilaExposicon> getFilas() {
        return filas;
    }

    @Override
    public String aceptar(ExportadorReporte exportador) {
        return exportador.visitar(this);
    }
}
