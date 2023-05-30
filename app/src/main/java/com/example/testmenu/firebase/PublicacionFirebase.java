package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Publicacion;
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

public class PublicacionFirebase {
    private CollectionReference mCollection;

    public PublicacionFirebase() {
        mCollection = FirebaseFirestore.getInstance().collection("Publicaciones");

    }

    public Task<Void> save(Publicacion publicacion) {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        publicacion.setId(id);
        return document.set(publicacion);
    }

    public Query getPostBySectorAndTimestamp(String sector) {
        return mCollection.whereEqualTo("sector", sector);
    }

    public Query getAll() {
        return mCollection.orderBy("timeStamp", Query.Direction.DESCENDING);
    }

    public Query getPublicacionDeUsuario(String id) {
        return mCollection.whereEqualTo("idUser", id);
    }

    public Query getPostByTitulo(String s) {
        return mCollection.orderBy("titulo").startAt(s).endAt(s+'\uf8ff');
    }

    public Task<DocumentSnapshot> getPostById(String id){
        return mCollection.document(id).get();
    }

    public Query getPostByIdList(List<String> postIds) {
        return mCollection.whereIn("id", postIds);
    }

    public Task<Void> borrarPublicacion(String id){
        return mCollection.document(id).delete();
    }

    public Task<Void> borrarPublicacionesDeUsuario(String idUsuario) {
        // Obtener la Query que devuelve todas las publicaciones del usuario
        Query query = getPublicacionDeUsuario(idUsuario);

        // Ejecutar la Query para obtener los resultados de las publicaciones del usuario
        return query.get().continueWithTask(task -> {
            if (task.isSuccessful()) {
                // Crear una lista de tareas de eliminación
                List<Task<Void>> tasks = new ArrayList<>();

                // Recorrer los resultados de la Query y agregar las tareas de eliminación
                for (QueryDocumentSnapshot document : task.getResult()) {
                    tasks.add(borrarPublicacion(document.getId()));
                }

                // Combinar todas las tareas de eliminación en una sola tarea
                return Tasks.whenAll(tasks);
            } else {
                throw task.getException();
            }
        });
    }
}
