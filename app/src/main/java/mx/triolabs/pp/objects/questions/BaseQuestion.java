package mx.triolabs.pp.objects.questions;

import java.util.List;

/**
 * Created by hugomedina on 12/8/16.
 */

public class BaseQuestion {

    private String FechaUltima;
    private List<Pregunta> Preguntas = null;

    public String getFechaUltima() {
        return FechaUltima;
    }

    public void setFechaUltima(String fechaUltima) {
        FechaUltima = fechaUltima;
    }

    public List<Pregunta> getPreguntas() {
        return Preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        Preguntas = preguntas;
    }
}
