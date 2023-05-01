package com.example.testmenu.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Usuarios;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.ImagenFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditarPerfilActivity extends AppCompatActivity {

    ImageButton btnAtras;
    CircleImageView fotoPerfil;
    ImageView fotoBanner;
    EditText usuario, telefono, descripcion;
    Button editarPerfil;

    AlertDialog mDialog;

    ImagenFirebase mImageProvider;
    UsuariosBBDDFirebase mUsersProvider;
    AutentificacioFirebase mAuthProvider;

    String mUsername = "";
    String mPhone = "";
    String mImageProfile = "";
    String mImageCover = "";
    String mDescripcion = "";

    //Imagenes

    File mImageFile;
    File mImageFile2;

    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];

    private final int GALLERY_REQUEST_CODE_PERFIL = 1;
    private final int GALLERY_REQUEST_CODE_BANNER = 2;
    private final int PHOTO_REQUEST_CODE_PERFIL = 3;
    private final int PHOTO_REQUEST_CODE_BANNER = 4;


    //FOTO1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;
    //FOTO2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnAtras = findViewById(R.id.btnSalir);
        fotoPerfil = findViewById(R.id.fotoPerfil);
        fotoBanner = findViewById(R.id.banner);
        usuario = findViewById(R.id.editUsername);
        telefono = findViewById(R.id.editTelefono);
        descripcion = findViewById(R.id.editDescripcion);
        editarPerfil = findViewById(R.id.btnActualizar);

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Imagen de galeria", "Tomar foto"};

        mImageProvider = new ImagenFirebase();
        mUsersProvider = new UsuariosBBDDFirebase();
        mAuthProvider = new AutentificacioFirebase();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("espere un momento")
                .setCancelable(false).build();

        rellenarInformacionUsuario();
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImagen(1);
            }
        });

        fotoBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImagen(2);
            }
        });

        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEditProfile();
            }
        });
    }

    public void rellenarInformacionUsuario() {
        DocumentReference documentReference = mUsersProvider.refereciaColeccion(mAuthProvider.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Manejar el error de Firebase Firestore
                    Log.w(TAG, "Error al obtener el documento.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    // Obtener los valores del objeto DocumentSnapshot
                    String nombre = value.getString("usuario");
                    String ntelefono = value.getString("telefono");
                    String descrip = value.getString("descripcion");


                    // Verificar si los valores obtenidos son nulos antes de establecer el texto en los TextViews
                    if(value.contains("fotoPerfil")){
                        String perfil = value.getString("fotoPerfil");
                        if(perfil != null){
                            if(!perfil.isEmpty()){
                                Picasso.get().load(perfil).into(fotoPerfil);
                            }
                        }
                    }
                    if(value.contains("banner")){
                        String banner = value.getString("banner");
                        if(banner != null){
                            if(!banner.isEmpty()){
                                Picasso.get().load(banner).into(fotoBanner);
                            }
                        }
                    }
                    if (nombre != null) {
                        usuario.setText(nombre);
                    } else {
                        usuario.setText("Sin nombre");
                    }
                    if (telefono != null) {
                        telefono.setText(ntelefono);
                    } else {
                        telefono.setText("Sin teléfono");
                    }
                    if (descripcion != null) {
                        descripcion.setText(descrip);
                    } else {
                        descripcion.setText("Sin descripción");
                    }
                } else {
                    // Manejar el caso en que el objeto DocumentSnapshot es nulo o no existe
                    Log.d(TAG, "El objeto DocumentSnapshot no existe");
                }
            }
        });
    }

    private void clickEditProfile() {
        mUsername = usuario.getText().toString();
        mPhone = telefono.getText().toString();
        mDescripcion = descripcion.getText().toString();
        if (!mUsername.isEmpty() && !mPhone.isEmpty()) {
            if (mImageFile != null && mImageFile2 != null) {
                saveImageCoverAndProfile(mImageFile, mImageFile2);
            }
            // TOMO LAS DOS FOTOS DE LA CAMARA
            else if (mPhotoFile != null && mPhotoFile2 != null) {
                saveImageCoverAndProfile(mPhotoFile, mPhotoFile2);
            } else if (mImageFile != null && mPhotoFile2 != null) {
                saveImageCoverAndProfile(mImageFile, mPhotoFile2);
            } else if (mPhotoFile != null && mImageFile2 != null) {
                saveImageCoverAndProfile(mPhotoFile, mImageFile2);
            } else if (mPhotoFile != null) {
                saveImage(mPhotoFile, true);
            } else if (mPhotoFile2 != null) {
                saveImage(mPhotoFile2, false);
            } else if (mImageFile != null) {
                saveImage(mImageFile, true);
            } else if (mImageFile2 != null) {
                saveImage(mImageFile2, false);
            } else {
                Usuarios user = new Usuarios();
                user.setUsuario(mUsername);
                user.setTelefono(mPhone);
                user.setDescripcion(mDescripcion);
                user.setId(mAuthProvider.getUid());
                updateInfo(user);
            }
        } else {
            Toast.makeText(this, "Ingrese el nombre de usuario, el telefono y la descripción", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageCoverAndProfile(File imageFile1, final File imageFile2) {
        mDialog.show();
        mImageProvider.save(EditarPerfilActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlProfile = uri.toString();

                            mImageProvider.save(EditarPerfilActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()) {
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String urlCover = uri2.toString();
                                                Usuarios user = new Usuarios();
                                                user.setUsuario(mUsername);
                                                user.setTelefono(mPhone);
                                                user.setDescripcion(mDescripcion);
                                                user.setFotoPerfil(urlProfile);
                                                user.setBanner(urlCover);
                                                user.setId(mAuthProvider.getUid());
                                                updateInfo(user);
                                            }
                                        });
                                    } else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditarPerfilActivity.this, "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditarPerfilActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveImage(File image, final boolean isProfileImage) {
        mDialog.show();
        mImageProvider.save(EditarPerfilActivity.this, image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();
                            Usuarios user = new Usuarios();
                            user.setUsuario(mUsername);
                            user.setTelefono(mPhone);
                            user.setDescripcion(mDescripcion);
                            if (isProfileImage) {
                                user.setFotoPerfil(url);
                                user.setBanner(mImageCover);
                            } else {
                                user.setBanner(url);
                                user.setFotoPerfil(mImageProfile);
                            }
                            user.setId(mAuthProvider.getUid());
                            updateInfo(user);
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditarPerfilActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateInfo(Usuarios usuario) {
        if (mDialog.isShowing()) {
            mDialog.show();
        }
        mUsersProvider.update(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(EditarPerfilActivity.this, "La informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditarPerfilActivity.this, "La informacion no se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectOptionImagen(final int numberImage) {

        mBuilderSelector.setItems(options, (dialogInterface, i) -> {
            if (i == 0) {
                if (numberImage == 1) {
                    openGallery(GALLERY_REQUEST_CODE_PERFIL);
                } else if (numberImage == 2) {
                    openGallery(GALLERY_REQUEST_CODE_BANNER);
                }
            } else if (i == 1) {
                if (numberImage == 1) {
                    takePhoto(PHOTO_REQUEST_CODE_PERFIL);
                } else if (numberImage == 2) {
                    takePhoto(PHOTO_REQUEST_CODE_BANNER);
                }
            }
        });
        mBuilderSelector.show();
    }

    private void takePhoto(int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile(requestCode);
            } catch (Exception e) {
                Toast.makeText(this, "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(EditarPerfilActivity.this, "com.example.testmenu", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    private File createPhotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );
        if (requestCode == PHOTO_REQUEST_CODE_PERFIL) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        } else if (requestCode == PHOTO_REQUEST_CODE_BANNER) {
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * SELECCION DE IMAGEN DESDE LA GALERIA
         */
        if (requestCode == GALLERY_REQUEST_CODE_PERFIL && resultCode == RESULT_OK) {
            try {

                mImageFile = FileUtil.from(this, data.getData());
                fotoPerfil.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_BANNER && resultCode == RESULT_OK) {
            try {
                mImageFile2 = FileUtil.from(this, data.getData());
                fotoBanner.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        /**
         * SELECCION DE FOTOGRAFIA
         */
        if (requestCode == PHOTO_REQUEST_CODE_PERFIL && resultCode == RESULT_OK) {
            mImageFile = new File(mAbsolutePhotoPath);
            Picasso.get().load(mPhotoPath).into(fotoPerfil);
        }

        /**
         * SELECCION DE FOTOGRAFIA
         */
        if (requestCode == PHOTO_REQUEST_CODE_BANNER && resultCode == RESULT_OK) {
            mImageFile2 = new File(mAbsolutePhotoPath2);
            Picasso.get().load(mPhotoPath2).into(fotoBanner);
        }
    }
}