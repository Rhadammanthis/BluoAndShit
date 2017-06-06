package mx.triolabs.pp.util;

import android.content.Context;
import android.content.SharedPreferences;

import mx.triolabs.pp.objects.login.response.LoginResponse;
import mx.triolabs.pp.objects.questions.Questions;

/**
 * Created by hugomedina on 11/25/16.
 * Encapsulated class that contains methods to read or write local data
 */
public class DataAccessor {

    SharedPreferences preferences;

    /**
     * Shared preferences' keys
     */

    private final String USER_PHONE = "user_phone";
    private final String USER_ID = "user_id";
    private final String USER_KEY = "user_key";

    private final String DIAGNOSTIC_GLUCOSE = "diagnostic_glucose";
    private final String DIAGNOSTIC_OXYGEN = "diagnostic_oxygen";
    private final String DIAGNOSTIC_PULSE = "diagnostic_pulse";

    private final String IS_USER_LOGGED_IN = "user_logged_in";
    private final String LAST_ANALYSIS_RESULT = "last_analysis_result";

    private final String SUBSCRIBED_TO_CHANNEL = "subscribed_to_channel";
    private final String NEW_TIPS_AVAILABLE_NUTRITION = "new_tips_available_nutrition";
    private final String NEW_TIPS_AVAILABLE_EXERCISE = "new_tips_available_exercise";
    private final String NEW_TIPS_AVAILABLE_HEALTH = "new_tips_available_health";

    private final String TEST_CATEGORIES = "test_categories";
    private final String NEW_TEST_READY = "new_test_ready";

    /**
     * Class constructor
     * @param context
     */
    public DataAccessor(Context context){
        preferences = context.getSharedPreferences(
                "mx.triolabs.pp", Context.MODE_PRIVATE);
    }

    /**
     * Saves the user's phone number
     * @param phoneNumber
     */
    public void setUserPhone(String phoneNumber){
        preferences.edit().putString(USER_PHONE, phoneNumber).apply();
    }

    /**
     * Returns the user's phone number
     * @return
     */
    public String getUserPhone(){
        return preferences.getString(USER_PHONE, "");
    }

    /**
     * Saves the user's login id
     * @param id
     */
    public void setUserId(String id){
        preferences.edit().putString(USER_ID, id).apply();
    }

    /**
     * Returns the user's login id
     * @return
     */
    public String getUserId(){
        return preferences.getString(USER_ID, "");
    }

    /**
     * Saves the user's login key
     * @param key
     */
    public void setUserKey(String key){
        preferences.edit().putString(USER_KEY, key).apply();
    }

    /**
     * Returns the user's login key
     * @return
     */
    public String getUserKey(){
        return preferences.getString(USER_KEY, "");
    }

    /**
     * Saves the diagnostic's glucose level
     * @param glucose
     */
    public void setGlucose(String glucose){
        preferences.edit().putString(DIAGNOSTIC_GLUCOSE, glucose).apply();
    }

    /**
     * Returns the diagnostic's glucose level
     * @return
     */
    public String getGlucose(){
        return preferences.getString(DIAGNOSTIC_GLUCOSE, "");
    }

    /**
     * Saves the diagnostics oxygen level
     * @param oxygen
     */
    public void setOxygen(String oxygen){
        preferences.edit().putString(DIAGNOSTIC_OXYGEN, oxygen).apply();
    }

    /**
     * Returns the diagnostic's oxygen level
     * @return
     */
    public String getOxygen(){
        return preferences.getString(DIAGNOSTIC_OXYGEN, "");
    }

    /**
     * Saves the diagnostic's pulse
     * @param pulse
     */
    public void setPulse(String pulse){
        preferences.edit().putString(DIAGNOSTIC_PULSE, pulse).apply();
    }

    /**
     * Returns the user's login key
     * @return
     */
    public String getPulse(){
        return preferences.getString(DIAGNOSTIC_PULSE, "");
    }

    /**
     * Records if the user is logged in
     * @param isLogged
     */
    public void setUserLoggedIn(boolean isLogged){
        preferences.edit().putBoolean(IS_USER_LOGGED_IN, isLogged).apply();
    }

    /**
     * Returns if the user is logged in
     * @return
     */
    public boolean isUserLoggedIn(){
        return preferences.getBoolean(IS_USER_LOGGED_IN, false);
    }

    /**
     * Records the result of the last analysis
     * @param isLogged
     */
    public void setLastAnaylisisResult(boolean isLogged){
        preferences.edit().putBoolean(LAST_ANALYSIS_RESULT, isLogged).apply();
    }

