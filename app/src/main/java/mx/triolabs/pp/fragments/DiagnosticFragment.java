package mx.triolabs.pp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.HitBuilders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mx.triolabs.pp.PPAplication;
import mx.triolabs.pp.R;
import mx.triolabs.pp.interfaces.DataFetchedListener;
import mx.triolabs.pp.interfaces.TabClickedListener;
import mx.triolabs.pp.objects.FetcherResponse;
import mx.triolabs.pp.objects.historial.response.Historial;
import mx.triolabs.pp.objects.mediciones.request.Mediciones;
import mx.triolabs.pp.objects.mediciones.response.MedicionesResponse;
import mx.triolabs.pp.objects.patient.response.Patient;
import mx.triolabs.pp.ui.LoginActivity;
import mx.triolabs.pp.ui.MainActivity;
import mx.triolabs.pp.util.DataAccessor;
import mx.triolabs.pp.util.Fetcher;
import mx.triolabs.pp.util.HttpVerbs;

import static mx.triolabs.pp.PPAplication.URL_GET_MY_DATA;
import static mx.triolabs.pp.PPAplication.URL_GET_MY_HISTORY;
import static mx.triolabs.pp.PPAplication.URL_SEND_READINGS;

/**
 * Created by hugomedina on 11/29/16.
 */

public class DiagnosticFragment extends BaseFragment implements TabClickedListener{

    private static final String FORCE_DIAGNOSTIC = "force_diagnostic";
    private static final String WELCOME_MESSAGE = "show_welcome_message";

    private ToggleButton tbGlucose, tbOxygen, tbPulse;
    private TextView tvName, tvLastDiagnostic, tvDate, tvTime;
    private Button bSave, bAnalyze, bDump;
    private ScrollView rlMain;
    private EditText etData;
    private LinearLayout llPulseData, llWelcome;

    private EditText etPulse, etPulse1, etPulse2;

    private Patient patient;
    private Historial historial;

    private int currentTabIndex = 0;
    private boolean forceDiagnostic = false, showWelcomeMessage = true;

