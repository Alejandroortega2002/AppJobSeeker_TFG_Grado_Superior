package com.example.testmenu.services;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.testmenu.channel.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {


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
            int idNotification = Integer.parseInt(data.get("idNotification"));

            showNotificationMessage(title,body, idNotification);
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

    private void showNotificationMessage(String title,String body,int idNotificationChat){
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(title,body);
        notificationHelper.getmManager().notify(idNotificationChat,builder.build());
    }
}
