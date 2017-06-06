package mx.triolabs.pp.objects;

import com.google.gson.Gson;

/**
 * Created by hugomedina on 11/28/16.
 */

/**
 * Base class to serialize into a JSON or compose an object from a JSON
 * @param <Object> The Object's class
 */
public abstract class BaseObject<Object> {

    /**
     * Converts an object to a JSON String
     * @param obj The Object to be converted
     * @return A JSON String of the Object
     */
    protected String objectToJson(Object obj){
        return new Gson().toJson(obj);
    }

    /**
     * Serializes an object to json represented as a String
     * @return Json instance of the object
     */
    abstract public String serialize();

    /**
     * Composes anew Object out of a JSON String
     * @param json
     * @return
     */
    abstract public Object compose(String json);

}
