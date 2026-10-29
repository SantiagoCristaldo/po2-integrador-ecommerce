package unq.traslado;

public class Direccion {
    
    private String calle;
    private int numero;
    private String ciudad;
    private String codigoPostal;
    private int cuposGuias;

    public Direccion(String calle, int numero, String ciudad, String codigoPostal, int cuposGuias)  {

        this.calle = calle;
        this.numero = numero;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.cuposGuias = cuposGuias;
    }

    public int getCuposDisponibles(){
        return this.cuposGuias;
    }

    public void asignarGuia(){
        this.cuposGuias--;
    }

    public String getCalle(){
        return calle;
    }

    public String getCiudad(){
        return ciudad;
    }

    public String getCodigoPostal(){
        return codigoPostal;
    }

    public int getNumero(){
        return numero;
    }
}


