package unq.notificaciones;

import unq.reserva.EstadoDeReserva;
import unq.reserva.Reserva;

public class GeneradorComprobantes implements ObservadorReserva {
    private int contadorFacturas;
    private int contadorVouchers;


    @Override
    public void actualizar(Reserva reserva, EstadoDeReserva estadoAnterior, EstadoDeReserva estadoActual) {
        if (estadoAnterior.debeGenerarVoucher()){
            this.generarFactura(reserva);
            this.generarVoucher(reserva);
        }
    }

    private FacturaFiscal generarFactura(Reserva reserva) {
        contadorFacturas++;
        return new FacturaFiscal(contadorFacturas, reserva.valorTotal(), reserva);
    }


    private VoucherViaje generarVoucher(Reserva reserva) {
        contadorVouchers++;
        return new VoucherViaje(contadorVouchers, reserva);

    }
}
