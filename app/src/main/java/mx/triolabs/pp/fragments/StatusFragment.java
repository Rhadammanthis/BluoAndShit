package mx.triolabs.pp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;

import java.util.Hashtable;

import mx.triolabs.pp.PPAplication;
import mx.triolabs.pp.R;
import mx.triolabs.pp.interfaces.DataFetchedListener;
import mx.triolabs.pp.interfaces.DataPushedListener;
import mx.triolabs.pp.objects.FetcherResponse;
import mx.triolabs.pp.objects.historial.response.Historial;
import mx.triolabs.pp.objects.mediciones.response.MedicionesResponse;
import mx.triolabs.pp.objects.misc.Tips;
import mx.triolabs.pp.ui.MainActivity;
import mx.triolabs.pp.util.CustomDialog;
import mx.triolabs.pp.util.DataAccessor;
import mx.triolabs.pp.util.Fetcher;
import mx.triolabs.pp.util.HttpVerbs;

import static mx.triolabs.pp.PPAplication.URL_GET_MY_HISTORY;
import static mx.triolabs.pp.ui.MainActivity.STARTED_FROM_NOTIFICATION;

/**
 * Created by hugomedina on 11/29/16.
 */

public class StatusFragment extends BaseFragment implements DataPushedListener{


    private static final String DIAGNOSTIC_DATA = "diagnostic_data";
    public static final String TIPS_FRAGMENT = "tips_frag";

    private RelativeLayout rlDiagnosticCard;
    private ToggleButton tbLatestDiagnostic;
    private Button bNewDiagnostic;
    private ImageButton ibHealth, ibExercise, ibNutrition, ibHealthNotif, ibExerciseNotif, ibNutritionNotif;
    private ImageView ivStatus;
    private TextView tvOxygen, tvPulse, tvGlucose;

    private MedicionesResponse diagnosticData = null;

    /**
     * Creates a new instance of the Status fragment bundled with last diagnostic's data
     * @param data The object that holds last diagnostic's data 295, 88, 111 -16 - 67
     * @return a new instance of the Status fragment
     */
    public static StatusFragment newInstance(MedicionesResponse data){

        StatusFragment fragment = new StatusFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIAGNOSTIC_DATA, data.serialize());
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View mainView = inflater.inflate(R.layout.fragment_status, container, false);
        initComponents(mainView);

        //Track this view for GAnalytics
        String name = "Status";
        Log.i(PPAplication.TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if(getArguments() != null){
            diagnosticData = new Gson().fromJson(getArguments().getString(DIAGNOSTIC_DATA), MedicionesResponse.class);

            //Changes the Bluo character if needed
            if(diagnosticData.getOk()){
                ivStatus.setImageResource(R.drawable.a_personaje_ok);
            }
            else{
                ivStatus.setImageResource(R.drawable.a_personaje_alerta);
            }
        }

        //Clears leftover data from the Diagnostic fragment
        dataAccessor.clearDiagnosticData();

        //If the Activity was fired from the Notifications, load the 'Test' fragment
        if((getActivity()).getIntent().getBooleanExtra(STARTED_FROM_NOTIFICATION, false)){
            ((MainActivity)getActivity()).tbTest.performClick();
        }
        else{

            //Dumps data into the 'Last Diagnostic' card
            new Fetcher(getActivity()).fetchData(HttpVerbs.GET,
                    URL_GET_MY_HISTORY,
                    null,
                    dataAccessor.getLoginCredentials(),
                    new DataFetchedListener() {
                        @Override
                        public void onDataFetched(FetcherResponse data) {
                            if(data.getCode() == Fetcher.SUCCESS){

                                Historial historial = new Historial().compose(data.getBody());

                                //Detail from the last entry in the hitoric data list
                                String glucose = historial.getGlucosa().get(historial.getGlucosa().size()- 1).getCantidad() + " " + getActivity().getResources().getString(R.string.diagnostic_magnitude_glucose);
                                String oxygen = historial.getOxigeno().get(historial.getOxigeno().size()- 1).getCantidad() + getString(R.string.diagnostic_magnitude_oxygen);
                                tvGlucose.setText(glucose);
                                tvOxygen.setText(Html.fromHtml(oxygen));
                                tvPulse.setText(historial.getPresion().get(historial.getPresion().size()- 1).getCantidadAlta()
                                        + " / " + historial.getPresion().get(historial.getPresion().size()- 1).getCantidadBaja()
                                        + " / " + historial.getPresion().get(historial.getPresion().size()- 1).getPulso());

                                dismissProgressDialog();
                            }
                            else if(data.getCode() == Fetcher.FAILURE){
                                showOkDialog(getActivity(), getString(R.string.system_alert), getString(R.string.system_error));
                            }
                        }
                    });
        }

        return mainView;

    }

