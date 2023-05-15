package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Valoraciones;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ValoracionFirebase {

    CollectionReference mCollection;

    public ValoracionFirebase() {
        mCollection = FirebaseFirestore.getInstance().collection("Valoracion");
    }

    public Task<Void> create(Valoraciones valoraciones) {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        valoraciones.setId(id);
        return mCollection.document().set(valoraciones);
    }

    public Query getCommentsByUser(String idUser) {
        return mCollection.whereEqualTo("userId", idUser);
    }

}
