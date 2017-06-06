package mx.triolabs.pp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.Hashtable;
import java.util.List;

import mx.triolabs.pp.PPAplication;
import mx.triolabs.pp.R;
import mx.triolabs.pp.interfaces.DataFetchedListener;
import mx.triolabs.pp.objects.FetcherResponse;
import mx.triolabs.pp.objects.misc.Tips;
import mx.triolabs.pp.objects.questions.BaseQuestion;
import mx.triolabs.pp.objects.questions.Pregunta;
import mx.triolabs.pp.objects.questions.Questions;
import mx.triolabs.pp.objects.questions.Respuesta;
import mx.triolabs.pp.objects.questions.TestResult;
import mx.triolabs.pp.objects.questions.TestResultResponse;
import mx.triolabs.pp.ui.MainActivity;
import mx.triolabs.pp.util.DataAccessor;
import mx.triolabs.pp.util.Fetcher;
import mx.triolabs.pp.util.HttpVerbs;
import mx.triolabs.pp.util.notification.NotificationEventReceiver;

import static mx.triolabs.pp.PPAplication.URL_TEST;
import static mx.triolabs.pp.PPAplication.URL_TEST_ANSWER;
import static mx.triolabs.pp.fragments.StatusFragment.TIPS_FRAGMENT;
import static mx.triolabs.pp.ui.MainActivity.STARTED_FROM_NOTIFICATION;
import static mx.triolabs.pp.util.Fetcher.SUCCESS;

/**
 * Created by hugomedina on 11/29/16.
 */

public class TestFragment extends BaseFragment {

    final int sdk = android.os.Build.VERSION.SDK_INT;

    private LinearLayout llYes, llNo, llReady, llQuestion;
    private RelativeLayout rlTest, rlNoTest, rlResult;
    private ImageButton ibNext;
    private ImageView ivYes, ivNo, ivGlobe, ivResult;
    private ImageButton ibHealth, ibExercise, ibNutrition, ibHealthNotif, ibExerciseNotif, ibNutritionNotif;
    private TextView tvQuestion;
    private Button bYes;

    //Checks wether the 'Yes' or 'No' button is selected
    private boolean yesClicked = false, noClicked = false;

    //Question list iterator
    private int questionIndex = 0;

    private Questions.Types testType;
    List<Pregunta> questionList = null;
    TestResult testResult = new TestResult();
    TestResultResponse testResultResponse;

    //Hash table that hold the relation between a test type and the image resources
    Hashtable<String, Integer> globeStyle = new Hashtable<String, Integer>();
    Hashtable<String, Integer> yesStyle = new Hashtable<String, Integer>();
    Hashtable<String, Integer> noStyle = new Hashtable<String, Integer>();
    Hashtable<String, Integer> resultCharacter = new Hashtable<String, Integer>();
    Hashtable<Boolean, Integer> healthTips = new Hashtable<Boolean, Integer>();
    Hashtable<Boolean, Integer> nutritionTips = new Hashtable<Boolean, Integer>();
    Hashtable<Boolean, Integer> exerciseTips = new Hashtable<Boolean, Integer>();

