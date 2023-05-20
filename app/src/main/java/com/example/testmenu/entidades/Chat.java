package com.example.testmenu.entidades;

import java.util.ArrayList;

public class Chat {
    private String idUser1;
    private String idUser2;
    private boolean isWriting;
    private long timmestamp;

    private String id;
    private int idNotification;

    private ArrayList<String>ids;

    public Chat(){

    }

    public Chat(String idUser1, String idUser2, boolean isWriting, long timmestamp, String id, int idNotification, ArrayList<String> ids) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.isWriting = isWriting;
        this.timmestamp = timmestamp;
        this.id = id;
        this.idNotification = idNotification;
        this.ids = ids;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }

    public String getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(String idUser1) {
        this.idUser1 = idUser1;
    }

    public String getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(String idUser2) {
        this.idUser2 = idUser2;
    }

    public boolean isWriting() {
        return isWriting;
    }

    public void setWriting(boolean writing) {
        isWriting = writing;
    }

    public long getTimmestamp() {
        return timmestamp;
    }

    public void setTimmestamp(long timmestamp) {
        this.timmestamp = timmestamp;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }
}
