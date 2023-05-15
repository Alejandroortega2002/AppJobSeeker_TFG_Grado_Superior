package com.example.testmenu.firebase;

import com.example.testmenu.entidades.FCMBody;
import com.example.testmenu.entidades.FCMResponse;
import com.example.testmenu.retrofit.IFCMApi;
import com.example.testmenu.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationFirebase {
    private String url = "https://fcm.googleapis.com";

    public NotificationFirebase(){

    }

    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
