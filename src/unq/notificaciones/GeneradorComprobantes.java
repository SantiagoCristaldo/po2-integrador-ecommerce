package unq.notificaciones;

import java.time.LocalDate;

import unq.reserva.Reserva;

public class GeneradorComprobantes implements ObservadorReserva {
    private RegistroTransacciones registro;

    public GeneradorComprobantes(RegistroTransacciones registro) {
        this.registro = registro;
    }

    @Override
    public void actualizar(EventoCambioEstado e) {
        if (e == null || e.getEstadoActual() == null) {
            return;
        }
        String estado = e.getEstadoActual().getClass().getSimpleName().toUpperCase();
        Reserva reserva = obtenerReserva(e);
        if ("FINALIZADA".equals(estado)) {
            FacturaFiscal factura = generarFactura(reserva);
            if (factura != null && registro != null) {
                registro.registrar(factura);
            }
        }
        if ("CONFIRMADA".equals(estado) || "FINALIZADA".equals(estado)) {
            VoucherViaje voucher = generarVoucher(reserva);
            if (voucher != null && registro != null) {
                registro.registrar(voucher);
            }
        }
    }

    private FacturaFiscal generarFactura(Reserva r) {
        float total = 0f;
        if (r != null) {
            try {
                total = (float) r.valorTotal();
            } catch (Exception ex) {
                total = 0f;
            }
        }
        return new FacturaFiscal("FAC-" + System.currentTimeMillis(), LocalDate.now(), total, r);
    }

    private VoucherViaje generarVoucher(Reserva r) {
        return new VoucherViaje("VOU-" + System.currentTimeMillis(), LocalDate.now(), r);
    }

    private Reserva obtenerReserva(EventoCambioEstado e) {
        try {
            java.lang.reflect.Field f = EventoCambioEstado.class.getDeclaredField("reserva");
            f.setAccessible(true);
            return (Reserva) f.get(e);
        } catch (Exception ex) {
            return null;
        }
    }
}
