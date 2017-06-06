package mx.triolabs.pp.objects.mediciones.response;

import com.google.gson.Gson;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 12/1/16.
 */

public class MedicionesResponse extends BaseObject<MedicionesResponse>{

    private String Msg;
    private Boolean Ok;

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public Boolean getOk() {
        return Ok;
    }

    public void setOk(Boolean ok) {
        Ok = ok;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public MedicionesResponse compose(String json) {
        return new Gson().fromJson(json, MedicionesResponse.class);
    }
}
