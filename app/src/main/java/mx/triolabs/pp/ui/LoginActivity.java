package mx.triolabs.pp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.pusher.android.notifications.interests.InterestSubscriptionChangeListener;

import mx.triolabs.pp.PPAplication;
import mx.triolabs.pp.R;
import mx.triolabs.pp.interfaces.DataFetchedListener;
import mx.triolabs.pp.objects.FetcherResponse;
import mx.triolabs.pp.objects.login.request.LoginCodeRequest;
import mx.triolabs.pp.objects.login.request.LoginRequest;
import mx.triolabs.pp.objects.login.response.FailedResponse;
import mx.triolabs.pp.objects.login.response.LoginCodeResponse;
import mx.triolabs.pp.objects.login.response.LoginResponse;
import mx.triolabs.pp.util.DataAccessor;
import mx.triolabs.pp.util.Fetcher;

import static mx.triolabs.pp.PPAplication.PUSH_NOTIFICATIONS_TAG;
import static mx.triolabs.pp.PPAplication.URL_PHONE_LOGIN;
import static mx.triolabs.pp.PPAplication.URL_SEND_ACCESS_CODE;
import static mx.triolabs.pp.util.HttpVerbs.POST;

public class LoginActivity extends BaseActivity implements DataFetchedListener{

    private String name = "LoginActivity";

    private EditText etPhoneNumber, etMessage;
    private Button bLogin;
    private String unMaskedPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Track this view for GAnalytics
        Log.i(PPAplication.TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //No title bar is set for the activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Full screen is set for the Window
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        initComponents();

        //If the user has already been signed, the system skips the Login
        if(new DataAccessor(LoginActivity.this).isUserLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        }

        //Add phone number mask
        etPhoneNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                String digits = s.toString().replaceAll("\\D", "");

                if(digits.length() <= 10){
                    if(digits.length() == 3 && !s.toString().contains("(")){
                        s.replace(0, s.length(), "(" + s.toString() + ")");
                    }
                    else if(digits.length() > 3 && digits.length() <= 6  && !s.toString().contains(" ")){
                        String areaCode = s.toString().substring(1,4);
                        String firstPart = s.toString().substring(5,s.toString().length());

                        s.replace(0, s.length(),"(" + areaCode + ") " + firstPart);
                    }
                    else if(digits.length() > 6 && digits.length() <= 9  && !s.toString().contains("-")){
                        String areaCode = s.toString().substring(1,4);
                        String firstPart = s.toString().substring(6,9);
                        String secondPart = s.toString().substring(9,s.toString().length());

                        s.replace(0, s.length(),"(" + areaCode + ") " + firstPart + "-" + secondPart);
                    }
                }

            }
        });

    }

    private void initComponents() {
        etPhoneNumber = ((EditText)findViewById(R.id.login_et_phone_number));
        etMessage = ((EditText)findViewById(R.id.login_et_message));
        bLogin = ((Button)findViewById(R.id.login_b_login));

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etMessage.getVisibility() == View.VISIBLE){

                    etMessage.setVisibility(View.GONE);
                    etPhoneNumber.setVisibility(View.VISIBLE);
                    bLogin.setText(getString(R.string.login_button));

                }
                else {

                    //Check if there's a valid phone number in the EditText
                    if (etPhoneNumber.getText().toString().isEmpty() || etPhoneNumber.getText().toString().length() < 14) {

                        etPhoneNumber.setVisibility(View.GONE);
                        etMessage.setVisibility(View.VISIBLE);
                        etMessage.setText(getString(R.string.system_no_phone));
                        bLogin.setText(getString(R.string.login_back));

                    } else {

                        showProgressDialog(LoginActivity.this, getString(R.string.system_logging_in));

                        unMaskedPhoneNumber = clearPhoneFormat(etPhoneNumber.getText().toString());

                        LoginCodeRequest loginCodeRequest = new LoginCodeRequest();
                        loginCodeRequest.setTelefono(unMaskedPhoneNumber);

                        //Call the WS to get a login code
                        new Fetcher(LoginActivity.this).fetchData(POST,
                                URL_SEND_ACCESS_CODE,
                                loginCodeRequest.serialize(),
                                null,
                                LoginActivity.this);
                    }
                }
            }
        });

    }


    @Override
    public void onDataFetched(FetcherResponse data) {

        //Check if ws returned a valid login Code
        if(data.getCode() == Fetcher.SUCCESS) {

            //If so, call the ws again now to get the user's credentials.
            final LoginCodeResponse loginCodeResponse = new Gson().fromJson(data.getBody(), LoginCodeResponse.class);
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setTelefono(unMaskedPhoneNumber);
            loginRequest.setCode(loginCodeResponse.getCode());

            new Fetcher(LoginActivity.this).fetchData(POST,
                    URL_PHONE_LOGIN,
                    loginRequest.serialize(),
                    null,
                    new DataFetchedListener() {
                        @Override
                        public void onDataFetched(FetcherResponse data) {

                            //Check if ws returned successful
                            if(data.getCode() == Fetcher.SUCCESS) {

                                LoginResponse loginResponse = new LoginResponse().compose(data.getBody());

                                //Save the user's credentials locally.
                                final DataAccessor dA = new DataAccessor(LoginActivity.this);
                                dA.setUserId(loginResponse.getId());
                                dA.setUserKey(loginResponse.getKey());
                                dA.setUserPhone(unMaskedPhoneNumber);

                                //Set the login flag to true to skip logging in
                                dA.setUserLoggedIn(true);

                                dismissProgressDialog();

                                //Calls next activity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                ((PPAplication)getApplication()).getNativePusher().subscribe(dA.getUserId(), new InterestSubscriptionChangeListener() {
                                    @Override
                                    public void onSubscriptionChangeSucceeded() {
                                        dA.setSubscribedToChannel(true);
                                        Log.d(PUSH_NOTIFICATIONS_TAG,"Success! I love donuts!");

                                    }

                                    @Override
                                    public void onSubscriptionChangeFailed(int statusCode, String response) {
                                        Log.d(PUSH_NOTIFICATIONS_TAG, ":(: received " + statusCode + " with" + response);
                                    }
                                });

                                //Sends an Action to Google Analytics
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Action")
                                        .setAction("Login")
                                        .build());

                                finish();

                            }
                            else {
                                dismissProgressDialog();

                                FailedResponse failedResponse = new FailedResponse().compose(data.getBody());

                                etPhoneNumber.setVisibility(View.GONE);
                                etMessage.setVisibility(View.VISIBLE);
                                etMessage.setText(failedResponse.getMsg());
                            }
                        }
                    });
        }
        else{

            dismissProgressDialog();

            //Processes the error response
            if(data.getCode() != Fetcher.NO_NETWORK){

                etPhoneNumber.setVisibility(View.GONE);
                etMessage.setVisibility(View.VISIBLE);
                etMessage.setText(getString(R.string.system_invalid_phone));
                bLogin.setText(getString(R.string.login_back));

                //Clears wrong phone number
                etPhoneNumber.setText("");

            }
            else if(data.getCode() == Fetcher.FAILURE){
                showOkDialog(LoginActivity.this, getString(R.string.system_alert), getString(R.string.system_error));
            }
        }
    }

    /**
     * Clears the mask out of the provided phone number
     * @param phoneNumber masked phone number
     * @return clear phone number
     */
    public String clearPhoneFormat(String phoneNumber){
        return phoneNumber.replace("(","").replace(")","").replace(" ","").replace("-","");
    }

}
