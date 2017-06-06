package mx.triolabs.pp.objects.questions;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import mx.triolabs.pp.objects.BaseObject;
import mx.triolabs.pp.objects.misc.Tips;

/**
 * Created by hugomedina on 12/8/16.
 */

public class Questions extends BaseObject<Questions>{

    public enum Types {
        EXERCISE("Ejercicio"), NUTRITION("Alimentacion"), PROFILING("Perfilamiento"), PREVENTION("Prevencion");

        private final String text;

        /**
         * @param text
         */
        Types(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    private List<Types> availableTypes = new ArrayList<>();

    private BaseQuestion Alimentacion = null;
    private BaseQuestion Ejercicio = null;
    private BaseQuestion Prevencion = null;
    private BaseQuestion Perfilamiento = null;

    public Questions(){

        availableTypes.addAll(Arrays.asList(Types.NUTRITION, Types.EXERCISE, Types.PREVENTION, Types.PROFILING));

    }

    public BaseQuestion getAlimentacion() {
        return Alimentacion;
    }

    public void setAlimentacion(BaseQuestion alimentacion) {
        Alimentacion = alimentacion;
    }

    public BaseQuestion getEjercicio() {
        return Ejercicio;
    }

    public void setEjercicio(BaseQuestion ejercicio) {
        Ejercicio = ejercicio;
    }

    public BaseQuestion getPrevencion() {
        return Prevencion;
    }

    public void setPrevencion(BaseQuestion prevencion) {
        Prevencion = prevencion;
    }

    public BaseQuestion getPerfilamiento() {
        return Perfilamiento;
    }

    public void setPerfilamiento(BaseQuestion perfilamiento) {
        Perfilamiento = perfilamiento;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public Questions compose(String json) {
        return new Gson().fromJson(json, Questions.class);
    }


    /**
     * Return a list with the available types to choose from
     * @param activeTypes A string that determinate which types are available. A '0' means available and a '1' means unavailable
     * @return
     */
    private List<Types> sortAvailableTypes(String activeTypes){

        if(activeTypes.charAt(0) == '1')
            availableTypes.remove(Types.NUTRITION);

        if(activeTypes.charAt(1) == '1')
            availableTypes.remove(Types.EXERCISE);

        if(activeTypes.charAt(2) == '1')
            availableTypes.remove(Types.PREVENTION);

        if(activeTypes.charAt(3) == '1')
            availableTypes.remove(Types.PROFILING);

        return availableTypes;
    }

    public Types getRandomAvailableType(String activeTypes){

        sortAvailableTypes(activeTypes);

        if(availableTypes.size() == 0)
            return null;

        if(availableTypes.size() == 1)
            return availableTypes.get(0);

        Random rand = new Random(Calendar.getInstance().getTimeInMillis());
        return availableTypes.get(rand.nextInt(availableTypes.size()));
    }

    public List<Pregunta> getRandomTestList(Types testType){

        switch(testType){
            case NUTRITION:
                return Alimentacion.getPreguntas();
            case EXERCISE:
                return Ejercicio.getPreguntas();
            case PREVENTION:
                return Prevencion.getPreguntas();
            case PROFILING:
                return Perfilamiento.getPreguntas();
        }

        return null;
    }
}