    public static TestFragment newInstance(boolean fromNotif){
        TestFragment fragment = new TestFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(STARTED_FROM_NOTIFICATION, fromNotif);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View mainView = inflater.inflate(R.layout.fragment_test, container, false);

        iniComponents(mainView);

        //Track this view for GAnalytics
        String name = "Test";
        Log.i(PPAplication.TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

//        dataAccessor.clearTakenTests();

        //If no new test available, show message
        if(dataAccessor.isNewTestReady())
            initTest();
        else {
            rlTest.setVisibility(View.GONE);
            rlResult.setVisibility(View.GONE);
            rlNoTest.setVisibility(View.VISIBLE);
        }

        return mainView;
    }

    /**
     * Fetches a new available test and initializes all system variables
     */
    public void initTest(){

        questionIndex = 0;

        //Initializes UI elements
        yesClicked = false;
        noClicked = false;

        llNo.setBackground(null);
        llYes.setBackground(null);

        ibNext.setVisibility(View.GONE);

        //If the fragment was instantiated from the notification, we don't need another progress dialog
        if(getArguments() != null){
            if(!getArguments().getBoolean(STARTED_FROM_NOTIFICATION))
                showProgressDialog(getActivity(), getString(R.string.system_loading));

            //The extras get removed
            getActivity().getIntent().removeExtra(STARTED_FROM_NOTIFICATION);
        }
        else
            showProgressDialog(getActivity(), getString(R.string.system_loading));

        //Fetches available tests from the WS
        new Fetcher(getActivity()).fetchData(HttpVerbs.GET,
                URL_TEST,
                null,
                dataAccessor.getLoginCredentials(),
                new DataFetchedListener() {
                    @Override
                    public void onDataFetched(FetcherResponse data) {
                        if(data.getCode() == SUCCESS){
                            Questions questions = new Questions().compose(data.getBody());

                            //Gets a random list of available questions
                            testType = questions.getRandomAvailableType(dataAccessor.getTakenTests());

                            //Checks if there is no more available tests
                            if(testType != null)
                            {

                                //Loads the questions list and sets the first one
                                questionList = questions.getRandomTestList(testType);
                                tvQuestion.setText(questionList.get(questionIndex).getPregunta());

                                //Updates the button's image resource according to the test's type
                                if(!questionList.get(questionIndex).isSi()){
                                    ivYes.setImageResource(noStyle.get(testType.toString()));
                                    ivNo.setImageResource(yesStyle.get(testType.toString()));
                                }
                                else{
                                    ivYes.setImageResource(yesStyle.get(testType.toString()));
                                    ivNo.setImageResource(noStyle.get(testType.toString()));
                                }

                                dismissProgressDialog();
                            }

                        }
                        else if(data.getCode() == Fetcher.FAILURE){
                            showOkDialog(getActivity(), getString(R.string.system_alert), getString(R.string.system_error));
                        }
                    }
                });

    }

    private void iniComponents(View main) {

        llYes = (LinearLayout) main.findViewById(R.id.test_ll_yes);
        llNo = (LinearLayout) main.findViewById(R.id.test_ll_no);
        llReady = (LinearLayout) main.findViewById(R.id.test_ll_ready);
        llQuestion = (LinearLayout) main.findViewById(R.id.test_ll_question);
        ibNext = (ImageButton) main.findViewById(R.id.test_ib_next);
        tvQuestion = (TextView) main.findViewById(R.id.test_tv_question);
        ivYes = (ImageView) main.findViewById(R.id.test_iv_yes);
        ivNo = (ImageView) main.findViewById(R.id.test_iv_no);
        ivGlobe = (ImageView) main.findViewById(R.id.test_iv_globe);
        ivResult = (ImageView) main.findViewById(R.id.test_iv_result);
        bYes = (Button) main.findViewById(R.id.test_b_yes);
        rlTest = (RelativeLayout) main.findViewById(R.id.test_rl_test);
        rlNoTest = (RelativeLayout) main.findViewById(R.id.test_rl_waiting);
        rlResult = (RelativeLayout) main.findViewById(R.id.test_rl_result);
        ibHealth = (ImageButton) main.findViewById(R.id.test_ib_health);
        ibExercise = (ImageButton) main.findViewById(R.id.test_ib_exercise);
        ibNutrition = (ImageButton) main.findViewById(R.id.test_ib_nutrition);
        ibHealthNotif = (ImageButton) main.findViewById(R.id.test_ib_health_notif);
        ibExerciseNotif = (ImageButton) main.findViewById(R.id.test_ib_exercise_notif);
        ibNutritionNotif = (ImageButton) main.findViewById(R.id.test_ib_nutrition_notif);

        rlNoTest.setVisibility(View.GONE);
        rlResult.setVisibility(View.GONE);

        //Hashes the resources to style the text globe
        globeStyle.put(Questions.Types.EXERCISE.toString(), R.drawable.a_chat_1);
        globeStyle.put(Questions.Types.NUTRITION.toString(), R.drawable.a_chat_2);
        globeStyle.put(Questions.Types.PROFILING.toString(), R.drawable.a_chat_3);
        globeStyle.put(Questions.Types.PREVENTION.toString(), R.drawable.a_chat_4);

        //Hashes the resources to style the 'Yes' button
        yesStyle.put(Questions.Types.EXERCISE.toString(), R.drawable.a_bien_1);
        yesStyle.put(Questions.Types.NUTRITION.toString(), R.drawable.a_bien_2);
        yesStyle.put(Questions.Types.PROFILING.toString(), R.drawable.a_bien_3);
        yesStyle.put(Questions.Types.PREVENTION.toString(), R.drawable.a_bien_4);

        //Hashes the resources to style the 'Yes' button
        noStyle.put(Questions.Types.EXERCISE.toString(), R.drawable.a_mal_1);
        noStyle.put(Questions.Types.NUTRITION.toString(), R.drawable.a_mal_2);
        noStyle.put(Questions.Types.PROFILING.toString(), R.drawable.a_mal_3);
        noStyle.put(Questions.Types.PREVENTION.toString(), R.drawable.a_mal_4);

        //Hashes the resources to set the correct result Bluo character
        resultCharacter.put("0", R.drawable.a_personaje_ok);
        resultCharacter.put("1", R.drawable.a_personaje_regular);
        resultCharacter.put("2", R.drawable.a_personaje_alerta);

        //Hashes the resources to signal if there are new tips available
        nutritionTips.put(false, R.drawable.a_alimentacion);
        nutritionTips.put(true, R.drawable.a_alimentacion_noti);
        exerciseTips.put(false, R.drawable.a_ejercicios);
        exerciseTips.put(true, R.drawable.a_ejercicios_noti);
        healthTips.put(false, R.drawable.a_salud);
        healthTips.put(true, R.drawable.a_salud_noti);



        //Loads the main test view
        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llReady.setVisibility(View.GONE);
                llQuestion.setVisibility(View.VISIBLE);

                //Updates the dialog globe's image resource according to the test's type
                ivGlobe.setImageResource(globeStyle.get(testType.toString()));

                //Sends an Action to Google Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("New test started")
                        .build());
            }
        });

        //Loads another question
        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionIndex++;

                //Cheks if we've reached the end of the test
                if(questionIndex >= questionList.size()){

                    //A new Respuesta object that'll be added to the list
                    Respuesta respuesta = new Respuesta();
                    respuesta.setPregunta(String.valueOf(questionIndex));
                    if(yesClicked)
                        respuesta.setRespuesta(true);
                    if(noClicked)
                        respuesta.setRespuesta(false);
                    testResult.getRespuestas().add(respuesta);

                    showProgressDialog(getActivity(), getString(R.string.test_sending));

                    new Fetcher(getActivity()).fetchData(HttpVerbs.POST,
                            URL_TEST_ANSWER + testType.toString(),
                            testResult.serialize(),
                            dataAccessor.getLoginCredentials(),
                            new DataFetchedListener() {
                                @Override
                                public void onDataFetched(FetcherResponse data) {

                                    dismissProgressDialog();

                                    if(data.getCode() == SUCCESS){

                                        Log.d("Dolores", data.getBody());
                                        testResultResponse = new TestResultResponse().compose(data.getBody());

                                        //Hide test related views and show the result character
                                        rlTest.setVisibility(View.GONE);
                                        ibNext.setVisibility(View.GONE);
                                        rlResult.setVisibility(View.VISIBLE);
                                        ivResult.setImageResource(resultCharacter.get(testResultResponse.getOk()));

                                        //Start notification alarm
                                        NotificationEventReceiver.stopAlarm(getActivity().getApplicationContext());
                                        NotificationEventReceiver.setupAlarm(getActivity().getApplicationContext());

                                        //Signals that there's no new test ready
                                        dataAccessor.setNewTestReady(false);

                                        dataAccessor.setTakenTests(testType);

                                        //Sends an Action to Google Analytics
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Action")
                                                .setAction("Test completed")
                                                .build());

                                        //If all tests have been taken, the alarm that sends Notifications is stopped
                                        if(dataAccessor.getAvailableTypes() == 0){
                                            Log.d("Ravioli", "Stopped");

                                            dataAccessor.clearTakenTests();

                                            NotificationEventReceiver.stopAlarm(getActivity().getApplicationContext());
                                            NotificationEventReceiver.setupAlarm(getActivity().getApplicationContext());

                                            Log.d("Ravioli", "Started again clean with " + String.valueOf(dataAccessor.getAvailableTypes()) + " available tests");

                                            //Sends an Action to Google Analytics
                                            mTracker.send(new HitBuilders.EventBuilder()
                                                    .setCategory("Action")
                                                    .setAction("All test completed")
                                                    .build());
                                        }
                                    }

                                }
                            });

                }
                else {

                    //A new Respuesta object that'll be added to the list
                    Respuesta respuesta = new Respuesta();
                    respuesta.setPregunta(String.valueOf(questionIndex));
                    if(yesClicked)
                        respuesta.setRespuesta(true);
                    if(noClicked)
                        respuesta.setRespuesta(false);
                    testResult.getRespuestas().add(respuesta);

                    //Loads the next question into the TextView
                    tvQuestion.setText(questionList.get(questionIndex).getPregunta());

                    //Resets the answer buttons
                    yesClicked = false;
                    noClicked = false;
                    llNo.setBackground(null);
                    llYes.setBackground(null);
                    ibNext.setVisibility(View.GONE);

                    //Changes the ImageView according to style
                    if(!questionList.get(questionIndex).isSi()){
                        ivYes.setImageResource(noStyle.get(testType.toString()));
                        ivNo.setImageResource(yesStyle.get(testType.toString()));
                    }
                    else{
                        ivYes.setImageResource(yesStyle.get(testType.toString()));
                        ivNo.setImageResource(noStyle.get(testType.toString()));
                    }

                }
            }
        });

        //Updated the button's style
        llYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Handles the logic to apply the 'selected' appearance to the view
                if(!yesClicked) {

                    //Changes the layout's background with the best method according to the SDk version
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.test_answer_background));
                    } else {
                        view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.test_answer_background));
                    }

                    //Sets global flag to inform that the 'Yes' button was selected
                    yesClicked = true;

                    //Clears the style out of the other button
                    if(noClicked){
                        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            llNo.setBackgroundDrawable(null);
                        } else {
                            llNo.setBackground(null);
                        }

                        noClicked = false;
                    }
                }
                else{

                    //Clears this button's style
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(null);
                    } else {
                        view.setBackground(null);
                    }

                    yesClicked = false;
                }

                //Checks if the 'Next' button should appear
                if(yesClicked || noClicked)
                    ibNext.setVisibility(View.VISIBLE);
                else
                    ibNext.setVisibility(View.GONE);
            }
        });

        //Updated the button's style
        llNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Handles the logic to apply the 'selected' appearance to the view
                if(!noClicked) {

                    //Changes the layout's background with the best method according to the SDk version
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.test_answer_background));
                    } else {
                        view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.test_answer_background));
                    }

                    //Sets global flag to inform that the 'No' button was selected
                    noClicked = true;

                    //Clears the style out of the other button
                    if(yesClicked){
                        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            llYes.setBackgroundDrawable(null);
                        } else {
                            llYes.setBackground(null);
                        }

                        yesClicked = false;
                    }

                }
                else{

                    //Clears this button's style
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(null);
                    } else {
                        view.setBackground(null);
                    }

                    noClicked = false;
                }

                //Checks if the 'Next' button should appear
                if(yesClicked || noClicked)
                    ibNext.setVisibility(View.VISIBLE);
                else
                    ibNext.setVisibility(View.GONE);
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

        //Instantiates a new accessor to retrieve persistent data
        dataAccessor = new DataAccessor(getActivity());

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

}
