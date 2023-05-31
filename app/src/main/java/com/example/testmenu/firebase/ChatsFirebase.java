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

    /**
     * Constructor de la clase ChatsFirebase.
     * Inicializa la referencia a la colección "Chats" en Firestore.
     */
    public ChatsFirebase() {
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    /**
     * Crea un nuevo documento de chat en Firestore.
     *
     * @param chat Objeto Chat que representa el chat a crear.
     */
    public void create(Chat chat) {
        mCollection.document(chat.getIdUser1() + chat.getIdUser2()).set(chat);
    }

    /**
     * Obtiene una consulta de todos los chats en los que está involucrado un usuario específico.
     *
     * @param idUser ID del usuario para el cual se obtendrán los chats.
     * @return Consulta que devuelve todos los chats en los que está involucrado el usuario.
     */
    public Query getAll(String idUser) {
        return mCollection.whereArrayContains("ids", idUser);
    }

    /**
     * Obtiene una consulta para un chat específico entre dos usuarios.
     *
     * @param idUser1 ID del primer usuario.
     * @param idUser2 ID del segundo usuario.
     * @return Consulta que devuelve el chat específico entre los dos usuarios.
     */
    public Query getChatByUser1AndUser2(String idUser1, String idUser2) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1 + idUser2);
        ids.add(idUser2 + idUser1);

        return mCollection.whereIn("id", ids);
    }

    /**
     * Elimina todos los chats asociados a un usuario específico.
     *
     * @param userId ID del usuario para el cual se eliminarán los chats.
     */
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
