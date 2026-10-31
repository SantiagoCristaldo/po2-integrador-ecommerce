package unq.notificaciones;

import java.util.ArrayList;
import java.util.List;

import unq.reserva.EstadoDeReserva;
import unq.reserva.Reserva;

public class GeneradorComprobantes implements ObservadorReserva {
    private List<FacturaFiscal> facturas = new ArrayList<>();
    private List<VoucherViaje> vouchers = new ArrayList<>();


    @Override
    public void actualizar(Reserva reserva, EstadoDeReserva estadoAnterior, EstadoDeReserva estadoActual) {
        if (estadoAnterior.debeGenerarVoucher()){
            this.generarFactura(reserva);
            this.generarVoucher(reserva);
        }
    }

    private void generarFactura(Reserva reserva) {
        facturas.add(new FacturaFiscal(facturas.size() + 1, reserva.valorTotal(), reserva));
    }


    private void generarVoucher(Reserva reserva) {
        vouchers.add(new VoucherViaje(vouchers.size() + 1, reserva));
    }

    public List<FacturaFiscal> getFacturas() {
        return new ArrayList<>(facturas);
    }

    public List<VoucherViaje> getVouchers() {
        return new ArrayList<>(vouchers);
    }
}
