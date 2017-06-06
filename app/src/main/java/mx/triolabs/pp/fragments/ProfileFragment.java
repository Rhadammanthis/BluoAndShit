package mx.triolabs.pp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.analytics.HitBuilders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.triolabs.pp.PPAplication;
import mx.triolabs.pp.R;
import mx.triolabs.pp.adapters.ProfileAdapter;
import mx.triolabs.pp.interfaces.DataFetchedListener;
import mx.triolabs.pp.objects.FetcherResponse;
import mx.triolabs.pp.objects.historial.response.Historial;
import mx.triolabs.pp.objects.patient.response.Patient;
import mx.triolabs.pp.ui.LoginActivity;
import mx.triolabs.pp.ui.MainActivity;
import mx.triolabs.pp.util.DataAccessor;
import mx.triolabs.pp.util.Fetcher;
import mx.triolabs.pp.util.HttpVerbs;

import static mx.triolabs.pp.PPAplication.URL_GET_MY_DATA;
import static mx.triolabs.pp.PPAplication.URL_GET_MY_HISTORY;

/**
 * Created by hugomedina on 11/29/16.
 */

public class ProfileFragment extends BaseFragment {

    TextView tvUserName, tvUserWeight, tvUserHeight, tvUserGender, tvClose;
    ToggleButton tbIllnesses, tbMeds, tbBack;
    LinearLayout llIllnesses, llMeds, llPlot, llHistoric;
    LineChart chart;
    RecyclerView recyclerView;

    ProfileAdapter adapter;
    Patient patientRecords;

    String[] markers;

    View.OnClickListener plotOnclick;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View mainView = inflater.inflate(R.layout.fragment_profile, container, false);

        initComponents(mainView);

        //Track this view for GAnalytics
        String name = "Profile";
        Log.i(PPAplication.TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //Instantiates a new accessor to retrieve persistent data
        dataAccessor = new DataAccessor(getActivity());

        showProgressDialog(getActivity(), getString(R.string.system_loading));

        new Fetcher(getActivity()).fetchData(HttpVerbs.GET,
                URL_GET_MY_DATA,
                null,
                dataAccessor.getLoginCredentials(),
                new DataFetchedListener() {
                    @Override
                    public void onDataFetched(FetcherResponse data) {
                        Log.d("Dolores", data.getBody());
                        if(data.getCode() == Fetcher.SUCCESS) {
                            patientRecords = new Patient().compose(data.getBody());

                            //Sets the patient's info into the text viws
                            tvUserName.setText(patientRecords.getNombre());
                            tvUserHeight.setText(patientRecords.getEstatura() + " " + getActivity().getResources().getString(R.string.profile_height_magnitude));
                            tvUserWeight.setText(patientRecords.getPeso() + " " + getActivity().getResources().getString(R.string.profile_weight_magnitude));
                            tvUserGender.setText(patientRecords.getGenero());

                            //Creates an adapter with the data in 'Medicamentos' and 'Padecimientos' to populate the recycler view
                            adapter = new ProfileAdapter(patientRecords.getPadecimientos(), ProfileAdapter.ILLNESSES, plotOnclick);
                            recyclerView.setAdapter(adapter);

                            dismissProgressDialog();
                        }
                        else if(data.getCode() == Fetcher.NO_CONTENT){
                            dismissProgressDialog();
                            showOkDialog(getActivity(),
                                    getString(R.string.system_alert),
                                    getString(R.string.system_missing_content));
                        }
                        else if(data.getCode() == Fetcher.FAILURE){
                            dismissProgressDialog();
                            showOkDialog(getActivity(), getString(R.string.system_alert), getString(R.string.system_error));
                        }
                    }
                });

        //Fetches user's latest diagnostics to plot
        new Fetcher(getActivity()).fetchData(HttpVerbs.GET,
                URL_GET_MY_HISTORY,
                null,
                dataAccessor.getLoginCredentials(),
                new DataFetchedListener() {
                    @Override
                    public void onDataFetched(FetcherResponse data) {
                        Log.d("Dolores", data.getBody());
                        if(data.getCode() == Fetcher.SUCCESS){

                            Historial historial = new Historial().compose(data.getBody());

                            //Gets the last 10 entries in the list and creates an Entry for each
                            List<Entry> entries = new ArrayList<Entry>();
                            int index = 1;
                            int top = historial.getGlucosa().size() < 10 ? historial.getGlucosa().size() : 10;
                            markers = new String[top];
                            for(int i = 0; i < top; i++){
                                entries.add(new Entry(index, Float.valueOf(historial.getGlucosa().get(historial.getGlucosa().size() - top + i).getCantidad())));

                                //Formats the date
                                Date date=new Date(historial.getGlucosa().get(historial.getGlucosa().size() - top + i).getFecha());
                                SimpleDateFormat df2 = new SimpleDateFormat("dd - MM - yyyy");
                                markers[i] = df2.format(date);

                                index++;
                            }

                            //Creates a style data set
                            LineDataSet dataSet = new LineDataSet(entries, "Glucosa");
                            dataSet.setColor(Color.CYAN);
                            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                            //Adds the data to the chart view
                            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                            dataSets.add(dataSet);
                            dataSet.setHighlightEnabled(true);
                            dataSet.setDrawHighlightIndicators(true);
                            LineData lineData = new LineData(dataSets);

                            chart.setHighlightPerTapEnabled(true);
                            chart.setData(lineData);

                            chart.setDrawMarkers(true);
                            CustomMarkerView customMarkerView = new CustomMarkerView(getActivity(), R.layout.custom_marker_view);
                            chart.setMarker(customMarkerView);

                            chart.invalidate();

                            dismissProgressDialog();
                        }
                        else if(data.getCode() == Fetcher.NO_CONTENT){
                            dismissProgressDialog();
                            showOkDialog(getActivity(),
                                    getString(R.string.system_alert),
                                    getString(R.string.system_missing_content));
                        }
                        else {
                            dismissProgressDialog();
                            showOkDialog(getActivity(), getString(R.string.system_alert), getString(R.string.system_error));
                        }
                    }
                });

        return mainView;

    }

