package com.example.testmenu.firebase;

import android.content.Context;


import com.example.testmenu.utils.CompressorBitmapImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;
public class ImagenFirebase {
    private StorageReference mStorage;

    /**
     * Constructor de la clase ImagenFirebase.
     * Inicializa la referencia al Storage en Firebase.
     */
    public ImagenFirebase() {
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    /**
     * Guarda una imagen en el Storage de Firebase.
     *
     * @param context Contexto de la aplicación.
     * @param file    Archivo de la imagen a guardar.
     * @return Tarea de carga de la imagen.
     */
    public UploadTask save(Context context, File file) {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("Imagenes de las publicaciones/" + new Date() + ".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    /**
     * Guarda una imagen de usuario en el Storage de Firebase.
     *
     * @param context Contexto de la aplicación.
     * @param file    Archivo de la imagen de usuario a guardar.
     * @return Tarea de carga de la imagen de usuario.
     */
    public UploadTask saveImgUser(Context context, File file) {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("Imagenes de los Usuarios/" + new Date() + ".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    /**
     * Obtiene la referencia al Storage de Firebase.
     *
     * @return Referencia al Storage de Firebase.
     */
    public StorageReference getStorage() {
        return mStorage;
    }
}
