package com.example.testmenu.firebase;

import android.content.Context;


import com.example.testmenu.utils.CompressorBitmapImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImagenFirebase {
    StorageReference mStorage;

    public ImagenFirebase() {

        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public UploadTask save(Context context, File file) {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("Imagenes de las publicaciones/" + new Date() + ".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    public UploadTask saveImgUser(Context context, File file) {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("Imagenes de los Usuarios/" + new Date() + ".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }
//gg
    public StorageReference getStorage() {
        return mStorage;
    }


}
