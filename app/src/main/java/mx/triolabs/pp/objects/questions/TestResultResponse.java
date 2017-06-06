package mx.triolabs.pp.objects.questions;

import com.google.gson.Gson;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 12/9/16.
 */

public class TestResultResponse extends BaseObject<TestResultResponse>{

    private String Msg;
    private String Ok;

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        this.Msg = msg;
    }

    public String getOk() {
        return Ok;
    }

    public void setOk(String ok) {
        this.Ok = ok;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public TestResultResponse compose(String json) {
        return new Gson().fromJson(json, TestResultResponse.class);
    }
}
