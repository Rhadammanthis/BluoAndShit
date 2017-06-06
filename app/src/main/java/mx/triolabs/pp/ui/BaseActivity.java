package mx.triolabs.pp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.Tracker;

import mx.triolabs.pp.PPAplication;
import mx.triolabs.pp.R;
import mx.triolabs.pp.util.CustomDialog;


/**
 * Created by hugomedina on 12/6/16.
 */

public class BaseActivity extends AppCompatActivity {

    private CustomDialog progressD = null;

    public Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PPAplication application = (PPAplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    /**
     * Shows a dialog with a title, content and an Ok button
     * @param context Context to instantiate the dialog from
     * @param title The dialog's title
     * @param content The dialog's content
     */
    public void showOkDialog(Context context, String title, String content){

        new CustomDialog.Builder(context)
                .canceledOnTouchOutside(false)
                .title(title)
                .content(content)
                .positiveText("Ok")
                .show();

    }


    /**
     * Shows a dialog with a title, content and an Ok button
     * @param context Context to instantiate the dialog from
     * @param title The dialog's title
     * @param content The dialog's content
     * @param onPositive Callback to the action to perform is positive answered selected
     */
    public CustomDialog showOkDialog(Context context, String title, String content, View.OnClickListener onPositive){

        return new CustomDialog.Builder(context)
                .canceledOnTouchOutside(false)
                .title(title)
                .content(content)
                .positiveText("Ok")
                .onPositive(onPositive)
                .show();
    }

    /**
     * Displays a dialog that performs an action when an option is selected
     * @param context The context where the dialog will be instantiated from
     * @param title The title of the dialog
     * @param content The content of the dialog
     * @param onPositive Callback to the action to perform is positive answered selected
     */
    public void showActionDialog(Context context, String title, String content, View.OnClickListener onPositive){

        new CustomDialog.Builder(context)
                .canceledOnTouchOutside(false)
                .title(title)
                .content(content)
                .positiveText("Si")
                .negativeText("No")
                .onPositive(onPositive)
                .show();

    }

    /**
     * Creates an instances a of progress dialog
     * @param context Context to instantiate the dialog from
     * @param content Text to appear in dialog
     */
    public void showProgressDialog(Context context, String content){

        if(progressD != null)
            try {
                throw new ProgressDialogAlreadyRunningException("Progress dialog has already been instantiated. Check for previous calls and terminate");
            } catch (ProgressDialogAlreadyRunningException progressDialogAlreadyRunningException) {
                progressDialogAlreadyRunningException.printStackTrace();
            }
        else
            progressD = new CustomDialog.Builder(context)
                    .canceledOnTouchOutside(false)
                    .progressContent(content)
                    .progress(true)
                    .show();
    }

    /**
     * Dismisses a running instance of a progress dialog
     */
    public void dismissProgressDialog(){
        if(progressD == null)
            try {
                throw new NoProgressDialogActiveException("No progress dialog active. Make sure you've previously called it.");
            } catch (NoProgressDialogActiveException noProgressDialogActiveException) {
                noProgressDialogActiveException.printStackTrace();
            }
        else {
            progressD.dismiss();
            progressD = null;
        }
    }

    private class NoProgressDialogActiveException extends Exception{
        NoProgressDialogActiveException(String message){super(message);}
    }

    private class ProgressDialogAlreadyRunningException extends Exception{
        ProgressDialogAlreadyRunningException(String message){super(message);}
    }
}
