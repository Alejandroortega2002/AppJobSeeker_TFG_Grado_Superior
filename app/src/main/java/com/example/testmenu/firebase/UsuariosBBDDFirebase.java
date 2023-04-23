package com.example.testmenu.firebase;

import com.example.testmenu.entidades.Usuarios;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsuariosBBDDFirebase {

    private CollectionReference mColeccion;

    public UsuariosBBDDFirebase(){
        mColeccion = FirebaseFirestore.getInstance().collection("Usuarios");
    }

    public Task<DocumentSnapshot> getUsuarios(String id) {
        return mColeccion.document(id).get();
    }

    public Task<Void> createUsuarios(Usuarios usuario) {
        return  mColeccion.document(usuario.getId()).set(usuario);
    }

    public DocumentReference refereciaColeccion(String id) {
        return mColeccion.document(id);
    }






}
