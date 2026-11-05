package unq.reportes;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

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

    /** El encabezado y una línea por fila; cada línea termina en salto de línea. */
    static <F> String conFilas(String encabezado, List<F> filas, Function<F, String> linea) {
        StringBuilder texto = new StringBuilder(encabezado);
        for (F fila : filas) {
            texto.append(linea.apply(fila)).append("\n");
        }
        return texto.toString();
    }
}
