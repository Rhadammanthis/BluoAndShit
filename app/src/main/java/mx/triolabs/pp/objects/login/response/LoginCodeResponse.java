package mx.triolabs.pp.objects.login.response;

import com.google.gson.Gson;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 11/28/16.
 */

public class LoginCodeResponse extends BaseObject<LoginCodeResponse> {

    private String Code;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public LoginCodeResponse compose(String json) {
        return new Gson().fromJson(json, getClass());
    }
}