    /**
     * Returns the result of the last analysis
     * @return
     */
    public boolean lastAnalysisResult(){
        return preferences.getBoolean(LAST_ANALYSIS_RESULT, true);
    }

    /**
     * Records which test categories have been already taken
     */
    public void setTakenTests(Questions.Types test){

        StringBuilder categories = new StringBuilder(getTakenTests());

        switch(test){
            case NUTRITION:
                categories.setCharAt(0, '1');
                break;
            case EXERCISE:
                categories.setCharAt(1, '1');
                break;
            case PREVENTION:
                categories.setCharAt(2, '1');
                break;
            case PROFILING:
                categories.setCharAt(3, '1');
                break;
        }

        preferences.edit().putString(TEST_CATEGORIES, categories.toString()).apply();
    }

    public int getAvailableTypes(){
        int count = getTakenTests().length() - getTakenTests().replace("0", "").length();
        return count;
    }

    /**
     * Returns the taken test's categories
     * @return
     */
    public String getTakenTests(){
        return preferences.getString(TEST_CATEGORIES, "0000");
    }

    /**
     * Records if there is anew test ready
     * @param ready
     */
    public void setNewTestReady(boolean ready){
        preferences.edit().putBoolean(NEW_TEST_READY, ready).apply();
    }

    /**
     * Returns if a new test is ready
     * @return
     */
    public boolean isNewTestReady(){
        return preferences.getBoolean(NEW_TEST_READY, true);
    }

    /**
     * Records if there are new tips available
     * @param type The tips type
     */
    public void setNewTipsAvailable(String type){
        switch(type){
            case "1":
                preferences.edit().putBoolean(NEW_TIPS_AVAILABLE_NUTRITION, true).apply();
                break;
            case "2":
                preferences.edit().putBoolean(NEW_TIPS_AVAILABLE_HEALTH, true).apply();
                break;
            case "3":
                preferences.edit().putBoolean(NEW_TIPS_AVAILABLE_EXERCISE, true).apply();
                break;
        }
    }

    /**
     * Clears the availability of a tip type
     * @param type The type
     */
    public void clearNewTips(String type){
        switch(type){
            case "1":
                preferences.edit().putBoolean(NEW_TIPS_AVAILABLE_NUTRITION, false).apply();
                break;
            case "2":
                preferences.edit().putBoolean(NEW_TIPS_AVAILABLE_HEALTH, false).apply();
                break;
            case "3":
                preferences.edit().putBoolean(NEW_TIPS_AVAILABLE_EXERCISE, false).apply();
                break;
        }
    }

    /**
     * Check is new nutrition tips available
     * @return
     */
    public boolean areNewNutritionTipsAvailable(){
        return preferences.getBoolean(NEW_TIPS_AVAILABLE_NUTRITION, false);
    }

    /**
     * Check is new health tips available
     * @return
     */
    public boolean areNewHealthTipsAvailable(){
        return preferences.getBoolean(NEW_TIPS_AVAILABLE_HEALTH, false);
    }

    /**
     * Check is new exercise tips available
     * @return
     */
    public boolean areNewExerciseTipsAvailable(){
        return preferences.getBoolean(NEW_TIPS_AVAILABLE_EXERCISE, false);
    }

    /**
     * Records if the user is already subscribed to the push notifications channel
     * @param subscribed
     */
    public void setSubscribedToChannel(boolean subscribed){
        preferences.edit().putBoolean(SUBSCRIBED_TO_CHANNEL, subscribed).apply();
    }

    /**
     * Check user is already subscribed to channel
     * @return
     */
    public boolean isSubscribedToChannel(){
        return preferences.getBoolean(SUBSCRIBED_TO_CHANNEL, false);
    }

    public void clearTakenTests(){
        preferences.edit().putString(TEST_CATEGORIES, "0000").apply();
    }

    public void clearDiagnosticData(){
        preferences.edit().putString(DIAGNOSTIC_GLUCOSE, "").apply();
        preferences.edit().putString(DIAGNOSTIC_OXYGEN, "").apply();
        preferences.edit().putString(DIAGNOSTIC_PULSE, "").apply();
    }

    public void clearUserData(){
        preferences.edit().putString(USER_PHONE, "").apply();
        preferences.edit().putString(USER_ID, "").apply();
        preferences.edit().putString(USER_KEY, "").apply();

        preferences.edit().putBoolean(IS_USER_LOGGED_IN, false).apply();

        preferences.edit().clear().apply();
    }

    public LoginResponse getLoginCredentials(){
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(getUserId());
        loginResponse.setKey(getUserKey());

        return loginResponse;
    }
}