    /**
     * Class that renders a marker above the selected data in the chart
     */
    public class CustomMarkerView extends MarkerView {

        private TextView tvContent;

        public CustomMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            tvContent = (TextView) findViewById(R.id.tvContent);
            this.setOffset(-(getWidth() / 2), (float) (-getHeight() * 2));
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            tvContent.setText(markers[(int)e.getX() - 1]);
            super.refreshContent(e, highlight);
        }

    }

    private void initComponents(View mainView) {

        recyclerView = (RecyclerView) mainView.findViewById(R.id.profile_rv_list);
        tvUserName = (TextView) mainView.findViewById(R.id.profile_tv_name);
        tvUserWeight = (TextView) mainView.findViewById(R.id.profile_tv_weight);
        tvUserHeight = (TextView) mainView.findViewById(R.id.profile_tv_height);
        tvUserGender = (TextView) mainView.findViewById(R.id.profile_tv_gender);
        tvClose = (TextView) mainView.findViewById(R.id.profile_tv_close);
        llIllnesses = (LinearLayout) mainView.findViewById(R.id.profile_ll_illnesses);
        llMeds = (LinearLayout) mainView.findViewById(R.id.profile_ll_meds);
        llHistoric = (LinearLayout) mainView.findViewById(R.id.profile_ll_historic);
        llPlot = (LinearLayout) mainView.findViewById(R.id.profile_ll_plot);
        tbIllnesses = (ToggleButton) mainView.findViewById(R.id.profile_tb_illnesses);
        tbMeds = (ToggleButton) mainView.findViewById(R.id.profile_tb_meds);
        tbBack = (ToggleButton) mainView.findViewById(R.id.profile_tb_back);
        chart = (LineChart) mainView.findViewById(R.id.chart);

        //Hides the plot's view by default
        llPlot.setVisibility(View.GONE);

        //Hides the plot's description
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        //Hides the plot's x and right-y axis labels
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        //Initializes the basic configuration for the Recycler View
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //Shows the plot view and hides the historic data view
        plotOnclick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Sends an Action to Google Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Checked plot")
                        .build());

                llPlot.setVisibility(View.VISIBLE);
                llHistoric.setVisibility(View.GONE);
            }
        };

        //Sets transition between the 'Medicamentos' and 'Padecimientos' tabs
        tbMeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Sends an Action to Google Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Meds checked out")
                        .build());

                //Updates the buttons' style
                tbMeds.setChecked(false);
                tbIllnesses.setChecked(true);

                //Hides the current tab and shows the other
                llMeds.setVisibility(View.VISIBLE);
                llIllnesses.setVisibility(View.GONE);

                //Updates the adapter for the selected tab
                adapter = new ProfileAdapter(patientRecords.getPadecimientos(), ProfileAdapter.MEDS, plotOnclick);
                recyclerView.setAdapter(adapter);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        //Sets transition between the 'Medicamentos' and 'Padecimientos' tabs
        tbIllnesses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Sends an Action to Google Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Illnesses checked out")
                        .build());

                //Updates the buttons' style
                tbMeds.setChecked(true);
                tbIllnesses.setChecked(false);

                //Hides the current tab and shows the other
                llMeds.setVisibility(View.GONE);
                llIllnesses.setVisibility(View.VISIBLE);

                //Updates the adapter for the selected tab
                adapter = new ProfileAdapter(patientRecords.getPadecimientos(), ProfileAdapter.ILLNESSES, plotOnclick);
                recyclerView.setAdapter(adapter);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        //Hides the plot view and shows the historic data view once again
        tbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPlot.setVisibility(View.GONE);
                llHistoric.setVisibility(View.VISIBLE);

                //Updates the button's style
                tbBack.setChecked(false);
            }
        });

        //Close the current logged in session
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showActionDialog(getActivity(),
                        getResources().getString(R.string.system_alert),
                        getResources().getString(R.string.system_sure),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //Unsubscribe this client from the push notification server
                                ((PPAplication)getActivity().getApplication()).getNativePusher().unsubscribe(dataAccessor.getUserId());
                                dataAccessor.setSubscribedToChannel(false);

                                //Sends an Action to Google Analytics
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Action")
                                        .setAction("Session closed")
                                        .build());

                                //Clears the user's phone number and credentials
                                dataAccessor.clearUserData();

                                //Loads the 'Login' screen
                                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        });
            }
        });

    }

}
