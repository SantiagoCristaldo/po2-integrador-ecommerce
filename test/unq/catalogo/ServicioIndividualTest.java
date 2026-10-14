package unq.catalogo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unq.excepciones.ExcepcionDominio;

class ServicioIndividualTest {

    private ServicioIndividual servicio;

    @BeforeEach
    void setUp() {
        servicio = servicioConDatosFijos();
    }

    private ServicioIndividual servicioConDatosFijos() {
        return new ServicioIndividual("HOT-001", "Hotel Central", "Un hotel céntrico",
                "Operador SA", new Categoria("Hotel"), 15000.0);
    }

    private ServicioIndividual servicioSin(String codigo, String nombre) {
        return new ServicioIndividual(codigo, nombre, "Una descripción",
                "Operador SA", new Categoria("Hotel"), 15000.0);
    }

    @Test
    void conDatosFijos_esValido() {
        assertDoesNotThrow(servicio::validar);
    }

    @Test
    void sinCodigo_noEsValido() {
        assertThrows(ExcepcionDominio.class, () -> servicioSin(null, "Hotel Central").validar());
    }

    @Test
    void sinNombre_noEsValido() {
        assertThrows(ExcepcionDominio.class, () -> servicioSin("HOT-001", null).validar());
    }

    @Test
    void sinOperador_noEsValido() {
        ServicioIndividual sinOperador = new ServicioIndividual("HOT-001", "Hotel Central",
                "Una descripción", null, new Categoria("Hotel"), 15000.0);
        assertThrows(ExcepcionDominio.class, sinOperador::validar);
    }

    @Test
    void sinCategoria_noEsValido() {
        ServicioIndividual sinCategoria = new ServicioIndividual("HOT-001", "Hotel Central",
                "Una descripción", "Operador SA", null, 15000.0);
        assertThrows(ExcepcionDominio.class, sinCategoria::validar);
    }

    // ---------- atributos dinámicos ----------

    @Test
    void conAtributoDinamicoValido_esValido() {
        AtributoDinamico capacidad = new AtributoDinamico("CapacidadMaxima", true, new Numero());
        capacidad.setValor(50);
        servicio.agregarAtributoDinamico(capacidad);

        assertDoesNotThrow(servicio::validar);
    }

    @Test
    void conAtributoDinamicoDeTipoInconsistente_noEsValido() {
        AtributoDinamico capacidad = new AtributoDinamico("CapacidadMaxima", true, new Numero());
        capacidad.setValor("cincuenta");
        servicio.agregarAtributoDinamico(capacidad);

        assertThrows(ExcepcionDominio.class, servicio::validar);
    }

    @Test
    void atributoObligatorioSinValor_noEsValido() {
        AtributoDinamico capacidad = new AtributoDinamico("CapacidadMaxima", true, new Numero());
        servicio.agregarAtributoDinamico(capacidad);

        assertThrows(ExcepcionDominio.class, servicio::validar);
    }

    @Test
    void atributoOpcionalSinValor_esValido() {
        AtributoDinamico observaciones = new AtributoDinamico("Observaciones", false, new Texto());
        servicio.agregarAtributoDinamico(observaciones);

        assertDoesNotThrow(servicio::validar);
    }

    @Test
    void agregarDosAtributosConElMismoNombre_lanzaExcepcion() {
        servicio.agregarAtributoDinamico(new AtributoDinamico("CapacidadMaxima", true, new Numero()));
        AtributoDinamico otraCapacidad = new AtributoDinamico("CapacidadMaxima", false, new Numero());

        assertThrows(ExcepcionDominio.class, () -> servicio.agregarAtributoDinamico(otraCapacidad));
    }

    @Test
    void setValorAtributo_seteaElValorDelAtributoCorrecto() {
        AtributoDinamico capacidad = new AtributoDinamico("CapacidadMaxima", true, new Numero());
        servicio.agregarAtributoDinamico(capacidad);

        servicio.setValorAtributo("CapacidadMaxima", 50);

        assertDoesNotThrow(servicio::validar);
    }

    @Test
    void setValorAtributo_conNombreInexistente_lanzaExcepcion() {
        assertThrows(ExcepcionDominio.class, () -> servicio.setValorAtributo("RatingEstrellas", 5));
    }

    // ---------- descuento promocional ----------

    @Test
    void sinDescuentoPromocional_precioFinalIgualAlBase() {
        assertEquals(15000.0, servicio.precioFinal());
    }

    @Test
    void conDescuentoPromocional_precioFinalEsElBaseConDescuento() {
        servicio.aplicarDescuentoPromocional(10);

        assertEquals(13500.0, servicio.precioFinal());
    }

    @Test
    void descuentoPromocionalFueraDeRango_lanzaExcepcion() {
        assertThrows(ExcepcionDominio.class, () -> servicio.aplicarDescuentoPromocional(150));
    }

    // ---------- consultas ----------

    @Test
    void contieneEnNombre_ignoraMayusculas() {
        assertTrue(servicio.contieneEnNombre("central"));
        assertFalse(servicio.contieneEnNombre("Hostel"));
    }

    @Test
    void exponeNombreDescripcionYPrecioBase() {
        assertEquals("Hotel Central", servicio.nombre());
        assertEquals("Un hotel céntrico", servicio.descripcion());
        assertEquals(15000.0, servicio.precioBase());
    }

    @Test
    void esDeCategoria_comparaPorValor() {
        assertTrue(servicio.esDeCategoria(new Categoria("Hotel")));
        assertFalse(servicio.esDeCategoria(new Categoria("Vuelo")));
    }

    @Test
    void codigoEnBlanco_noEsValido() {
        assertThrows(ExcepcionDominio.class, () -> servicioSin("   ", "Hotel Central").validar());
    }
}
