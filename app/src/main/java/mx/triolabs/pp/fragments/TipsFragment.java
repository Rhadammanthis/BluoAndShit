package mx.triolabs.pp.fragments;


import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import mx.triolabs.pp.PPAplication;
import mx.triolabs.pp.R;
import mx.triolabs.pp.adapters.ProfileAdapter;
import mx.triolabs.pp.adapters.TipsAdapter;
import mx.triolabs.pp.interfaces.DataFetchedListener;
import mx.triolabs.pp.objects.FetcherResponse;
import mx.triolabs.pp.objects.misc.Tips;
import mx.triolabs.pp.objects.patient.response.Patient;
import mx.triolabs.pp.objects.tips.Recomendaciones;
import mx.triolabs.pp.objects.tips.Tip;
import mx.triolabs.pp.ui.MainActivity;
import mx.triolabs.pp.util.CustomDialog;
import mx.triolabs.pp.util.DataAccessor;
import mx.triolabs.pp.util.Fetcher;
import mx.triolabs.pp.util.HttpVerbs;

import static mx.triolabs.pp.PPAplication.URL_GET_MY_DATA;
import static mx.triolabs.pp.PPAplication.URL_GET_TIPS;

/**
 * Created by hugomedina on 11/29/16.
 */

public class TipsFragment extends BaseFragment {

    private static String TYPE = "tips_type";

    RecyclerView recyclerView;

    Tips.Types type = null;

    CustomDialog dialog;

    /**
     * Returns an instance of a TipsFragment with a Type
     * @param type The Type
     * @return A new instance of the fragment with the Type
     */
    public static Fragment newInstance(Tips.Types type){

        Fragment tipsFragment = new TipsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TYPE, type);
        tipsFragment.setArguments(bundle);

        return tipsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View mainView = inflater.inflate(R.layout.fragment_tips, container, false);

        //Track this view for GAnalytics
        String name = "Tips";
        Log.i(PPAplication.TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //Get the Type of Tips if available
        if(getArguments() != null){
            type = (Tips.Types) getArguments().getSerializable(TYPE);
        }

        //Instantiates a new accessor to retrieve persistent data
        dataAccessor = new DataAccessor(getActivity());

        //List init
        recyclerView = (RecyclerView) mainView.findViewById(R.id.tips_rv);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Log.d("Bronson", "Tips seen!");

        showProgressDialog(getActivity(), getString(R.string.system_loading));

        new Fetcher(getActivity()).fetchData(HttpVerbs.GET,
                URL_GET_TIPS + type.toString(),
                null,
                dataAccessor.getLoginCredentials(),
                new DataFetchedListener() {
                    @Override
                    public void onDataFetched(FetcherResponse data) {
                        Log.d("Dolores", data.getBody());
                        if(data.getCode() == Fetcher.SUCCESS) {

                            //An object casted from the fetch response
                            Recomendaciones recomendaciones = new Recomendaciones().compose(data.getBody());

                            //A new Adapter that holdes the Tips list and a Tips Type
                            TipsAdapter adapter = null;

                            //Reverses the list order
                            Collections.reverse(recomendaciones.getTips());

                            //Adds a Type to the adapter depending on what Type the Fragment was created with
                            switch(type){

                                case NUTRITION:
                                    adapter = new TipsAdapter(recomendaciones.getTips(), Tips.Types.NUTRITION, getActivity());
                                    break;

                                case HEALTH:
                                    adapter = new TipsAdapter(recomendaciones.getTips(), Tips.Types.HEALTH, getActivity());
                                    break;

                                case EXERCISE:
                                    adapter = new TipsAdapter(recomendaciones.getTips(), Tips.Types.EXERCISE, getActivity());
                                    break;

                            }

                            //Add line separator to list
                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                    DividerItemDecoration.VERTICAL);

                            recyclerView.addItemDecoration(dividerItemDecoration);

                            //Adds the Adapter to the RecyclerView and resets the view
                            recyclerView.setAdapter(adapter);
                            recyclerView.getAdapter().notifyDataSetChanged();

                            dismissProgressDialog();
                        }
                        //I no items were retrieved, show a dialog and return
                        else if(data.getCode() == Fetcher.NO_CONTENT){
                            dismissProgressDialog();
                            dialog = showOkDialog(getActivity(),
                                    getString(R.string.system_alert),
                                    getString(R.string.system_no_content),
                                    new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        getActivity().onBackPressed();
                                    }

                            });
                        }
                        else if(data.getCode() == Fetcher.FAILURE){
                            dismissProgressDialog();
                            showOkDialog(getActivity(), getString(R.string.system_alert), getString(R.string.system_error));
                        }
                    }
                });

        return mainView;

    }

}
