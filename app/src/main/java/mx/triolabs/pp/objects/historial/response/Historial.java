package mx.triolabs.pp.objects.historial.response;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.triolabs.pp.objects.BaseObject;

/**
 * Created by hugomedina on 11/30/16.
 */

public class Historial extends BaseObject<Historial>{

    private List<Glucosa> Glucosa = new ArrayList<Glucosa>();
    private List<Oxigeno> Oxigeno = new ArrayList<Oxigeno>();
    private List<Presion> Presion = new ArrayList<Presion>();

    public List<mx.triolabs.pp.objects.historial.response.Glucosa> getGlucosa() {
        return Glucosa;
    }

    public void setGlucosa(List<mx.triolabs.pp.objects.historial.response.Glucosa> glucosa) {
        Glucosa = glucosa;
    }

    public List<mx.triolabs.pp.objects.historial.response.Oxigeno> getOxigeno() {
        return Oxigeno;
    }

    public void setOxigeno(List<mx.triolabs.pp.objects.historial.response.Oxigeno> oxigeno) {
        Oxigeno = oxigeno;
    }

    public List<mx.triolabs.pp.objects.historial.response.Presion> getPresion() {
        return Presion;
    }

    public void setPresion(List<mx.triolabs.pp.objects.historial.response.Presion> presion) {
        Presion = presion;
    }

    public String getLatestDate(){

        Long latest = 0l;

        for(Glucosa temp : Glucosa){
            if(temp.getFecha() > latest)
                latest = temp.getFecha();
        }

        Date date=new Date(latest);
        SimpleDateFormat df2 = new SimpleDateFormat("dd - MM - yyyy");

        return df2.format(date);
    }

    public String getDayOfLastDisgnostic(){
        Long latest = 0l;

        for(Glucosa temp : Glucosa){
            if(temp.getFecha() > latest)
                latest = temp.getFecha();
        }

        Date date=new Date(latest);
        SimpleDateFormat df2 = new SimpleDateFormat("dd");

        return df2.format(date);
    }

    @Override
    public String serialize() {
        return objectToJson(this);
    }

    @Override
    public Historial compose(String json) {
        return new Gson().fromJson(json, getClass());
    }
}
