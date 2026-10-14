package unq.reserva;

import unq.catalogo.Catalogo;

public class EnChecking implements EstadoDeReserva {
    @Override
    public void agregarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("No se pueden agregar items durante el check-in");
    }

    @Override
    public void quitarItem(Reserva reserva, Catalogo item) {
        throw new ExcepcionPorInvalido("No se pueden quitar items durante el check-in");
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new ExcepcionPorInvalido("La reserva ya esta confirmada");
    }

    @Override
    public void hacerCheckin(Reserva reserva) {
        throw new ExcepcionPorInvalido("El check-in ya fue realizado");
    }

    @Override
    public void iniciar(Reserva reserva) {
        reserva.cambiarEstado(new EnCurso());
    }

    @Override
    public void finalizar(Reserva reserva) {
        throw new ExcepcionPorInvalido("No se puede finalizar una reserva en check-in");
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.liberarCupos();
        reserva.reembolsar();
        reserva.cambiarEstado(new Cancelada());
    }
}