    /**
     * New fragment instance that prevents from skipping the diagnostic
     * @param forceDiagnostic is the fragment should force the diagnostic
     * @param showWelcomeMessage weather the welcome vie should be shown
     * @return a new instance of the fragment
     */
    public static DiagnosticFragment newInstance(boolean forceDiagnostic, boolean showWelcomeMessage){
        DiagnosticFragment fragment = new DiagnosticFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(FORCE_DIAGNOSTIC, forceDiagnostic);
        bundle.putBoolean(WELCOME_MESSAGE, showWelcomeMessage);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Track this view for GAnalytics
        String name = "Diagnostic";
        Log.i(PPAplication.TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        View mainView = inflater.inflate(R.layout.fragment_diagnostic, container, false);
        initComponents(mainView);

        if(getArguments() != null){
            forceDiagnostic = getArguments().getBoolean(FORCE_DIAGNOSTIC);
            showWelcomeMessage = getArguments().getBoolean(WELCOME_MESSAGE);
        }

        showProgressDialog(getActivity(), getString(R.string.system_loading));

        //Activates the logic behind the transition among tabs
        addTabsListeners();

        //Fetches user's latest diagnostics
        new Fetcher(getActivity()).fetchData(HttpVerbs.GET,
                URL_GET_MY_HISTORY,
                null,
                dataAccessor.getLoginCredentials(),
                new DataFetchedListener() {
                    @Override
                    public void onDataFetched(FetcherResponse data) {
                        Log.d("Dolores", data.getBody());
                        if(data.getCode() == Fetcher.SUCCESS){

                            historial = new Historial().compose(data.getBody());
                            tvLastDiagnostic.setText(historial.getLatestDate());

                            //If a diagnostic has been recorded today, skip the diagnostic altogether unless forced to

                            DateFormat dFDay = new SimpleDateFormat("dd");
                            String day = dFDay.format(Calendar.getInstance().getTime());

                            if (day.equals(tvLastDiagnostic.getText().toString().substring(0, 2)) && !forceDiagnostic) {

                                //Custom object to recreate last diagnostic's result
                                MedicionesResponse response = new MedicionesResponse();
                                response.setOk(dataAccessor.lastAnalysisResult());

                                replaceFragment(StatusFragment.newInstance(response), MainActivity.STATUS_FRAGMENT_TAG);
                            }
                            else{

                                //Fetches user's personal data
                                new Fetcher(getActivity()).fetchData(HttpVerbs.GET,
                                        URL_GET_MY_DATA,
                                        null,
                                        dataAccessor.getLoginCredentials(),
                                        new DataFetchedListener() {
                                            @Override
                                            public void onDataFetched(FetcherResponse data) {
                                                Log.d("Dolores", data.getBody());
                                                if(data.getCode() == Fetcher.SUCCESS) {

                                                    //All set and done, present views
                                                    patient = new Patient().compose(data.getBody());
                                                    tvName.setText(patient.getNombre());

                                                    //Shows welcome message view if necessary
                                                    if(showWelcomeMessage){
                                                        llWelcome.setVisibility(View.VISIBLE);
                                                    }
                                                    else{
                                                        rlMain.setVisibility(View.VISIBLE);
                                                        llWelcome.setVisibility(View.GONE);
                                                    }

                                                    dismissProgressDialog();
                                                }
                                            }
                                        });
                            }
                        }
                        else {

                            dismissProgressDialog();

                            if(data.getCode() == Fetcher.UNAUTHORIZED){

                                new MaterialDialog.Builder(getActivity())
                                        .canceledOnTouchOutside(false)
                                        .title(getString(R.string.system_alert))
                                        .content(getString(R.string.system_auth_error))
                                        .positiveText("Ok")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                dataAccessor.clearUserData();
                                                startActivity(new Intent(getActivity(), LoginActivity.class));

                                            }
                                        })
                                        .dismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialogInterface) {

                                                dataAccessor.clearUserData();
                                                startActivity(new Intent(getActivity(), LoginActivity.class));

                                            }
                                        })
                                        .show();

                            }

