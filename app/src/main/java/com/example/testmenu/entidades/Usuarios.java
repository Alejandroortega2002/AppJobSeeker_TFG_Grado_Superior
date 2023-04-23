package com.example.testmenu.entidades;

public class Usuarios {

    private String id;
    private String email;
    private String nUsuario;
    private String telefono;

    public Usuarios(){

    }

    public Usuarios(String id, String email, String nUsuario, String telefono, String password) {
        this.id = id;
        this.email = email;
        this.nUsuario = nUsuario;
        this.telefono = telefono;
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

    public String getnUsuario() {
        return nUsuario;
    }

    public void setnUsuario(String nUsuario) {
        this.nUsuario = nUsuario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
