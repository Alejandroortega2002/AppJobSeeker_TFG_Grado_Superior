package com.example.testmenu.services;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import androidx.core.app.RemoteInput;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.testmenu.R;
import com.example.testmenu.channel.NotificationHelper;
import com.example.testmenu.entidades.Mensaje;
import com.example.testmenu.receivers.MessageReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    public static final String NOTIFICATION_REPLY="NotificationReply";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String, String> data = message.getData();
        String title = data.get("title");
        String body = data.get(("body"));
        if (title.equals("MENSAJE")){
            showNotificationMessage(data);
        } else {
            showNotification(title,body);

        }
    }

    private void showNotification(String title,String body){
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(title,body);
        Random random = new Random();
        int numero = random.nextInt(10000);
        notificationHelper.getmManager().notify(numero,builder.build());
    }

    private void showNotificationMessage(Map<String,String> data){
        String title =data.get("title");
        String body =data.get("body");
        String usernameSender =data.get("usernameSender");
        String usernameReceiver =data.get("usernameReceiver");
        String lastMessage =data.get("lastMessage");
        String messagesJson =data.get("messages");
        String imageSender =data.get("imageSender");
        String imageReceiver =data.get("imageReceiver");
        String idSender =data.get("idSender");
        String idReceiver =data.get("idReceiver");
        String idChat =data.get("idChat");
       final int idNotification = Integer.parseInt(data.get("idNotification"));

        Intent intent = new Intent(this, MessageReceiver.class);
        intent.putExtra("idSender",idSender);
        intent.putExtra("idReceiver",idReceiver);
        intent.putExtra("idChat",idChat);
        intent.putExtra("idNotification",idNotification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent, PendingIntent.FLAG_MUTABLE);

        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        final NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent).addRemoteInput(remoteInput)
                .build();


        Gson gson = new Gson();
        Mensaje[] mensajes = gson.fromJson(messagesJson, Mensaje[].class);

        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get()
                                .load(imageSender)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmapSender, Picasso.LoadedFrom from) {
                                        Picasso.get()
                                                .load(imageReceiver)
                                                .into(new Target() {
                                                    @Override
                                                    public void onBitmapLoaded(Bitmap bitmapReceiver, Picasso.LoadedFrom from) {
                                                        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
                                                        NotificationCompat.Builder builder = notificationHelper.getNotificationMessage(
                                                                mensajes,
                                                                usernameSender,
                                                                usernameReceiver,
                                                                lastMessage,
                                                                bitmapSender,
                                                                bitmapReceiver,
                                                                action
                                                        );
                                                        notificationHelper.getmManager().notify(idNotification,builder.build());
                                                    }

                                                    @Override
                                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                    }

                                                    @Override
                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                    }
                                                });
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    }
                });


    }
}
