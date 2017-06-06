package mx.triolabs.pp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.Tracker;

import mx.triolabs.pp.ui.BaseActivity;
import mx.triolabs.pp.ui.MainActivity;
import mx.triolabs.pp.util.CustomDialog;
import mx.triolabs.pp.util.DataAccessor;

/**
 * Created by hugomedina on 12/21/16.
 */

/**
 * Interface to access the MainActivity methods
 */
public abstract class BaseFragment extends Fragment {

    public DataAccessor dataAccessor;
    public Tracker mTracker;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        dataAccessor = new DataAccessor(getActivity());
        mTracker = ((MainActivity)getActivity()).mTracker;
        return null;
    }

    /**
     * Adds a Fragment to the application's Manager
     * @param fragment The Fragment
     * @param tag A tag to identify the Fragment in the Manager
     */
    public void addFragment(Fragment fragment, String tag){
        ((MainActivity)getActivity()).addFragment(fragment, tag);
    }

    /**
     * Adds a Fragment to the application's Manager and BackStack
     * @param fragment The Fragment
     * @param tag A tag to identify the Fragment in the Manager
     */
    public void addFragmentWithBackStack(Fragment fragment, String tag){
        ((MainActivity)getActivity()).addFragmentWithBackStack(fragment, tag);
    }

    /**
     * Replaces the current Fragment on the application's Manager
     * @param fragment The new Fragment
     * @param tag A tag to identify the Fragment in the Manager
     */
    public void replaceFragment(Fragment fragment, String tag){
        ((MainActivity)getActivity()).replaceFragment(fragment, tag);
    }

    /**
     * Activates the logic behind the tab's transition
     */
    public void addTabsListeners(){
        ((MainActivity)getActivity()).addTabsListeners();
    }

    /**
     * Prevents from switching tabs
     */
    public void removeTabListeners(){
        ((MainActivity)getActivity()).removeTabListeners();
    }

    /**
     * Shows a dialog with a title, content and an Ok button
     * @param context Context to instantiate the dialog from
     * @param title The dialog's title
     * @param content The dialog's content
     */
    public void showOkDialog(Context context, String title, String content){
        ((MainActivity)getActivity()).showOkDialog(context, title, content);
    }

    /**
     * Shows a dialog with a title, content and an Ok button
     * @param context Context to instantiate the dialog from
     * @param title The dialog's title
     * @param content The dialog's content
     * @param onPositive Callback to the action to perform is positive answered selected
     */
    public CustomDialog showOkDialog(Context context, String title, String content, View.OnClickListener onPositive){
        return ((MainActivity)getActivity()).showOkDialog(context, title, content, onPositive);
    }

    /**
     * Displays a dialog that performs an action when an option is selected
     * @param context The context where the dialog will be instantiated from
     * @param title The title of the dialog
     * @param content The content of the dialog
     * @param onPositive Callback to the action to perform is positive answered selected
     */
    public void showActionDialog(Context context, String title, String content, View.OnClickListener onPositive){
        ((MainActivity)getActivity()).showActionDialog(context, title, content, onPositive);
    }

    /**
     * Creates an instances a of progress dialog
     * @param context Context to instantiate the dialog from
     * @param content Text to appear in dialog
     */
    public void showProgressDialog(Context context, String content){
        ((MainActivity)getActivity()).showProgressDialog(context, content);
    }

    /**
     * Dismisses a running instance of a progress dialog
     */
    public void dismissProgressDialog(){
        ((MainActivity)getActivity()).dismissProgressDialog();
    }


}
