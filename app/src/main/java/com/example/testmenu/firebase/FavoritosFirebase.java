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

    /**
     * Constructor de la clase FavoritosFirebase.
     * Inicializa la referencia a la colección "Likes" en Firestore.
     */
    public FavoritosFirebase() {
        mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }

    /**
     * Crea un nuevo documento de favorito en Firestore.
     *
     * @param favoritos Objeto Favoritos que representa el favorito a crear.
     * @return Una tarea que se completa una vez que se haya creado el favorito.
     */
    public Task<Void> create(Favoritos favoritos) {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        favoritos.setId(id);
        return document.set(favoritos);
    }

    /**
     * Obtiene una consulta de todos los favoritos para una publicación específica.
     *
     * @param idPost ID de la publicación para la cual se obtendrán los favoritos.
     * @return Consulta que devuelve todos los favoritos para la publicación.
     */
    public Query getLikesByPost(String idPost) {
        return mCollection.whereEqualTo("idPost", idPost);
    }

    /**
     * Obtiene una consulta de todos los favoritos realizados por un usuario específico.
     *
     * @param userId ID del usuario para el cual se obtendrán los favoritos.
     * @return Consulta que devuelve todos los favoritos realizados por el usuario.
     */
    public Query getLikesByUser(String userId) {
        return mCollection.whereEqualTo("idUser", userId);
    }

    /**
     * Obtiene una consulta para un favorito específico basado en el ID de la publicación y el ID del usuario.
     *
     * @param idPost ID de la publicación.
     * @param idUser ID del usuario.
     * @return Consulta que devuelve el favorito específico.
     */
    public Query getLikeByPostAndUser(String idPost, String idUser) {
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUser", idUser);
    }

    /**
     * Elimina un favorito específico de Firestore.
     *
     * @param id ID del favorito a eliminar.
     * @return Una tarea que se completa una vez que se haya eliminado el favorito.
     */
    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }

    /**
     * Elimina todos los favoritos realizados por un usuario específico.
     *
     * @param userId ID del usuario para el cual se eliminarán los favoritos.
     * @return Una tarea que se completa una vez que se hayan eliminado todos los favoritos.
     */
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

    /**
     * Elimina todos los favoritos asociados a una publicación específica.
     *
     * @param postId ID de la publicación para la cual se eliminarán los favoritos.
     * @return Una tarea que se completa una vez que se hayan eliminado todos los favoritos.
     */
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