                            //Shows welcome message view if necessary
                            if(showWelcomeMessage){
                                llWelcome.setVisibility(View.VISIBLE);
                            }
                            else{
                                rlMain.setVisibility(View.VISIBLE);
                                llWelcome.setVisibility(View.GONE);
                            }
                        }

                    }
                });


        //Date format objects to extract time and date
        DateFormat dFTime = new SimpleDateFormat("h:mm a");
        DateFormat dFDate = new SimpleDateFormat("dd - MMMM - yyyy");
        String time = dFTime.format(Calendar.getInstance().getTime());
        String date = dFDate.format(Calendar.getInstance().getTime());
        tvTime.setText(time);
        tvDate.setText(date);

        return mainView;

    }

    public void initComponents(View view) {

        DiagnosticButtonClicker diagnosticButtonClicker = new DiagnosticButtonClicker(view, this);
        dataAccessor = new DataAccessor(getActivity());

        etData = (EditText) view.findViewById(R.id.diagnostic_et_data);
        etPulse = (EditText) view.findViewById(R.id.diagnostic_et_pulse);
        etPulse1 = (EditText) view.findViewById(R.id.diagnostic_et_pulse1);
        etPulse2 = (EditText) view.findViewById(R.id.diagnostic_et_pulse2);
        tbGlucose = (ToggleButton) view.findViewById(R.id.diagnostic_tb_glucose);
        tbOxygen = (ToggleButton) view.findViewById(R.id.diagnostic_tb_oxygen);
        tbPulse = (ToggleButton) view.findViewById(R.id.diagnostic_tb_pulse);
        llPulseData = (LinearLayout) view.findViewById(R.id.diagnostic_ll_pulse);
        tvName = (TextView) view.findViewById(R.id.diagnostic_tv_name);
        tvLastDiagnostic = (TextView) view.findViewById(R.id.diagnostic_tv_last);
        tvDate = (TextView) view.findViewById(R.id.diagnostic_tv_date);
        tvTime = (TextView) view.findViewById(R.id.diagnostic_tv_time);
        rlMain = (ScrollView) view.findViewById(R.id.diagnostic_sv);
        bSave = (Button) view.findViewById(R.id.diagnostic_b_save);
        bAnalyze = (Button) view.findViewById(R.id.diagnostic_b_analyze);
        bDump = (Button) view.findViewById(R.id.diagnostic_b_dump);
        llWelcome = (LinearLayout) view.findViewById(R.id.diagnostic_ll_welcome);

        tbGlucose.setOnClickListener(diagnosticButtonClicker);
        tbOxygen.setOnClickListener(diagnosticButtonClicker);
        tbPulse.setOnClickListener(diagnosticButtonClicker);

        //Hides views that should not shown by default
        llPulseData.setVisibility(View.GONE);
        tbGlucose.setChecked(true);
        rlMain.setVisibility(View.GONE);

        //Disables "Guardar" button when there's no data in the input box
        etData.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() > 0)
                    bSave.setEnabled(true);
                else
                    bSave.setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
        etPulse.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() > 0 && etPulse1.getText().toString().length() > 0 && etPulse2.getText().toString().length() > 0)
                    bSave.setEnabled(true);
                else
                    bSave.setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
        etPulse1.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() > 0 && etPulse.getText().toString().length() > 0 && etPulse2.getText().toString().length() > 0)
                    bSave.setEnabled(true);
                else
                    bSave.setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
        etPulse2.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() > 0 && etPulse.getText().toString().length() > 0 && etPulse1.getText().toString().length() > 0)
                    bSave.setEnabled(true);
                else
                    bSave.setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        //Saves a local instance of the data in the input box
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(currentTabIndex){
                    case 0:
                        dataAccessor.setGlucose(etData.getText().toString());
                        break;
                    case 1:
                        dataAccessor.setOxygen(etData.getText().toString());
                        break;
                    case 2:
                        dataAccessor.setPulse(etPulse.getText().toString() + " " + etPulse1.getText().toString() + " " + etPulse2.getText().toString());
                        break;
                }
            }
        });

        //Calls the assessment method in the parent Activity
        bAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showProgressDialog(getActivity(), getString(R.string.system_analyzing));

                //Prod
                Mediciones mediciones = new Mediciones();

                mediciones.setGlucosa(dataAccessor.getGlucose());
                mediciones.setOxigeno(dataAccessor.getOxygen());

                if(!dataAccessor.getPulse().isEmpty())
                    mediciones.setPresion(dataAccessor.getPulse().split(" ")[0]+"/"+dataAccessor.getPulse().split(" ")[1]+"-"+dataAccessor.getPulse().split(" ")[2]);
                else
                    mediciones.setPresion("");

                new Fetcher(getActivity()).fetchData(HttpVerbs.POST,
                        URL_SEND_READINGS,
                        mediciones.serialize(),
                        dataAccessor.getLoginCredentials(),
                        ((MainActivity)getActivity()));

                //Sends an Action to Google Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Data analyzed")
                        .build());

                dataAccessor.clearDiagnosticData();
            }
        });

        bDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlMain.setVisibility(View.VISIBLE);
                llWelcome.setVisibility(View.GONE);
            }
        });

        //Checks if there are any local instances of data and if so, it's presented
        if(dataAccessor.getGlucose().length() > 0)
            etData.setText(dataAccessor.getGlucose());

    }

    public boolean isNewDiagnostic(){
        return forceDiagnostic;
    }

    @Override
    public void onTabClicked(int tabIndex) {

        //Clears any text in the input boxes
        etData.setText("");
        etPulse.setText("");
        etPulse1.setText("");

        //disables the "Save" button
        bSave.setEnabled(false);
        currentTabIndex = tabIndex;

        //Presents the correct input box according to the selected tab
        if(tabIndex == 2) {
            etData.setVisibility(View.GONE);
            llPulseData.setVisibility(View.VISIBLE);
        }
        else{
            etData.setVisibility(View.VISIBLE);
            llPulseData.setVisibility(View.GONE);
        }

        //Writes the local instance of data (if available) in the corresponding input box
        switch(tabIndex){
            case 0:
                String glucose = dataAccessor.getGlucose();
                if(glucose.length() > 0)
                    etData.setText(glucose);
                break;
            case 1:
                String oxygen = dataAccessor.getOxygen();
                if(oxygen.length() > 0)
                    etData.setText(oxygen);
                break;
            case 2:
                String pulse = dataAccessor.getPulse();
                if(pulse.length() > 0) {
                    etPulse.setText(pulse.split(" ")[0]);
                    etPulse1.setText(pulse.split(" ")[1]);
                    etPulse2.setText(pulse.split(" ")[2]);
                }

                etPulse.requestFocus();
                break;
        }

        //If there's any data available, the tab toggle button is set tu "checked"
        if(dataAccessor.getGlucose().length() > 0)
            tbGlucose.setChecked(true);
        if(dataAccessor.getOxygen().length() > 0)
            tbOxygen.setChecked(true);
        if(dataAccessor.getPulse().length() > 0)
            tbPulse.setChecked(true);
    }

    /**
     * Handles the logic behind switching tabs and anything that gets carried throughout
     */
    private class DiagnosticButtonClicker implements View.OnClickListener{

        private int currentTab = 0;
        private View view;
        private TabClickedListener tabClickedListener;

        private DiagnosticButtonClicker(View view, TabClickedListener tabClickedListener){
            this.view = view;
            this.tabClickedListener = tabClickedListener;
        }

        @Override
        public void onClick(View view) {

            if(((view.getId() == R.id.diagnostic_tb_glucose) && currentTab != 0) ||
                    (view.getId() == R.id.diagnostic_tb_oxygen) && currentTab != 1 ||
                    (view.getId() == R.id.diagnostic_tb_pulse) && currentTab != 2) {


                switch (view.getId()) {
                    case R.id.diagnostic_tb_glucose:
                        currentTab = 0;
                        ((TextView) this.view.findViewById(R.id.diagnostic_tv_magnitude)).setText(getResources().getString(R.string.diagnostic_magnitude_glucose));
                        ((ToggleButton) this.view.findViewById(R.id.diagnostic_tb_oxygen)).setChecked(false);
                        ((ToggleButton) this.view.findViewById(R.id.diagnostic_tb_pulse)).setChecked(false);
                        break;
                    case R.id.diagnostic_tb_oxygen:
                        currentTab = 1;
                        ((TextView) this.view.findViewById(R.id.diagnostic_tv_magnitude)).setText(Html.fromHtml(getString(R.string.diagnostic_magnitude_oxygen)));
                        ((ToggleButton) this.view.findViewById(R.id.diagnostic_tb_glucose)).setChecked(false);
                        ((ToggleButton) this.view.findViewById(R.id.diagnostic_tb_pulse)).setChecked(false);
                        break;
                    case R.id.diagnostic_tb_pulse:
                        currentTab = 2;
                        ((TextView) this.view.findViewById(R.id.diagnostic_tv_magnitude)).setText(getResources().getString(R.string.diagnostic_magnitude_pulse));
                        ((ToggleButton) this.view.findViewById(R.id.diagnostic_tb_glucose)).setChecked(false);
                        ((ToggleButton) this.view.findViewById(R.id.diagnostic_tb_oxygen)).setChecked(false);
                        break;
                }

                tabClickedListener.onTabClicked(currentTab);

            }
            else{
                ((ToggleButton) this.view.findViewById(view.getId())).setChecked(true);
            }
        }
    }
}
