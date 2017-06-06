package mx.triolabs.pp.interfaces;

/**
 * Created by hugomedina on 11/29/16.
 */

/**
 * Provides an interface to link a selected Tab with its parent View
 */
public interface TabClickedListener {

    /**
     * Passes the clicked Tab index to the main View
     * @param tabIndex The index of the selcted tab
     */
    void onTabClicked(int tabIndex);

}
