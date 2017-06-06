package mx.triolabs.pp.objects.login.request;

import com.google.gson.Gson;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 11/28/16.
 */

public class LoginRequest extends BaseObject<LoginRequest>{

    private String Telefono;
    private String Code;

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        this.Telefono = telefono;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public LoginRequest compose(String json) {
        return new Gson().fromJson(json, getClass());
    }
}
