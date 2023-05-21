package com.example.testmenu.receivers;

import static com.example.testmenu.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.RemoteInput;

import com.example.testmenu.activities.ChatActivity;
import com.example.testmenu.entidades.Mensaje;
import com.example.testmenu.firebase.MensajeFirebase;

import java.util.Date;

public class MessageReceiver extends BroadcastReceiver {

    String mExtraIdSender;
    String mExtraIdReceiver;
    String mExtraIdChat;
    int mExtraIdNotificaction;

    @Override
    public void onReceive(Context context, Intent intent) {
        mExtraIdSender = intent.getExtras().getString("idSender");
        mExtraIdReceiver = intent.getExtras().getString("idReceiver");
        mExtraIdChat = intent.getExtras().getString("idChat");
        mExtraIdNotificaction = intent.getExtras().getInt("idNotification");

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
                //getToken(mensaje);
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
