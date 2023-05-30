package com.example.testmenu.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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

    private static int PERMISSION_REQUEST_CODE = 1;
    private ImageButton btnAtras;
    private CircleImageView fotoPerfil;
    private ImageView fotoBanner;
    private EditText usuario, telefono, descripcion;
    private Button editarPerfil;

    private AlertDialog mDialog;

    private ImagenFirebase mImageProvider;
    private UsuariosBBDDFirebase mUsersProvider;
    private AutentificacioFirebase mAuthProvider;

    private String mUsername = "";
    private String mPhone = "";
    private String mImageProfile = "";
    private String mImageCover = "";
    private String mDescripcion = "";

    //Imagenes
    private File mImageFile;
    private File mImageFile2;

    private AlertDialog.Builder mBuilderSelector;
    private CharSequence options[];

    private int GALLERY_REQUEST_CODE_PERFIL = 1;
    private int GALLERY_REQUEST_CODE_BANNER = 2;
    private int PHOTO_REQUEST_CODE_PERFIL = 3;
    private int PHOTO_REQUEST_CODE_BANNER = 4;

    //FOTO1
    private String mAbsolutePhotoPath;
    private String mPhotoPath;
    private File mPhotoFile;

    //FOTO2
    private String mAbsolutePhotoPath2;
    private String mPhotoPath2;
    private File mPhotoFile2;


    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
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

    /**
     * Rellena los campos de Editext e Images de la activity con los datos del usuario desde la base de datos
     * <p>
     * En caso de error, se registra en el Log con un mensaje
     *
     * @return void
     */
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
                    if (value.contains("fotoPerfil")) {
                        String perfil = value.getString("fotoPerfil");
                        if (perfil != null) {
                            if (!perfil.isEmpty()) {
                                Picasso.get().load(perfil).into(fotoPerfil);
                            }
                        }
                    }
                    if (value.contains("banner")) {
                        String banner = value.getString("banner");
                        if (banner != null) {
                            if (!banner.isEmpty()) {
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

    /**
     * Se guardan los nuevos datos y se registran en la base de datos
     *
     * @return void
     */
    public void clickEditProfile() {
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

    /**
     * Almacena la imagen de perfil y portada del usuario en la base de datos de Firebase Storage.
     *
     * @param imageFile1 imagen de perfil
     * @param imageFile2 imagen de portada
     */
    public void saveImageCoverAndProfile(File imageFile1, final File imageFile2) {
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

    /**
     * Guarda una imagen en Firebase Storage y actualiza la información del usuario con la URL de la imagen.
     *
     * @param image          imagen a guardar
     * @param isProfileImage comprueba el tipo de archivo.
     * @return void
     */
    public void saveImage(File image, final boolean isProfileImage) {
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

    /**
     * Pasa los cambios realizados a la base de datos, muestra un dialogo en caso de éxito o error
     *
     * @param usuario usuario ha actualizar
     * @return void
     */
    public void updateInfo(Usuarios usuario) {
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

    /**
     * Solicitud de cambio de imagen. Dependiendo de la solicitud se abre la galería o la cámara de fotos
     *
     * @param numberImage número de imagen (1 o 2)
     * @return void
     */
    public void selectOptionImagen(final int numberImage) {

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

    /**
     * Toma una foto y se le asigna a la solicitud deseada
     *
     * @param requestCode el código de solicitud que se utiliza para identificar la solicitud
     */
    public void takePhoto(int requestCode) {
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

    /**
     * Se crea archivo de la foto en formato <b>.jpg</b>
     *
     * @param requestCode el código de solicitud que se utiliza para identificar la solicitud
     * @return archivo de imagen creado
     * @throws IOException
     */
    public File createPhotoFile(int requestCode) throws IOException {
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

    /**
     * Abre la galería del dispositivo por el directorio <b>image/</b>
     *
     * @param requestCode el código de solicitud que se utiliza para identificar la solicitud
     * @return void
     */
    public void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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