package unq.reserva;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoucherReembolsoTest {

    @Test
    void guardaFechaYReservaAsociada() {
        Reserva reserva = mock(Reserva.class);   // DUMMY
        LocalDate hoy = LocalDate.of(2026, 10, 27);

        VoucherReembolso voucher = new VoucherReembolso(hoy, reserva, 5000.0);

        assertEquals(hoy, voucher.fechaEmision);
        assertSame(reserva, voucher.reservaAsociada);
        verifyNoInteractions(reserva);
    }

    @Test
    void montoDevuelveElMontoAReembolsar() {
        VoucherReembolso voucher = new VoucherReembolso(LocalDate.now(), mock(Reserva.class), 5000.0);
        assertEquals(5000.0, voucher.monto());
    }
}
