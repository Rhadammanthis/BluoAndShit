package mx.triolabs.pp.objects.historial.response;

/**
 * Created by hugomedina on 11/30/16.
 */

public class Presion {

    private String CantidadAlta;
    private String CantidadBaja;
    private String Pulso;
    private Long Fecha;

    public String getPulso() {
        return Pulso;
    }

    public void setPulso(String pulso) {
        this.Pulso = pulso;
    }

    public String getCantidadAlta() {
        return CantidadAlta;
    }

    public void setCantidadAlta(String cantidadAlta) {
        this.CantidadAlta = cantidadAlta;
    }

    public String getCantidadBaja() {
        return CantidadBaja;
    }

    public void setCantidadBaja(String cantidadBaja) {
        this.CantidadBaja = cantidadBaja;
    }

    public Long getFecha() {
        return Fecha;
    }

    public void setFecha(Long fecha) {
        this.Fecha = fecha;
    }

}
