package com.example.testmenu.entidades;

public class Publicacion {

    private String id;
    private String titulo;
    private int precio;
    private String descripcion;
    private String image1;
    private String image2;
    private String idUser;
    private String categoria;

    private String sector;
    private long timeStamp;

    public Publicacion(){

    }

    public Publicacion(String titulo, int precio, String descripcion, String image1, String image2, String idUser, String categoria,String sector,long timeStamp ) {
        this.titulo = titulo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.image1 = image1;
        this.image2 = image2;
        this.idUser = idUser;
        this.categoria = categoria;
        this.sector = sector;
        this.timeStamp = timeStamp;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
