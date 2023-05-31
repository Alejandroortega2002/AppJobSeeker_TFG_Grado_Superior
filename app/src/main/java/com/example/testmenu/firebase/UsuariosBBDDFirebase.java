package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Usuarios;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class UsuariosBBDDFirebase {

    private CollectionReference mColeccion;

    /**
     * Constructor de la clase UsuariosBBDDFirebase.
     */
    public UsuariosBBDDFirebase() {
        mColeccion = FirebaseFirestore.getInstance().collection("Usuarios");
    }

    /**
     * Obtiene los datos de un usuario específico.
     *
     * @param id El ID del usuario.
     * @return Una tarea (Task) que se completa con el documento que contiene los datos del usuario.
     */
    public Task<DocumentSnapshot> getUsuarios(String id) {
        return mColeccion.document(id).get();
    }

    /**
     * Obtiene una consulta de usuarios con un nombre de usuario específico.
     *
     * @param nombreUser El nombre de usuario.
     * @return Una consulta (Query) que devuelve los usuarios con el nombre de usuario especificado.
     */
    public Query getNombreUser(String nombreUser){
        return mColeccion.whereEqualTo("usuario",nombreUser);
    }

    /**
     * Obtiene una consulta de usuarios con un correo electrónico específico.
     *
     * @param correo El correo electrónico.
     * @return Una consulta (Query) que devuelve los usuarios con el correo electrónico especificado.
     */
    public Query getCorreoUser(String correo){
        return mColeccion.whereEqualTo("email",correo);
    }

    /**
     * Obtiene una referencia a tiempo real de los datos de un usuario específico.
     *
     * @param id El ID del usuario.
     * @return Una referencia (DocumentReference) a los datos del usuario.
     */
    public DocumentReference getUsuariosRealTime(String id) {
        return mColeccion.document(id);
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     *
     * @param usuario El objeto Usuarios que representa al usuario.
     * @return Una tarea (Task) que se completa cuando se haya creado el usuario.
     */
    public Task<Void> createUsuarios(Usuarios usuario) {
        return mColeccion.document(usuario.getId()).set(usuario);
    }

    /**
     * Obtiene una referencia a un documento específico en la colección de usuarios.
     *
     * @param id El ID del documento.
     * @return Una referencia (DocumentReference) al documento especificado.
     */
    public DocumentReference refereciaColeccion(String id) {
        return mColeccion.document(id);
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param id El ID del usuario.
     * @return Una tarea (Task) que se completa cuando se haya eliminado el usuario.
     */
    public Task<Void> deleteUsuarios(String id) {
        return mColeccion.document(id).delete();
    }

    /**
     * Actualiza los datos de un usuario en la base de datos.
     *
     * @param usuario El objeto Usuarios que contiene los nuevos datos del usuario.
     * @return Una tarea (Task) que se completa cuando se hayan actualizado los datos del usuario.
     */
    public Task<Void> update(Usuarios usuario) {
        Map<String, Object> map = new HashMap<>();
        map.put("usuario", usuario.getUsuario());
        map.put("telefono", usuario.getTelefono());
        map.put("timeStamp", new Date().getTime());
        map.put("banner", usuario.getBanner());
        map.put("fotoPerfil", usuario.getFotoPerfil());
        map.put("descripcion", usuario.getDescripcion());
        return mColeccion.document(usuario.getId()).update(map);
    }

    /**
     * Actualiza el estado de conexión en línea y la última conexión de un usuario.
     *
     * @param idUser  El ID del usuario.
     * @param status  El estado de conexión en línea.
     * @return Una tarea (Task) que se completa cuando se hayan actualizado los datos del usuario.
     */
    public Task<Void> updateOnline(String idUser, boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("LastConnect", new Date().getTime());
        map.put("online", status);
        return mColeccion.document(idUser).update(map);
    }

    /**
     * Actualiza la media de un usuario.
     *
     * @param id    El ID del usuario.
     * @param media La nueva media.
     * @return Una tarea (Task) que se completa cuando se haya actualizado la media del usuario.
     */
    public Task<Void> updateMedia(String id, float media) {
        Map<String, Object> map = new HashMap<>();
        map.put("media", media);
        return mColeccion.document(id).update(map);
    }

    public Task<Boolean> verificarExistenciaUsuario(String nombreUsuario) {
        Query query = mColeccion.whereEqualTo("usuario", nombreUsuario).limit(1);

        return query.get().continueWith(task -> {
            if (task.isSuccessful()) {
                return !task.getResult().isEmpty();
            } else {
                throw task.getException(); // Lanzar la excepción para manejarla en el lugar donde se llame a este método
            }
        });
    }

}
