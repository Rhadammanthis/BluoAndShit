package mx.triolabs.pp.objects.questions;

/**
 * Created by hugomedina on 12/9/16.
 */

public class Pregunta {

    private String Pregunta;
    private String NumPregunta;
    private boolean Si;

    public boolean isSi() {
        return Si;
    }

    public void setSi(boolean si) {
        Si = si;
    }

    public String getPregunta() {
        return Pregunta;
    }

    public void setPregunta(String pregunta) {
        Pregunta = pregunta;
    }

    public String getNumPregunta() {
        return NumPregunta;
    }

    public void setNumPregunta(String numPregunta) {
        NumPregunta = numPregunta;
    }


}
