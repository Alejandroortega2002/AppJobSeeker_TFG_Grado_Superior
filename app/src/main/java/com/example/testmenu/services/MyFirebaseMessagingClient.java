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

    /**
     * Muestra una notificación en el dispositivo.
     *
     * @param title el título de la notificación
     * @param body el cuerpo o contenido de la notificación
     */
    private void showNotification(String title,String body){
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(title,body);
        Random random = new Random();
        int numero = random.nextInt(10000);
        notificationHelper.getmManager().notify(numero,builder.build());
    }

    /**
     * Muestra un mensaje de notificación utilizando los datos proporcionados.
     *
     * @param data un mapa de pares clave-valor que contiene los datos necesarios para mostrar la notificación
     */
    private void showNotificationMessage(Map<String,String> data){
        final String imageSender =data.get("imageSender");
        final String imageReceiver =data.get("imageReceiver");

        getImageSender(data,imageSender,imageReceiver);
    }

    private void getImageSender(final Map<String,String> data, final String imageSender,final String imageReceiver) {
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get()
                                .load(imageSender)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmapSender, Picasso.LoadedFrom from) {
                                        getImageReceiver(data,imageReceiver,bitmapSender);
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        getImageReceiver(data,imageReceiver,null);
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    }
                });
    }

    /**
     * Obtiene la imagen del remitente y luego llama al método para obtener la imagen del receptor.
     *
     * @param data un mapa de pares clave-valor que contiene los datos necesarios para obtener las imágenes
     * @param bitmapSender una instancia de Bitmap que representa la imagen del remitente
     * @param imageReceiver una cadena de texto que representa la URL de la imagen del receptor
     */
    private void getImageReceiver(final Map<String,String> data, String imageReceiver,Bitmap bitmapSender){
        Picasso.get()
                .load(imageReceiver)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmapReceiver, Picasso.LoadedFrom from) {
                        notifyMessage(data,bitmapSender,bitmapReceiver);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        notifyMessage(data,bitmapSender,null);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    /**
     * Notifica un mensaje con los datos proporcionados y muestra una notificación en el dispositivo.
     *
     * @param data un mapa de pares clave-valor que contiene los datos necesarios para notificar el mensaje
     * @param bitmapSender una instancia de Bitmap que representa la imagen del remitente
     * @param bitmapReceiver una instancia de Bitmap que representa la imagen del receptor
     */
    private void notifyMessage(Map<String,String> data, Bitmap bitmapSender, Bitmap bitmapReceiver){
        final String usernameSender =data.get("usernameSender");
        final String usernameReceiver =data.get("usernameReceiver");
        final String lastMessage =data.get("lastMessage");
        String messagesJson =data.get("messages");

        final String imageSender =data.get("imageSender");
        final String imageReceiver =data.get("imageReceiver");

        final String idSender =data.get("idSender");
        final String idReceiver =data.get("idReceiver");
        final String idChat =data.get("idChat");
        final int idNotification = Integer.parseInt(data.get("idNotification"));

        Intent intent = new Intent(this, MessageReceiver.class);
        intent.putExtra("idSender",idSender);
        intent.putExtra("idReceiver",idReceiver);
        intent.putExtra("idChat",idChat);
        intent.putExtra("idNotification",idNotification);
        intent.putExtra("usernameSender",usernameSender);
        intent.putExtra("usernameReceiver",usernameReceiver);
        intent.putExtra("imageSender",imageSender);
        intent.putExtra("imageReceiver",imageReceiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent, PendingIntent.FLAG_MUTABLE);

        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        final NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent).addRemoteInput(remoteInput)
                .build();


        Gson gson = new Gson();
        Mensaje[] mensajes = gson.fromJson(messagesJson, Mensaje[].class);
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
}
