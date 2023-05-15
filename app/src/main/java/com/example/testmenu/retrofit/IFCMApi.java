package com.example.testmenu.retrofit;

import com.example.testmenu.entidades.FCMBody;
import com.example.testmenu.entidades.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
       "Content-Type:application/json",
       "Authorization:key=AAAAalL3QX0:APA91bEVmwxvL6c5nlqiaPyvsyQCTS3ReHGhUXlhwvGUjSaEzFaEBmKYdWVEBSRfb7Od4k6SNgQULBINHoRtUqTqT3pELhi0lJFU0lmLuCB9C5OjWMXl-ZB2M-unSutOKUDdWC5ZCxj6"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
