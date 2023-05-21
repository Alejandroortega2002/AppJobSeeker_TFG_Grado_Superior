package com.example.testmenu.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Mensaje;

import java.util.Date;

public class NotificationHelper extends ContextWrapper {
    private static final String CHANNEL_ID = "com.example.testmenu";
    private static final String CHANNEL_NAME = "com.example.TestMenu";

    private NotificationManager manager;

    public NotificationHelper(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getmManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getmManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNotification(String title, String body){
        return  new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setColor(Color.GRAY)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body)
                .setBigContentTitle(title)
        );
    }

    public NotificationCompat.Builder getNotificationMessage(
            Mensaje[] mensajes,
            String usernameSender,
            String usernameReceiver,
            String lastMessage,
            Bitmap bitmapSender,
            Bitmap bitmapReceiver,
            NotificationCompat.Action action
    ){
        Person person1 = new Person.Builder()
                .setName(usernameReceiver)
                .setIcon(IconCompat.createWithBitmap(bitmapReceiver))
                .build();

        Person person2 = new Person.Builder()
                .setName(usernameSender)
                .setIcon(IconCompat.createWithBitmap(bitmapSender))
                .build();

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person1);
        NotificationCompat.MessagingStyle.Message message1 =
                new NotificationCompat.MessagingStyle.Message(
                        lastMessage,
                        new Date().getTime(),
                        person1
                );

        messagingStyle.addMessage(message1);
        for (Mensaje m: mensajes){
            NotificationCompat.MessagingStyle.Message message2 =
                    new NotificationCompat.MessagingStyle.Message(
                            m.getMessage(),
                            m.getTimestamp(),
                            person2
                    );
            messagingStyle.addMessage(message2);

        }
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(messagingStyle)
                .addAction(action);

    }
}
