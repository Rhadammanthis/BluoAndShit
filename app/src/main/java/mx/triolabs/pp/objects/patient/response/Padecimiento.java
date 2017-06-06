package mx.triolabs.pp.objects.patient.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugomedina on 11/30/16.
 */

public class Padecimiento {

    private String Nombre;
    private String Rango;
    private List<Medicamento> Medicamentos = new ArrayList<Medicamento>();

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getRango() {
        return Rango;
    }

    public void setRango(String rango) {
        this.Rango = rango;
    }

    public List<Medicamento> getMedicamentos() {
        return Medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.Medicamentos = medicamentos;
    }

}