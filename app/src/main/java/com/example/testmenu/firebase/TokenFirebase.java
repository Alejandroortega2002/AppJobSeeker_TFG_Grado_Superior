package com.example.testmenu.firebase;

import static android.content.ContentValues.TAG;

import android.util.Log;


import androidx.annotation.NonNull;


import com.example.testmenu.entidades.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
public class TokenFirebase {
    private CollectionReference mCollection;

    /**
     * Constructor de la clase TokenFirebase.
     */
    public TokenFirebase(){
        mCollection = FirebaseFirestore.getInstance().collection("Tokens");
    }

    /**
     * Crea y guarda un token de notificación para un usuario específico.
     *
     * @param idUser El ID del usuario.
     */
    public void create(String idUser){
        if (idUser == null){
            return;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Obtener el nuevo token de registro de FCM
                        Token tokenFB = new Token(task.getResult());
                        String token = task.getResult();
                        mCollection.document(idUser).set(tokenFB);

                        // Imprimir el token en el log y mostrarlo en un toast
                        System.out.println(token);
                    }
                });

    }

    /**
     * Obtiene el token de notificación de un usuario específico.
     *
     * @param idUser El ID del usuario.
     * @return Una tarea (Task) que se completa con el documento que contiene el token.
     */
    public Task<DocumentSnapshot> getToken(String idUser){
        return mCollection.document(idUser).get();
    }
}
