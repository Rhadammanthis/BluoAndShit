package mx.triolabs.pp.objects.historial.response;

/**
 * Created by hugomedina on 11/30/16.
 */

public class Oxigeno {

    private String Cantidad;
    private Long Fecha;

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String cantidad) {
        this.Cantidad = cantidad;
    }

    public Long getFecha() {
        return Fecha;
    }

    public void setFecha(Long fecha) {
        this.Fecha = fecha;
    }

}
