package unq.catalogo;

/**
 * Tipo de un atributo dinámico. El valor llega como texto y cada tipo decide si es válido
 * intentando convertirlo (valueOf) al tipo que representa.
 */
public interface TipoValor {
    boolean esValido(String valor);
}
