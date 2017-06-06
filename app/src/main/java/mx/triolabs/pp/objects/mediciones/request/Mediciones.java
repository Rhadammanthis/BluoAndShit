package mx.triolabs.pp.objects.mediciones.request;

import com.google.gson.Gson;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 12/1/16.
 */

public class Mediciones extends BaseObject<Mediciones> {

    private String Glucosa;
    private String Presion;
    private String Oxigeno;

    public void setOxigeno(String oxigeno) {
        Oxigeno = oxigeno;
    }

    public String getGlucosa() {
        return Glucosa;
    }

    public void setGlucosa(String glucosa) {
        Glucosa = glucosa;
    }

    public String getPresion() {
        return Presion;
    }

    public void setPresion(String presion) {
        Presion = presion;
    }

    public String getOxigeno() {
        return Oxigeno;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public Mediciones compose(String json) {
        return new Gson().fromJson(json, Mediciones.class);
    }
}
