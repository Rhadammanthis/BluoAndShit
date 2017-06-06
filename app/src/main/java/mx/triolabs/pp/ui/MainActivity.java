package mx.triolabs.pp.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import com.google.firebase.messaging.RemoteMessage;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;

import java.util.HashMap;
import java.util.Hashtable;

import mx.triolabs.pp.PPAplication;
import mx.triolabs.pp.R;
import mx.triolabs.pp.fragments.DiagnosticFragment;
import mx.triolabs.pp.fragments.ProfileFragment;
import mx.triolabs.pp.fragments.StatusFragment;
import mx.triolabs.pp.fragments.TestFragment;
import mx.triolabs.pp.fragments.TipsFragment;
import mx.triolabs.pp.interfaces.DataFetchedListener;
import mx.triolabs.pp.interfaces.DataPushedListener;
import mx.triolabs.pp.interfaces.TabClickedListener;
import mx.triolabs.pp.objects.FetcherResponse;
import mx.triolabs.pp.objects.mediciones.response.MedicionesResponse;
import mx.triolabs.pp.util.DataAccessor;
import mx.triolabs.pp.util.Fetcher;

public class MainActivity extends BaseActivity implements TabClickedListener, DataFetchedListener{

    public static final String STARTED_FROM_NOTIFICATION = "started_from_notification";

    public static final String DIAGNOSTIC_FRAGMENT_TAG = "diagnostic_fragment";
    public static final String STATUS_FRAGMENT_TAG = "status_fragment";
    public static final String TEST_FRAGMENT_TAG = "test_fragment";
    public static final String PROFILE_FRAGMENT_TAG = "profile_fragment";

    public ToggleButton tbStatus, tbTest, tbProfile;
    private TabButtonClicker tabClicker = new TabButtonClicker(this);

    private MedicionesResponse diagnosticResult = null;
    private DataAccessor accessor;

    private boolean statusFragmentActive = true;
    public boolean newDiagnostic = false;

