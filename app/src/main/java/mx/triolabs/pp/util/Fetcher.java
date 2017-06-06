package mx.triolabs.pp.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.util.concurrent.Callable;

import mx.triolabs.pp.PPAplication;
import mx.triolabs.pp.R;
import mx.triolabs.pp.interfaces.DataFetchedListener;
import mx.triolabs.pp.objects.FetcherResponse;
import mx.triolabs.pp.objects.login.response.FailedResponse;
import mx.triolabs.pp.objects.login.response.LoginResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hugomedina on 11/25/16.
 */

public class Fetcher {

    private static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/json; charset=utf-8");

    private String url;
    private DataFetchedListener dataFetchedListener;
    private HttpVerbs httpVerb;
    private String requestBody;
    private Context context;

    private LoginResponse headerData;

    /**
     * Http response code that symbolizes a successful request
     */
    public static int SUCCESS = 200;

    /**
     * Http response code that symbolizes a failed request
     */
    public static int FAILURE = 500;

    /**
     * No network available
     */
    public static int NO_CONTENT = 204;

    /**
     * No network available
     */
    public static int NO_NETWORK = 0;

    /**
     * Unauthorized
     */
    public static int UNAUTHORIZED = 401;

    public Fetcher(Context context){
        this.context = context;
    }

    /**
     * Sends a request to the provided URL and returns it's response
     * @param httpVerb The http verb the send the request with
     * @param url The url to send the request to
     * @param body The body of the request, provided the request is by any other verb than GET
     * @param headerData The user's login credentials
     * @param dataFetchedListener The interface to catch the response
     */
    public void fetchData(HttpVerbs httpVerb, String url, String body, LoginResponse headerData, DataFetchedListener dataFetchedListener){

        this.httpVerb = httpVerb;
        this.url = url;
        this.dataFetchedListener = dataFetchedListener;
        this.requestBody = body;
        this.headerData = headerData;

        if(PPAplication.isNetworkAvailable(context)) {

            Observable<FetcherResponse> tvShowObservable = Observable.fromCallable(new Callable<FetcherResponse>() {
                @Override
                public FetcherResponse call() throws IOException {
                    return fetchDynamicData();
                }
            });

            Subscription sub = tvShowObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Observer<FetcherResponse>() {
                                @Override
                                public void onCompleted() {
                                    Log.d("Wizard", "Completed");
                                }


                                @Override
                                public void onError(Throwable e) {
                                    Log.d("Wizard", "Error");
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(FetcherResponse response) {
                                    Log.d("Wizard", "Next");
                                    returnData(response);

                                }
                            });
        }
        else {

            new CustomDialog.Builder(context)
                    .content(context.getString(R.string.system_no_network))
                    .positiveText("Ok")
                    .onPositive(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FetcherResponse response = new FetcherResponse();
                            response.setBody("");
                            response.setCode(NO_NETWORK);

                            returnData(response);
                        }
                    })
                    .show();
        }
    }

    private void returnData(FetcherResponse response){
        dataFetchedListener.onDataFetched(response);
    }

    private FetcherResponse fetchDynamicData(){

        FetcherResponse fetcherResponse = new FetcherResponse();

        OkHttpClient client = new OkHttpClient();
        Request request = null;

        switch(httpVerb){
            case GET:

                Log.d("Maeve", "Id: " + headerData.getId());
                Log.d("Maeve", "Key: " + headerData.getKey());

                request = new Request.Builder()
                        .url(url)
                        .addHeader("id", headerData.getId())
                        .addHeader("key", headerData.getKey())
                        .get()
                        .build();
                break;
            case POST:
                if(headerData != null){
                    request = new Request.Builder()
                            .url(url)
                            .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, requestBody))
                            .addHeader("id", headerData.getId())
                            .addHeader("key", headerData.getKey())
                            .addHeader("content-type", "application/json")
                            .build();
                }
                else{
                    request = new Request.Builder()
                            .url(url)
                            .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, requestBody))
                            .addHeader("content-type", "application/json")
                            .build();
                }
                break;
        }

        Response response = null;

        try {
            response = client.newCall(request).execute();

            fetcherResponse.setCode(response.code());
            fetcherResponse.setBody(response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(response != null)
            return fetcherResponse;
        else
            return null;

    }

}
