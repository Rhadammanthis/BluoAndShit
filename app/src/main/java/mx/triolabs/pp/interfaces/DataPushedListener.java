package mx.triolabs.pp.interfaces;

/**
 * Created by hugomedina on 12/27/16.
 */

/**
 * Can be fired when receiving pushed data
 */
public interface DataPushedListener {

    /**
     * Passes the pushed type
     * @param type The tip type that was pushed
     */
    void onDataPushed(String type);

}
