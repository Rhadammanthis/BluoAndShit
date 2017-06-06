package mx.triolabs.pp.objects.patient.response;

/**
 * Created by hugomedina on 11/30/16.
 */

public class Medicamento {

    private String Nombre;
    private String Dosis;

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getDosis() {
        return Dosis;
    }

    public void setDosis(String dosis) {
        this.Dosis = dosis;
    }

}
