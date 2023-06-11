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

    private String mExtraIdSender;
    private String mExtraIdReceiver;
    private String mExtraIdChat;
    private String mExtraUsernameSender;
    private String mExtraUsernameReceiver;
    private String mExtraImageSender;
    private String mExtraImageReceiver;
    private int mExtraIdNotificaction;
    private TokenFirebase mTokenFirebase;
    private NotificationFirebase mNotificationFirebase;
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

    /**
     * Crea un objeto de entidad <b>Mensaje</b> y se obtiene el token del objeto.
     * <p>
     * Se instancia un objeto de entidad Mensaje y se le asigna sus datos.
     * Se instancia un objeto de clase <b>MensajeFirebase</b> en el que mandamos crear a la base de datos un documento del nuevo objeto.
     * En el caso de que se cumpla, se llama al método <b>getToken()</b>
     *
     * @param message el texto que se evnia
     * @return void
     */
    public void sendMensaje(String message) {
        Mensaje mensaje = new Mensaje();
        mensaje.setIdChat(mExtraIdChat);
        mensaje.setIdSender(mExtraIdSender);
        mensaje.setIdReceiver(mExtraIdReceiver);
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

    /**
     * Se consulta del token del mensaje creado y se envia una notificacion
     * <p>
     * Se realiza una consulta a la base de datos. Se revisa si existe el documento con el token filtrado por el id del user que envia el mensaje.
     * En caso de que exista, se obtienen los últimos mensajes y se guardan en un array para luego convertirlos a un formato Json con la clase <b>Gson</b>.
     * Luego, se llama al método <b>sendNotification()</b>
     *
     * @param message objeto mensaje del que se ha creado
     * @return void
     *
     */
    public void getToken( Mensaje message) {
        // Obtener el token del destinatario a través de la instancia mTokenFirebase
        mTokenFirebase.getToken(mExtraIdSender).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        // Obtener el token del documento DocumentSnapshot
                        String token = documentSnapshot.getString("token");

                        // Crear una instancia de Gson para convertir el objeto Mensaje en formato JSON
                        Gson gson = new Gson();

                        // Crear una lista de Mensaje y agregar el mensaje proporcionado
                        ArrayList<Mensaje> messages = new ArrayList<>();
                        messages.add(message);

                        // Convertir la lista de mensajes a formato JSON
                        String mensajes = gson.toJson(messages);

                        // Llamar al método sendNotificaction() para enviar la notificación
                        sendNotificaction(token, mensajes, message);
                    }
                }
            }
        });
    }

    /**
     *Envía una notificación al token especificado con el mensaje y los datos adicionales dados.
     *<p>
     *Se instancia un objeto mapa para guardar varios datos por clave-valor.
     *Se instancia un objeto de tipo FCMBody(Fire Cloud Messaging) el cual se le asignan por parámetro el token, la prioridad, el tiempo y el objeto data.
     *Después se envia la notificación al otro usuario.
     *
     *@param token El token al que se enviará la notificación.
     *@param messages Mensajes adicionales para incluir en la notificación.
     *@param message El objeto de mensaje que contiene información sobre el remitente y el receptor.
     *@return void
     */
    public void sendNotificaction(String token, String messages, Mensaje message) {
        // Crear un mapa para almacenar los datos de la notificación
        Map<String, String> data = new HashMap<>();

        // Agregar los datos relevantes a enviar en la notificación al mapa
        data.put("title", "MENSAJE");
        data.put("body", message.getMessage());
        data.put("idNotification", String.valueOf(mExtraIdNotificaction));
        data.put("messages", messages);
        data.put("usernameSender", mExtraUsernameReceiver.toUpperCase());
        data.put("usernameReceiver", mExtraUsernameSender.toUpperCase());
        data.put("idSender", message.getIdSender());
        data.put("idReceiver", message.getIdReceiver());
        data.put("idChat", message.getIdChat());
        data.put("imageSender", mExtraImageReceiver);
        data.put("imageReceiver", mExtraImageSender);

        // Crear una instancia de FCMBody con el token, prioridad y tiempo de vida de la notificación
        FCMBody body = new FCMBody(token, "high", "4500s", data);

        // Enviar la notificación utilizando la instancia mNotificationFirebase
        mNotificationFirebase.sendNotification(body).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                // Manejar la respuesta de la solicitud de envío de notificación (opcional)
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                // Manejar el caso de fallo en la solicitud de envío de notificación
                Log.d("ERROR", "El error fue: " + t.getMessage());
            }
        });
    }
    /**
     *Recupera el texto del mensaje del intent dado.
     *@param intent El intent que contiene el texto del mensaje.
     *@return El texto del mensaje si está disponible, null en caso contrario.
     */
    public CharSequence getMessageText(Intent intent) {
        // Obtener los resultados de entrada remota de la intención
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        // Verificar si los resultados de entrada remota no son nulos
        if (remoteInput != null) {
            // Obtener el texto de la entrada remota utilizando la clave NOTIFICATION_REPLY
            return remoteInput.getCharSequence(NOTIFICATION_REPLY);
        }

        // Si no hay resultados de entrada remota, retornar null
        return null;
    }
}