    private void initComponents(View view) {

        dataAccessor = new DataAccessor(getActivity());

        rlDiagnosticCard = (RelativeLayout) view.findViewById(R.id.status_rl_diagnostic_card);
        tbLatestDiagnostic = (ToggleButton) view.findViewById(R.id.status_tb_last);
        bNewDiagnostic = (Button) view.findViewById(R.id.status_b_new_diagnostic);
        ibHealth = (ImageButton) view.findViewById(R.id.status_ib_health);
        ibExercise = (ImageButton) view.findViewById(R.id.status_ib_exercise);
        ibNutrition = (ImageButton) view.findViewById(R.id.status_ib_nutrition);
        ibHealthNotif = (ImageButton) view.findViewById(R.id.status_ib_health_notif);
        ibExerciseNotif = (ImageButton) view.findViewById(R.id.status_ib_exercise_notif);
        ibNutritionNotif = (ImageButton) view.findViewById(R.id.status_ib_nutrition_notif);
        ivStatus = (ImageView) view.findViewById(R.id.status_iv_status);
        tvOxygen = (TextView) view.findViewById(R.id.status_tv_oxygen);
        tvPulse = (TextView) view.findViewById(R.id.status_tv_pulse);
        tvGlucose = (TextView) view.findViewById(R.id.status_tv_glucose);

        //Show or hide the card with the last diagnostic's data if button remains pressed, hide is released
        tbLatestDiagnostic.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    //Sends an Action to Google Analytics
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Show latest diagnostic")
                            .build());

                    tbLatestDiagnostic.setChecked(true);
                    rlDiagnosticCard.setVisibility(View.VISIBLE);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    tbLatestDiagnostic.setChecked(false);
                    rlDiagnosticCard.setVisibility(View.GONE);
                }
                return true;
            }
        });

        bNewDiagnostic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Sends an Action to Google Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("New diagnostic started")
                        .build());

                ((MainActivity)getActivity()).newDiagnostic = true;
                removeTabListeners();
                replaceFragment(DiagnosticFragment.newInstance(true, false), MainActivity.STATUS_FRAGMENT_TAG);
            }
        });

        View.OnClickListener exerciseOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentWithBackStack(TipsFragment.newInstance(Tips.Types.EXERCISE), TIPS_FRAGMENT);

                //Sends an Action to Google Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Checked Exercise tips")
                        .build());

                //Resets the 'new tips' flag and updated UI
                dataAccessor.clearNewTips("3");
                changeExerciseButton();
            }
        };

        View.OnClickListener healthOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentWithBackStack(TipsFragment.newInstance(Tips.Types.HEALTH), TIPS_FRAGMENT);

                //Sends an Action to Google Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Checked Health tips")
                        .build());

                //Resets the 'new tips' flag and updated UI
                dataAccessor.clearNewTips("2");
                changeHealthButton();
            }
        };

        View.OnClickListener nutritionOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentWithBackStack(TipsFragment.newInstance(Tips.Types.NUTRITION), TIPS_FRAGMENT);

                //Sends an Action to Google Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Checked Nutrition tips")
                        .build());

                //Resets the 'new tips' flag and updated UI
                dataAccessor.clearNewTips("1");
                changeNutritionButton();
            }
        };

        ibExercise.setOnClickListener(exerciseOnClick);
        ibHealth.setOnClickListener(healthOnClick);
        ibNutrition.setOnClickListener(nutritionOnClick);
        ibExerciseNotif.setOnClickListener(exerciseOnClick);
        ibHealthNotif.setOnClickListener(healthOnClick);
        ibNutritionNotif.setOnClickListener(nutritionOnClick);

        //Check if new tips available. if so change image resource
        changeExerciseButton();
        changeNutritionButton();
        changeHealthButton();


    }

    public void changeNutritionButton(){
        ibNutrition.setVisibility(dataAccessor.areNewNutritionTipsAvailable() ? View.GONE : View.VISIBLE);
        ibNutritionNotif.setVisibility(dataAccessor.areNewNutritionTipsAvailable() ? View.VISIBLE : View.GONE);
    }

    public void changeExerciseButton(){
        ibExercise.setVisibility(dataAccessor.areNewExerciseTipsAvailable() ? View.GONE : View.VISIBLE);
        ibExerciseNotif.setVisibility(dataAccessor.areNewExerciseTipsAvailable() ? View.VISIBLE : View.GONE);
    }

    public void changeHealthButton(){
        ibHealth.setVisibility(dataAccessor.areNewHealthTipsAvailable() ? View.GONE : View.VISIBLE);
        ibHealthNotif.setVisibility(dataAccessor.areNewHealthTipsAvailable() ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onDataPushed(final String type) {

        //When data is pushed, update the UI on the main thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Change the correct view according to the pushed type
                switch(type){
                    case "1":
                        changeNutritionButton();
                        break;
                    case "2":
                        changeHealthButton();
                        break;
                    case "3":
                        changeExerciseButton();
                        break;
                }

            }
        });



    }
}
