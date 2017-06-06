package mx.triolabs.pp.objects.login.response;

import com.google.gson.Gson;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 12/6/16.
 */

public class FailedResponse extends BaseObject<FailedResponse> {

    private String Msg;

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public FailedResponse compose(String json) {
        return new Gson().fromJson(json, FailedResponse.class);
    }
}
