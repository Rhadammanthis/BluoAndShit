package mx.triolabs.pp.objects.tips;

import com.google.gson.Gson;

import java.util.List;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 12/7/16.
 */

public class Recomendaciones extends BaseObject<Recomendaciones> {

    private List<Tip> Tips = null;

    public List<Tip> getTips() {
        return Tips;
    }

    public void setTips(List<Tip> tips) {
        this.Tips = tips;
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public Recomendaciones compose(String json) {
        return new Gson().fromJson(json, Recomendaciones.class);
    }
}
