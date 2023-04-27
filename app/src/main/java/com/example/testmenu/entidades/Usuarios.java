package com.example.testmenu.entidades;

public class Usuarios {

    private String id;
    private String usuario;
    private String email;
    private String telefono;
    private long timeStamp;

    public Usuarios() {

    }

    public Usuarios(String id, String email, String usuario, String telefono, long timeStamp) {
        this.id = id;
        this.usuario = usuario;
        this.email = email;
        this.telefono = telefono;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
