package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Chat;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatsFirebase {
    CollectionReference mCollection;

    public ChatsFirebase(){
        mCollection = FirebaseFirestore.getInstance().collection("Chats");

    }

    public void create(Chat chat){
         mCollection.document(chat.getIdUser1()).collection("UsersChat").document(chat.getIdUser2()).set(chat);
         mCollection.document(chat.getIdUser2()).collection("UsersChat").document(chat.getIdUser1()).set(chat);

    }

    public Query getAll(String idUser){
        return mCollection.document(idUser).collection("UsersChat");
    }
}
