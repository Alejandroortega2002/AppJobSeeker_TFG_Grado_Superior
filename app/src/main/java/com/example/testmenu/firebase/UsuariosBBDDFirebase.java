package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Usuarios;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsuariosBBDDFirebase {

    private final CollectionReference mColeccion;

    public UsuariosBBDDFirebase() {
        mColeccion = FirebaseFirestore.getInstance().collection("Usuarios");
    }

    public Task<DocumentSnapshot> getUsuarios(String id) {
        return mColeccion.document(id).get();
    }

    public DocumentReference getUsuariosRealTime(String id) {
        return mColeccion.document(id);
    }

    public Task<Void> createUsuarios(Usuarios usuario) {
        return mColeccion.document(usuario.getId()).set(usuario);
    }

    public DocumentReference refereciaColeccion(String id) {

        return mColeccion.document(id);
    }

    public Task<Void> deleteUsuarios(String id) {

        return mColeccion.document(id).delete();
    }

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

    public Task<Void> updateOnline(String idUser, boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("LastConnect", new Date().getTime());
        map.put("online", status);
        return mColeccion.document(idUser).update(map);
    }

    public Task<Void> updateMedia(String id, float media) {
        Map<String, Object> map = new HashMap<>();
        map.put("media", media);
        return mColeccion.document(id).update(map);
    }


}
