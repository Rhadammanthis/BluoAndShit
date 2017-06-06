package mx.triolabs.pp.objects.questions;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 12/8/16.
 */

public class TestResult extends BaseObject<TestResult>{

    public List<Respuesta> getRespuestas() {
        return Respuestas;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        Respuestas = respuestas;
    }

    private List<Respuesta> Respuestas = new ArrayList<>();

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public TestResult compose(String json) {
        return new Gson().fromJson(json, TestResult.class);
    }
}
