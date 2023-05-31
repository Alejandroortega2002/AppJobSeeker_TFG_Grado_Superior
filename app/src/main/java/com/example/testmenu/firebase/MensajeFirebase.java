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

    /**
     * Constructor de la clase MensajeFirebase.
     */
    public MensajeFirebase() {
        mCollection = FirebaseFirestore.getInstance().collection("Mensajes");
    }

    /**
     * Crea un nuevo mensaje en la colección "Mensajes".
     *
     * @param mensaje El mensaje a crear.
     * @return Una tarea (Task) que se completa cuando se crea el mensaje.
     */
    public Task<Void> create(Mensaje mensaje) {
        DocumentReference document = mCollection.document();
        mensaje.setId(document.getId());
        return document.set(mensaje);
    }

    /**
     * Obtiene los mensajes de un chat específico ordenados por timestamp en orden ascendente.
     *
     * @param idChat El ID del chat.
     * @return Una consulta (Query) para obtener los mensajes del chat.
     */
    public Query getMensajeByChat(String idChat) {
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.ASCENDING);
    }

    /**
     * Obtiene los mensajes de un chat específico enviados por un remitente específico y no vistos.
     *
     * @param idChat   El ID del chat.
     * @param idSender El ID del remitente.
     * @return Una consulta (Query) para obtener los mensajes del chat y remitente específicos que no han sido vistos.
     */
    public Query getMensajeByChatAndSender(String idChat, String idSender) {
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender).whereEqualTo("viewed", false);
    }

    /**
     * Obtiene los últimos tres mensajes de un chat específico enviados por un remitente específico y no vistos.
     *
     * @param idChat   El ID del chat.
     * @param idSender El ID del remitente.
     * @return Una consulta (Query) para obtener los últimos tres mensajes del chat y remitente específicos que no han sido vistos.
     */
    public Query getLastThreeMensajeByChatAndSender(String idChat, String idSender) {
        return mCollection
                .whereEqualTo("idChat", idChat)
                .whereEqualTo("idSender", idSender)
                .whereEqualTo("viewed", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3);
    }

    /**
     * Obtiene el último mensaje de un chat específico ordenado por timestamp en orden descendente.
     *
     * @param idChat El ID del chat.
     * @return Una consulta (Query) para obtener el último mensaje del chat.
     */
    public Query getLastMessage(String idChat) {
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }

    /**
     * Obtiene el último mensaje enviado por un remitente específico en un chat específico ordenado por timestamp en orden descendente.
     *
     * @param idChat   El ID del chat.
     * @param idSender El ID del remitente.
     * @return Una consulta (Query) para obtener el último mensaje enviado por el remitente en el chat.
     */
    public Query getLastMessageSender(String idChat, String idSender) {
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }

    /**
     * Actualiza el estado "visto" de un mensaje específico.
     *
     * @param idDocument El ID del documento del mensaje.
     * @param state      El nuevo estado "visto".
     * @return Una tarea (Task) que se completa cuando se actualiza el estado "visto" del mensaje.
     */
    public Task<Void> updateviewed(String idDocument, boolean state) {
        Map<String, Object> map = new HashMap<>();
        map.put("viewed", state);
        return mCollection.document(idDocument).update(map);
    }
}
