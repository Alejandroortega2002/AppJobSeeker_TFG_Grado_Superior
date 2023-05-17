package com.example.testmenu.entidades;

public class Usuarios {

    private String id;
    private String usuario;
    private String email;
    private String telefono;
    private String descripcion;
    private String banner;
    private float media;
    private String fotoPerfil;
    private long timeStamp;

    private boolean online;

    private long lastConnection;


    public Usuarios() {

    }

    public Usuarios(String id, String usuario, String email, String telefono, String descripcion, String banner, float media, String fotoPerfil, long timeStamp, boolean online, long lastConnection) {
        this.id = id;
        this.usuario = usuario;
        this.email = email;
        this.telefono = telefono;
        this.descripcion = descripcion;
        this.banner = banner;
        this.media = media;
        this.fotoPerfil = fotoPerfil;
        this.timeStamp = timeStamp;
        this.online = online;
        this.lastConnection = lastConnection;
    }

    public float getMedia() {
        return media;
    }

    public void setMedia(float media) {
        this.media = media;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isOnline() {return online;}

    public void setOnline(boolean online) {this.online = online;}

    public long getLastConnection() {return lastConnection;}

    public void setLastConnection(long lastConnection) {this.lastConnection = lastConnection;}
}