    private DataPushedListener dataPushedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        initComponents();

    }

    private void initComponents() {

        tbStatus = (ToggleButton) findViewById(R.id.main_tb_status);
        tbTest = (ToggleButton) findViewById(R.id.main_tb_test);
        tbProfile = (ToggleButton) findViewById(R.id.main_tb_profile);

        tbStatus.setEnabled(false);
        tbTest.setEnabled(false);
        tbProfile.setEnabled(false);

        accessor = new DataAccessor(MainActivity.this);

        //Check the Status tab when initializing the activity and load the corresponding fragment
        tbStatus.setChecked(true);
        addFragment(new DiagnosticFragment(), STATUS_FRAGMENT_TAG);

        //The main listener thats fires when data is pushed
        ((PPAplication)getApplication()).getNativePusher().setFCMListener(new FCMPushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {

                    //Should set if new tips available
                    accessor.setNewTipsAvailable(remoteMessage.getData().get("type"));

                    //Changes the UI resource
                    dataPushedListener = ((StatusFragment)(getFragmentManager().findFragmentByTag(STATUS_FRAGMENT_TAG)));

                    if(remoteMessage.getData().get("type") != null && dataPushedListener != null){
                        dataPushedListener.onDataPushed(remoteMessage.getData().get("type"));
                    }
            }
        });

    }

    /**
     * Adds a Fragment to the application's Manager
     * @param fragment The Fragment
     * @param tag A tag to identify the Fragment in the Manager
     */
    public void addFragment(Fragment fragment, String tag){
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_out_right, R.animator.slide_out_left)
                .add(R.id.main_fl_container, fragment, tag)
                .commit();
    }

    /**
     * Adds a Fragment to the application's Manager and BackStack
     * @param fragment The Fragment
     * @param tag A tag to identify the Fragment in the Manager
     */
    public void addFragmentWithBackStack(Fragment fragment, String tag){
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_out_right, R.animator.slide_out_left)
                .addToBackStack(tag)
                .add(R.id.main_fl_container, fragment, tag)
                .commit();
    }

    /**
     * Replaces the current Fragment on the application's Manager
     * @param fragment The new Fragment
     * @param tag A tag to identify the Fragment in the Manager
     */
    public void replaceFragment(Fragment fragment, String tag){
        getFragmentManager().popBackStack();

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_out_right, R.animator.slide_out_left)
                .replace(R.id.main_fl_container, fragment, tag)
                .commit();
    }

    @Override
    public void onTabClicked(int tabIndex) {
        //switch to selected tab
        switch(tabIndex){
            case 0:

                //If the tab button is pressed when registering a new diagnostic, the 'Status' fragment is shown
                //instead of the 'Diagnostic' one
                if(newDiagnostic){

                    //Custom object to recreate last diagnostic's result
                    MedicionesResponse response = new MedicionesResponse();
                    response.setOk(accessor.lastAnalysisResult());

                    replaceFragment(StatusFragment.newInstance(response), MainActivity.STATUS_FRAGMENT_TAG);

                    newDiagnostic = false;
                }
                else {

                    Fragment fragg = getFragmentManager().findFragmentByTag(STATUS_FRAGMENT_TAG);

                    //Clears the back stack
                    for(int i = 0; i < getFragmentManager().getBackStackEntryCount(); i++)
                        getFragmentManager().popBackStack();

                    if(!statusFragmentActive){
                        getFragmentManager().beginTransaction().show(fragg).commit();
                        statusFragmentActive = true;
                    }

                }

                break;
            case 1:

                //Hide the Status fragment and load a new one
                if(statusFragmentActive){
                    getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag(STATUS_FRAGMENT_TAG)).commit();
                    statusFragmentActive = false;

                    //If the fragment was called from tjeNotification, signal that we don't need a new progress dialog
                    if(getIntent().getBooleanExtra(STARTED_FROM_NOTIFICATION, false))
                        addFragmentWithBackStack(TestFragment.newInstance(true), TEST_FRAGMENT_TAG);
                    else
                        addFragmentWithBackStack(TestFragment.newInstance(false), TEST_FRAGMENT_TAG);
                }
                else {
                    addFragmentWithBackStack(new TestFragment(), TEST_FRAGMENT_TAG);
                }


                break;
            case 2:

                //Hide the Status fragment and load a new one
                if(statusFragmentActive){
                    getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag(STATUS_FRAGMENT_TAG)).commit();
                    statusFragmentActive = false;
                    addFragmentWithBackStack(new ProfileFragment(), PROFILE_FRAGMENT_TAG);
                }
                else {
                    addFragmentWithBackStack(new ProfileFragment(), PROFILE_FRAGMENT_TAG);
                }

                break;
        }
    }

    @Override
    public void onDataFetched(FetcherResponse data) {

        //Compose a new Object with the fetched data
        if(data.getCode() == Fetcher.SUCCESS){
            diagnosticResult = new MedicionesResponse().compose(data.getBody());
        }

        //Saves the result and loads a new StatusFragment
        accessor.setLastAnaylisisResult(diagnosticResult.getOk());
        replaceFragment(StatusFragment.newInstance(diagnosticResult), STATUS_FRAGMENT_TAG);
        dismissProgressDialog();
    }

    /**
     * Activates the logic behind the tab's transition
     */
    public void addTabsListeners(){
        tbStatus.setOnClickListener(tabClicker);
        tbTest.setOnClickListener(tabClicker);
        tbProfile.setOnClickListener(tabClicker);

        tbStatus.setEnabled(true);
        tbTest.setEnabled(true);
        tbProfile.setEnabled(true);
    }

    /**
     * Prevents from switching tabs
     */
    public void removeTabListeners(){
        tbStatus.setOnClickListener(null);
        tbTest.setOnClickListener(null);
        tbProfile.setOnClickListener(null);

        tbStatus.setEnabled(false);
        tbTest.setEnabled(false);
        tbProfile.setEnabled(false);
    }

    /**
     * General logic to transition among tabs. Updates UI elements and informs the MainView of the selected tab
     */
    private class TabButtonClicker implements View.OnClickListener{

        private int currentTab = 0;
        private TabClickedListener tabClickedListener;

        private TabButtonClicker(TabClickedListener tabClickedListener){
            this.tabClickedListener = tabClickedListener;
        }

        @Override
        public void onClick(View view) {

            if(((view.getId() == R.id.main_tb_status) && currentTab != 0) ||
                    (view.getId() == R.id.main_tb_test) && currentTab != 1 ||
                    (view.getId() == R.id.main_tb_profile) && currentTab != 2) {


                switch (view.getId()) {
                    case R.id.main_tb_status:
                        currentTab = 0;
                        ((ToggleButton) findViewById(R.id.main_tb_test)).setChecked(false);
                        ((ToggleButton) findViewById(R.id.main_tb_profile)).setChecked(false);
                        break;
                    case R.id.main_tb_test:
                        currentTab = 1;
                        ((ToggleButton) findViewById(R.id.main_tb_status)).setChecked(false);
                        ((ToggleButton) findViewById(R.id.main_tb_profile)).setChecked(false);
                        break;
                    case R.id.main_tb_profile:
                        currentTab = 2;
                        ((ToggleButton) findViewById(R.id.main_tb_status)).setChecked(false);
                        ((ToggleButton) findViewById(R.id.main_tb_test)).setChecked(false);
                        break;
                }

                tabClickedListener.onTabClicked(currentTab);
            }
            else{
                ((ToggleButton) findViewById(view.getId())).setChecked(true);
            }
        }
    }

    @Override
    public void onBackPressed() {

        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.main_fl_container);

        //If the current Fragment is an instance of DiagnosticFragment, return to the StatusFragment
        if(currentFragment.getClass() == DiagnosticFragment.class){
            if(((DiagnosticFragment)currentFragment).isNewDiagnostic()){
                MedicionesResponse response = new MedicionesResponse();
                response.setOk(accessor.lastAnalysisResult());
                replaceFragment(StatusFragment.newInstance(response), MainActivity.STATUS_FRAGMENT_TAG);
            }
        }
        //If the current Fragment is an instance of TipsFragment, return to the previous Fragment, kill the app otherwise
        else if (currentFragment.getClass() == TipsFragment.class) {
            getFragmentManager().popBackStack();
        }
        else {
            finish();
        }

    }
}

