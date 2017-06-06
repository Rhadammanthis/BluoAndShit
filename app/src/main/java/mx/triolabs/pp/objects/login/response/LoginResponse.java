package mx.triolabs.pp.objects.login.response;

import com.google.gson.Gson;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 11/28/16.
 */

public class LoginResponse extends BaseObject<LoginResponse>{

    public String Id;
    public String Key;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public LoginResponse compose(String json) {
        return new Gson().fromJson(json, getClass());
    }
}
