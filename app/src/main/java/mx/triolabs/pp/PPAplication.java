package mx.triolabs.pp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.pusher.android.notifications.interests.InterestSubscriptionChangeListener;
import com.pusher.android.notifications.tokens.PushNotificationRegistrationListener;

import mx.triolabs.pp.ui.LoginActivity;
import mx.triolabs.pp.util.DataAccessor;

/**
 * Created by hugomedina on 12/6/16.
 */

public class PPAplication extends Application {

    public static final String PUSH_NOTIFICATIONS_TAG = "Bronson";
    public static final String TAG = "GAnalytics";

    //URLs to get and post data
    public static final String URL_SEND_ACCESS_CODE = "http://bluoapplication-dev.us-west-2.elasticbeanstalk.com/SendAccessCode";
    public static final String URL_PHONE_LOGIN = "http://bluoapplication-dev.us-west-2.elasticbeanstalk.com/PhoneLogin";
    public static final String URL_GET_MY_DATA = "http://bluoapplication-dev.us-west-2.elasticbeanstalk.com/GetMyData";
    public static final String URL_GET_MY_HISTORY = "http://bluoapplication-dev.us-west-2.elasticbeanstalk.com/GetMyHistory?q=";
    public static final String URL_SEND_READINGS = "http://bluoapplication-dev.us-west-2.elasticbeanstalk.com/SendReading";
    public static final String URL_GET_TIPS = "http://bluoapplication-dev.us-west-2.elasticbeanstalk.com/GetTips?tipo=";
    public static final String URL_TEST = "http://bluoapplication-dev.us-west-2.elasticbeanstalk.com/Tests";
    public static final String URL_TEST_ANSWER = "http://bluoapplication-dev.us-west-2.elasticbeanstalk.com/TestAnswer/";

    private PushNotificationRegistration nativePusher;

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;

    /**
     * Checks if there's network available
     * @param context The context
     * @return true if network available, false otherwise
     */
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        final DataAccessor accessor = new DataAccessor(this);

        //Register the app as a FCM client
        PusherAndroid pusherAndroid = new PusherAndroid(getString(R.string.pusher_app_key));
        nativePusher = pusherAndroid.nativePusher();
        try {
            nativePusher.registerFCM(this, new PushNotificationRegistrationListener() {
                @Override
                public void onSuccessfulRegistration() {
                    Log.d(PUSH_NOTIFICATIONS_TAG,"REGISTRATION SUCCESSFUL!!! YEEEEEHAWWWWW!");
                }

                @Override
                public void onFailedRegistration(int statusCode, String response) {
                    Log.d(PUSH_NOTIFICATIONS_TAG,
                            "A real sad day. Registration failed with code " + statusCode +
                                    " " + response
                    );
                }
            });
        } catch (ManifestValidator.InvalidManifestException e) {
            e.printStackTrace();
        }

        //Checks if the client is already subscribed to the Pusher interest
        if(accessor.isSubscribedToChannel()){
            Log.d(PUSH_NOTIFICATIONS_TAG, "User already subscribed to channel");
        }
        else {
            nativePusher.subscribe(accessor.getUserId(), new InterestSubscriptionChangeListener() {
                @Override
                public void onSubscriptionChangeSucceeded() {
                    accessor.setSubscribedToChannel(true);
                    Log.d(PUSH_NOTIFICATIONS_TAG,"Success! I love donuts!");

                }

                @Override
                public void onSubscriptionChangeFailed(int statusCode, String response) {
                    Log.d(PUSH_NOTIFICATIONS_TAG, ":(: received " + statusCode + " with" + response);
                }
            });
        }

        //Sets the main listener that can be fired when the app is not active
        getNativePusher().setFCMListener(new FCMPushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {

                //Show new tips only if a user is already logged in
                if(accessor.isUserLoggedIn())
                    accessor.setNewTipsAvailable(remoteMessage.getData().get("type"));
            }
        });

    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public PushNotificationRegistration getNativePusher(){
        return nativePusher;
    }

}
