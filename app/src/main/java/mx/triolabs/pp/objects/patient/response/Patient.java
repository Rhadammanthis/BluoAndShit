package mx.triolabs.pp.objects.patient.response;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 11/30/16.
 */

public class Patient extends BaseObject<Patient> {

    private String Nombre;
    private String Peso;
    private String Estatura;
    private String Genero;
    private List<Padecimiento> Padecimientos = new ArrayList<Padecimiento>();

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getPeso() {
        return Peso;
    }

    public void setPeso(String peso) {
        this.Peso = peso;
    }

    public String getEstatura() {
        return Estatura;
    }

    public void setEstatura(String estatura) {
        this.Estatura = estatura;
    }

    public String getGenero() {
        return Genero;
    }

    public void setGenero(String genero) {
        this.Genero = genero;
    }

    public List<Padecimiento> getPadecimientos() {
        return Padecimientos;
    }

    public void setPadecimientos(List<Padecimiento> padecimientos) {
        this.Padecimientos = padecimientos;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public Patient compose(String json) {
        return new Gson().fromJson(json, Patient.class);
    }
}
