package unq.notificaciones;

import unq.reserva.Reserva;

public class ClubViajeros implements ObservadorReserva {
    private MailSender mailSender;
    private float porcentajeCupon = 5;

    public ClubViajeros(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void actualizar(EventoCambioEstado e) {
        if (e != null && e.getEstadoActual() != null
                && e.getEstadoActual().getClass().getSimpleName().equalsIgnoreCase("FINALIZADA")) {
            Reserva reserva = obtenerReserva(e);
            CuponDescuento cupon = generarCupon(reserva);
            if (cupon != null) {
                mailSender.enviarMail(null, "Cupón de descuento", "Tu cupón es: " + cupon.getCodigo(), cupon);
            }
        }
    }

    private CuponDescuento generarCupon(Reserva r) {
        if (r == null) {
            return new CuponDescuento("CUPON-DEFAULT", porcentajeCupon, java.time.LocalDate.now().plusMonths(1));
        }
        return new CuponDescuento("CUPON-" + System.currentTimeMillis(), porcentajeCupon, java.time.LocalDate.now().plusMonths(1));
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
