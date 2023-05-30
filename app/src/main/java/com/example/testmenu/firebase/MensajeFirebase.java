package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Mensaje;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class MensajeFirebase {
    private CollectionReference mCollection;

    public MensajeFirebase(){
        mCollection = FirebaseFirestore.getInstance().collection("Mensajes");
    }

    public Task<Void> create(Mensaje mensaje){
        DocumentReference document = mCollection.document();
        mensaje.setId((document.getId()));
        return document.set(mensaje);
    }

    public Query getMensajeByChat(String idChat){
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.ASCENDING);
    }

    public Query getMensajeByChatAndSender(String idChat, String idSender){
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idSender",idSender).whereEqualTo("viewed", false);
    }
   public Query getLastThreeMensajeByChatAndSender(String idChat, String idSender){
        return mCollection
                .whereEqualTo("idChat", idChat)
                .whereEqualTo("idSender",idSender)
                .whereEqualTo("viewed", false)
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .limit(3);
    }

    public Query getLastMessage(String idChat){
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }

    public Query getLastMessageSender(String idChat, String idSender){
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idSender",idSender).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }
    public Task<Void>updateviewed(String idDocument, boolean state){
        Map<String,Object> map = new HashMap<>();
        map.put("viewed",state);
        return  mCollection.document(idDocument).update(map);
    }
}
