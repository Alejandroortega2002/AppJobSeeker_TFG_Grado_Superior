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

    /**
     * Constructor de la clase PublicacionFirebase.
     */
    public PublicacionFirebase() {
        mCollection = FirebaseFirestore.getInstance().collection("Publicaciones");
    }

    /**
     * Guarda una nueva publicación en la colección "Publicaciones".
     *
     * @param publicacion La publicación a guardar.
     * @return Una tarea (Task) que se completa cuando se guarda la publicación.
     */
    public Task<Void> save(Publicacion publicacion) {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        publicacion.setId(id);
        return document.set(publicacion);
    }

    /**
     * Obtiene las publicaciones de un sector específico ordenadas por timestamp en orden descendente.
     *
     * @param sector El sector de las publicaciones.
     * @return Una consulta (Query) para obtener las publicaciones del sector.
     */
    public Query getPostBySectorAndTimestamp(String sector) {
        return mCollection.whereEqualTo("sector", sector);
    }

    /**
     * Obtiene todas las publicaciones ordenadas por timestamp en orden descendente.
     *
     * @return Una consulta (Query) para obtener todas las publicaciones.
     */
    public Query getAll() {
        return mCollection.orderBy("timeStamp", Query.Direction.DESCENDING);
    }

    /**
     * Obtiene las publicaciones de un usuario específico.
     *
     * @param id El ID del usuario.
     * @return Una consulta (Query) para obtener las publicaciones del usuario.
     */
    public Query getPublicacionDeUsuario(String id) {
        return mCollection.whereEqualTo("idUser", id);
    }

    /**
     * Obtiene las publicaciones que coinciden con un título específico.
     *
     * @param s El título de la publicación a buscar.
     * @return Una consulta (Query) para obtener las publicaciones que coinciden con el título.
     */
    public Query getPostByTitulo(String s) {
        return mCollection.orderBy("titulo").startAt(s).endAt(s + '\uf8ff');
    }

    /**
     * Obtiene una publicación por su ID.
     *
     * @param id El ID de la publicación.
     * @return Una tarea (Task) que se completa con la publicación correspondiente al ID.
     */
    public Task<DocumentSnapshot> getPostById(String id) {
        return mCollection.document(id).get();
    }

    /**
     * Obtiene las publicaciones por una lista de IDs.
     *
     * @param postIds Lista de IDs de las publicaciones a obtener.
     * @return Una consulta (Query) para obtener las publicaciones por los IDs especificados.
     */
    public Query getPostByIdList(List<String> postIds) {
        return mCollection.whereIn("id", postIds);
    }

    /**
     * Borra una publicación por su ID.
     *
     * @param id El ID de la publicación a borrar.
     * @return Una tarea (Task) que se completa cuando se borra la publicación.
     */
    public Task<Void> borrarPublicacion(String id) {
        return mCollection.document(id).delete();
    }

    /**
     * Borra todas las publicaciones de un usuario específico.
     *
     * @param idUsuario El ID del usuario.
     * @return Una tarea (Task) que se completa cuando se borran todas las publicaciones del usuario.
     */
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
