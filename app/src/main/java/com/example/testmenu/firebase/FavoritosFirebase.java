package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Favoritos;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FavoritosFirebase {

    CollectionReference mCollection;

    public FavoritosFirebase() {
        mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }

    public Task<Void> create(Favoritos favoritos) {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        favoritos.setId(id);
        return document.set(favoritos);
    }

    public Query getLikesByPost(String idPost) {
        return mCollection.whereEqualTo("idPost", idPost);
    }

    public Query getLikeByPostAndUser(String idPost, String idUser) {
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUser", idUser);
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }

}