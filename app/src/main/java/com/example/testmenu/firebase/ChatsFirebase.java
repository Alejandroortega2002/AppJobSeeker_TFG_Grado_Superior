package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Chat;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ChatsFirebase {
    private CollectionReference mCollection;

    public ChatsFirebase() {
        mCollection = FirebaseFirestore.getInstance().collection("Chats");

    }

    public void create(Chat chat) {
        mCollection.document(chat.getIdUser1() + chat.getIdUser2()).set(chat);

    }

    public Query getAll(String idUser) {
        return mCollection.whereArrayContains("ids",idUser);
    }

    public Query getChatByUser1AndUser2(String idUser1, String idUser2) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1 + idUser2);
        ids.add(idUser2 + idUser1);

        return mCollection.whereIn("id", ids);
    }


    public void deleteChatsByUserId(String userId) {
        mCollection.whereArrayContains("ids", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error aquí
//                    String errorMessage = "No se pudo eliminar el chat de la cuenta. Inténtelo de nuevo más tarde.";
//                    Toast.makeText(AjustesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
    }
}
