package unq.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import unq.reserva.Reserva;

class ComprobantesTest {

    private final Reserva reserva = mock(Reserva.class);

    @Test
    void facturaFiscal_guardaNumeroTotalYReserva() {
        FacturaFiscal factura = new FacturaFiscal(7, 35000.0, reserva);

        assertEquals(7, factura.getNumero());
        assertEquals(35000.0, factura.getTotal(), 0.001);
        assertSame(reserva, factura.getReserva());
    }

    @Test
    void voucherViaje_guardaCodigoYReserva() {
        VoucherViaje voucher = new VoucherViaje(3, reserva);

        assertEquals(3, voucher.getCodigo());
        assertSame(reserva, voucher.getReserva());
    }

    @Test
    void cuponDescuento_guardaCodigoYPorcentaje() {
        CuponDescuento cupon = new CuponDescuento("CuponNro1", 5f);

        assertEquals("CuponNro1", cupon.getCodigo());
        assertEquals(5f, cupon.getPorcentaje(), 0.001f);
    }
}
