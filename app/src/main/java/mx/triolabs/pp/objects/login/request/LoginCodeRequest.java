package mx.triolabs.pp.objects.login.request;

import com.google.gson.Gson;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 11/28/16.
 */

public class LoginCodeRequest extends BaseObject<LoginCodeRequest>{

    private String Telefono;

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        this.Telefono = telefono;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public LoginCodeRequest compose(String json) {
        return new Gson().fromJson(json, getClass());
    }
}
