package com.example.testmenu.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    /**
     *Crea una instancia de Retrofit para realizar llamadas a una API utilizando la URL especificada.
     <p>
     *@param url La URL base de la API.
     *@return Una instancia de Retrofit configurada con la URL base y el convertidor Gson.
     */
    public static Retrofit getClient(String url){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
