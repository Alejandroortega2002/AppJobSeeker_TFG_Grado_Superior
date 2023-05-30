package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Favoritos;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritosFirebase {

    private CollectionReference mCollection;

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

    public Query getLikesByUser(String userId) {
        return mCollection.whereEqualTo("idUser", userId);
    }

    public Query getLikeByPostAndUser(String idPost, String idUser) {
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUser", idUser);
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }

    public Task<Void> deleteFavoritesByUser(String userId) {
        Query query = mCollection.whereEqualTo("idUser", userId);

        return query.get().continueWithTask(task -> {
            List<Task<Void>> deleteTasks = new ArrayList<>();

            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                deleteTasks.add(documentSnapshot.getReference().delete());
            }

            return Tasks.whenAll(deleteTasks);
        });
    }

    public Task<Void> deleteFavoritesByPost(String postId) {
        Query query = mCollection.whereEqualTo("idPost", postId);

        return query.get().continueWithTask(task -> {
            List<Task<Void>> deleteTasks = new ArrayList<>();

            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                deleteTasks.add(documentSnapshot.getReference().delete());
            }

            return Tasks.whenAll(deleteTasks);
        });
    }

}