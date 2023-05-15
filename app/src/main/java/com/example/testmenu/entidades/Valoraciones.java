package com.example.testmenu.entidades;

public class Valoraciones {
    String id, userId, valoracion, nota, timeStamp;

    public Valoraciones() {

    }

    public Valoraciones(String id,String userId,String valoracion, String nota, String timeStamp) {
        this.id = id;
        this.userId = userId;
        this.valoracion = valoracion;
        this.nota = nota;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
