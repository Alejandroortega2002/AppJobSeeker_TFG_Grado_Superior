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
        // Llamar al método onMessageReceived() de la clase padre
        super.onMessageReceived(message);

        // Obtener los datos de la notificación
        Map<String, String> data = message.getData();

        // Obtener el título y el cuerpo de la notificación a partir de los datos
        String title = data.get("title");
        String body = data.get("body");

        // Verificar si el título es "MENSAJE"
        if (title.equals("MENSAJE")) {
            // Mostrar una notificación personalizada utilizando los datos recibidos
            showNotificationMessage(data);
        } else {
            // Mostrar una notificación estándar con el título y cuerpo recibidos
            showNotification(title, body);
        }
    }

    /**
     * Muestra una notificación en el dispositivo.
     *
     * @param title el título de la notificación
     * @param body el cuerpo o contenido de la notificación
     */
    public void showNotification(String title, String body) {
        // Crear una instancia de NotificationHelper utilizando el contexto de la base
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());

        // Obtener un builder de notificación utilizando el título y el cuerpo proporcionados
        NotificationCompat.Builder builder = notificationHelper.getNotification(title, body);

        // Generar un número aleatorio para identificar la notificación
        Random random = new Random();
        int numero = random.nextInt(10000);

        // Notificar la notificación utilizando el NotificationManager de NotificationHelper
        notificationHelper.getmManager().notify(numero, builder.build());
    }

    /**
     * Muestra un mensaje de notificación utilizando los datos proporcionados.
     *
     * @param data un mapa de pares clave-valor que contiene los datos necesarios para mostrar la notificación
     */
    public void showNotificationMessage(Map<String, String> data) {
        // Obtiene los valores de "imageSender" y "imageReceiver" del mapa de datos
        final String imageSender = data.get("imageSender");
        final String imageReceiver = data.get("imageReceiver");

        // Llama al método getImageSender() pasando los datos y las imágenes obtenidas
        getImageSender(data, imageSender, imageReceiver);
    }

    public void getImageSender(final Map<String, String> data, final String imageSender, final String imageReceiver) {
        // Crea un nuevo Handler para realizar operaciones en el hilo principal
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // Carga la imagen del remitente utilizando Picasso
                Picasso.get().load(imageSender).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmapSender, Picasso.LoadedFrom from) {
                        // Si la carga de la imagen del remitente es exitosa, llama al método getImageReceiver()
                        // pasando los datos, la imagen del receptor y la imagen del remitente
                        getImageReceiver(data, imageReceiver, bitmapSender);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        // Si la carga de la imagen del remitente falla, llama al método getImageReceiver()
                        // pasando los datos, la imagen del receptor y nulo como imagen del remitente
                        getImageReceiver(data, imageReceiver, null);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Este método se llama cuando se está preparando la carga de la imagen,
                        // pero no se realiza ninguna acción en este caso
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
    public void getImageReceiver(final Map<String, String> data, String imageReceiver, Bitmap bitmapSender) {
        // Carga la imagen del receptor utilizando Picasso
        Picasso.get().load(imageReceiver).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmapReceiver, Picasso.LoadedFrom from) {
                // Si la carga de la imagen del receptor es exitosa, llama al método notifyMessage()
                // pasando los datos, la imagen del remitente y la imagen del receptor cargadas
                notifyMessage(data, bitmapSender, bitmapReceiver);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Si la carga de la imagen del receptor falla, llama al método notifyMessage()
                // pasando los datos, la imagen del remitente y nulo como imagen del receptor
                notifyMessage(data, bitmapSender, null);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Este método se llama cuando se está preparando la carga de la imagen,
                // pero no se realiza ninguna acción en este caso
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
    public void notifyMessage(Map<String, String> data, Bitmap bitmapSender, Bitmap bitmapReceiver) {
        // Obtiene los valores de "usernameSender", "usernameReceiver" y "lastMessage" del mapa de datos
        final String usernameSender = data.get("usernameSender");
        final String usernameReceiver = data.get("usernameReceiver");
        final String lastMessage = data.get("lastMessage");

        // Obtiene el valor de "messages" del mapa de datos
        String messagesJson = data.get("messages");

        // Obtiene los valores de "imageSender" e "imageReceiver" del mapa de datos
        final String imageSender = data.get("imageSender");
        final String imageReceiver = data.get("imageReceiver");

        // Obtiene los valores de "idSender", "idReceiver", "idChat" e "idNotification" del mapa de datos
        final String idSender = data.get("idSender");
        final String idReceiver = data.get("idReceiver");
        final String idChat = data.get("idChat");
        final int idNotification = Integer.parseInt(data.get("idNotification"));

        // Crea un intent para enviar a MessageReceiver
        Intent intent = new Intent(this, MessageReceiver.class);
        // Agrega los datos adicionales al intent
        intent.putExtra("idSender", idSender);
        intent.putExtra("idReceiver", idReceiver);
        intent.putExtra("idChat", idChat);
        intent.putExtra("idNotification", idNotification);
        intent.putExtra("usernameSender", usernameSender);
        intent.putExtra("usernameReceiver", usernameReceiver);
        intent.putExtra("imageSender", imageSender);
        intent.putExtra("imageReceiver", imageReceiver);

        // Crea un PendingIntent para el broadcast
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_MUTABLE);

        // Crea un RemoteInput para permitir respuestas directamente desde la notificación
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        // Crea una acción de notificación que permite responder
        final NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent).addRemoteInput(remoteInput)
                .build();

        // Convierte el JSON de mensajes a un arreglo de objetos Mensaje utilizando Gson
        Gson gson = new Gson();
        Mensaje[] mensajes = gson.fromJson(messagesJson, Mensaje[].class);

        // Crea una instancia de NotificationHelper
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());

        // Obtiene un Builder de NotificationCompat a través de NotificationHelper
        NotificationCompat.Builder builder = notificationHelper.getNotificationMessage(
                mensajes,
                usernameSender,
                usernameReceiver,
                lastMessage,
                bitmapSender,
                bitmapReceiver,
                action
        );

        // Notifica la notificación utilizando el NotificationManager de NotificationHelper
        notificationHelper.getmManager().notify(idNotification, builder.build());
    }
}
