package com.example.testmenu.receivers;

import static com.example.testmenu.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.RemoteInput;

import com.example.testmenu.activities.ChatActivity;
import com.example.testmenu.entidades.FCMBody;
import com.example.testmenu.entidades.FCMResponse;
import com.example.testmenu.entidades.Mensaje;
import com.example.testmenu.firebase.MensajeFirebase;
import com.example.testmenu.firebase.NotificationFirebase;
import com.example.testmenu.firebase.TokenFirebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageReceiver extends BroadcastReceiver {

    String mExtraIdSender;
    String mExtraIdReceiver;
    String mExtraIdChat;
    String mExtraUsernameSender;
    String mExtraUsernameReceiver;
    String mExtraImageSender;
    String mExtraImageReceiver;
    int mExtraIdNotificaction;

    TokenFirebase mTokenFirebase;
    NotificationFirebase mNotificationFirebase;
    @Override
    public void onReceive(Context context, Intent intent) {
        mExtraIdSender = intent.getExtras().getString("idSender");
        mExtraIdReceiver = intent.getExtras().getString("idReceiver");
        mExtraIdChat = intent.getExtras().getString("idChat");

        mExtraUsernameSender = intent.getExtras().getString("usernameSender");
        mExtraUsernameReceiver = intent.getExtras().getString("usernameReceiver");
        mExtraImageSender = intent.getExtras().getString("imageSender");
        mExtraImageReceiver = intent.getExtras().getString("imageReceiver");


        mExtraIdNotificaction = intent.getExtras().getInt("idNotification");

        mTokenFirebase = new TokenFirebase();
        mNotificationFirebase = new NotificationFirebase();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mExtraIdNotificaction);

        String message = getMessageText(intent).toString();
        sendMensaje(message);
    }

    private void sendMensaje(String message) {
        final Mensaje mensaje = new Mensaje();
        mensaje.setIdChat(mExtraIdChat);
        mensaje.setIdSender(mExtraIdReceiver);
        mensaje.setIdReceiver(mExtraIdSender);
        mensaje.setTimestamp(new Date().getTime());
        mensaje.setViewed(false);
        mensaje.setIdChat(mExtraIdChat);
        mensaje.setMessage(message);

        MensajeFirebase mMensajeFirebase = new MensajeFirebase();
        mMensajeFirebase.create(mensaje).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getToken(mensaje);
            }
        });
    }

    private void getToken(final Mensaje message){
        mTokenFirebase.getToken(mExtraIdSender).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Gson gson = new Gson();
                        ArrayList<Mensaje> messages = new ArrayList<>();
                        messages.add(message);
                        String mensajes = gson.toJson(messages);
                        sendNotificaction(token,mensajes, message);
                    }
                }
            }
        });
    }

    private void sendNotificaction(final String token, String messages, Mensaje message){
        final Map<String, String> data = new HashMap<>();
        data.put("title","MENSAJE");
        data.put("body", message.getMessage());
        data.put("idNotification",String.valueOf(mExtraIdNotificaction));
        data.put("messages",messages);
        data.put("usernameSender",mExtraUsernameReceiver.toUpperCase());
        data.put("usernameReceiver",mExtraUsernameSender.toUpperCase());
        data.put("idSender",message.getIdSender());
        data.put("idReceiver",message.getIdReceiver());
        data.put("idChat",message.getIdChat());

        data.put("imageSender",mExtraImageReceiver);
        data.put("imageReceiver",mExtraImageSender);

        FCMBody body = new FCMBody(token, "high", "4500s", data);
        mNotificationFirebase.sendNotification(body).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.d("ERROR","El error fue: " + t.getMessage());
            }
        });

    }
    private CharSequence getMessageText(Intent intent){
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput!=null){
            return remoteInput.getCharSequence(NOTIFICATION_REPLY);
        }
        return null;
    }
}
