package unq.notificaciones;

import java.util.ArrayList;
import java.util.List;

public class RegistroTransacciones {
    private List<String> codigos = new ArrayList<String>();
    private List<Object> comprobantes = new ArrayList<Object>();

    public void registrar(String codigo) {
        codigos.add(codigo);
    }

    public void registrar(Object c) {
        comprobantes.add(c);
    }

    public List<String> getCodigos() {
        return codigos;
    }

    public List<Object> getComprobantes() {
        return comprobantes;
    }
}
