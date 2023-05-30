package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Valoraciones;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

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

    public Task<Void> deleteCommentsByUser(String idUser) {
        // Crea una consulta para obtener los comentarios del usuario especificado
        Query query = getCommentsByUser(idUser);

        // Ejecuta la consulta y encadena una tarea con continueWithTask
        return query.get().continueWithTask(task -> {
            // Obtiene los documentos resultantes de la consulta
            List<DocumentSnapshot> documents = task.getResult().getDocuments();

            // Crea una lista para almacenar las tareas de eliminación de comentarios
            List<Task<Void>> deleteTasks = new ArrayList<>();

            // Itera sobre cada documento obtenido
            for (DocumentSnapshot document : documents) {
                // Obtiene la referencia al documento y crea una tarea de eliminación
                deleteTasks.add(document.getReference().delete());
            }

            // Combina todas las tareas de eliminación en una sola tarea compuesta
            return Tasks.whenAll(deleteTasks);
        });
    }


}
