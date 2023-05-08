package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Publicacion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

public class PublicacionFirebase {
    CollectionReference mCollection;

    public PublicacionFirebase() {
        mCollection = FirebaseFirestore.getInstance().collection("Publicaciones");
    }

    public Task<Void> save(Publicacion publicacion) {
        return mCollection.document().set(publicacion);
    }

    public Query getAll() {
        return mCollection.orderBy("timeStamp", Query.Direction.DESCENDING);
    }

    public Query getPublicacionDeUsuario(String id) {
        return mCollection.whereEqualTo("idUser", id);
    }


    public Task<DocumentSnapshot>getPostById(String id){
        return mCollection.document(id).get();
    }

    public Task<Void> borrarPublicacion(String id){
        return mCollection.document(id).delete();
    }
}
