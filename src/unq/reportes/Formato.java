package unq.reportes;

import java.util.List;
import java.util.Locale;

/** Piezas de armado de texto que comparten los tres exportadores. */
final class Formato {

    private Formato() {
    }

    /** Número con dos decimales, sin depender del idioma de la máquina. */
    static String numero(double valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }

    /** Título con el rango de fechas del reporte. */
    static String encabezadoConPeriodo(String titulo, Periodo periodo) {
        return titulo + " (" + periodo.getDesde() + " a " + periodo.getHasta() + ")\n";
    }

    /** Encabezado, una línea por fila y un pie; cada línea termina en salto de línea. */
    static String conFilas(String encabezado, String pie, List<String> filas) {
        StringBuilder texto = new StringBuilder(encabezado);
        for (String fila : filas) {
            texto.append(fila).append("\n");
        }
        return texto.append(pie).toString();
    }
}
