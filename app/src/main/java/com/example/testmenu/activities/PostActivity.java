package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.ImagenFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.utils.FileUtil;
import com.example.testmenu.utils.ViewedMensajeHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    Button mButtonPost;
    ImagenFirebase mImagenFirebase;
    PublicacionFirebase mPublicacionFribase;
    AutentificacioFirebase mAutentificacionFirebase;
    TextInputEditText mTextInputTitulo;
    TextInputEditText mTextInputPrecio;
    TextInputEditText mTextInputDescripcion;
    ImageView mImageviewContrato1;
    ImageView mImageviewContrato2;
    ImageView mImageviewContrato3;
    ImageView mImageviewContrato4;
    CircleImageView mCircleBack;


    TextView mTextViewCategoria;
    Spinner mTextViewSector;
    File mImageFile;
    File mImageFile2;

    String mCategoria = "";

    String mSector = "";
    String mTitulo = "";
    String mPrecio = "";
    String mDescripcion = "";
    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];

    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE_2 = 2;
    private final int PHOTO_REQUEST_CODE = 3;
    private final int PHOTO_REQUEST_CODE_2 = 4;

    //FOTO1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    //FOTO2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;

    AlertDialog dialogoEspera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Verificar y solicitar los permisos necesarios
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        mImagenFirebase = new ImagenFirebase();
        mPublicacionFribase = new PublicacionFirebase();
        mAutentificacionFirebase = new AutentificacioFirebase();


        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Imagen de galeria", "Tomar foto"};

        dialogoEspera = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("espere un momento")
                .setCancelable(false).build();

        mImageViewPost1 = findViewById(R.id.imagePost1);
        mImageViewPost2 = findViewById(R.id.imagePost2);
        mButtonPost = findViewById(R.id.btnPost);
        mTextInputTitulo = findViewById(R.id.textInputnombrePublicacion);
        mTextInputPrecio = findViewById(R.id.textInputPagar);
        mTextInputDescripcion = findViewById(R.id.textInputDescription);
        mImageviewContrato1 = findViewById(R.id.contrato1);
        mImageviewContrato2 = findViewById(R.id.contrato2);
        mImageviewContrato3 = findViewById(R.id.contrato3);
        mImageviewContrato4 = findViewById(R.id.contrato4);
        mTextViewCategoria = findViewById(R.id.textViewCategoria);
        mTextViewSector = findViewById(R.id.spinnerSector);
        mCircleBack = findViewById(R.id.back);


        mCircleBack.setOnClickListener(view -> finish());
        mButtonPost.setOnClickListener(view -> clickPost());

        mImageViewPost1.setOnClickListener(view -> {
            selectOptionImagen(1);

        });
        mImageViewPost2.setOnClickListener(view -> {
            selectOptionImagen(2);
        });

        mImageviewContrato1.setOnClickListener(view -> {
            mCategoria = "Indefinido";
            mTextViewCategoria.setText(mCategoria);
        });
        mImageviewContrato2.setOnClickListener(view -> {
            mCategoria = "Temporal";
            mTextViewCategoria.setText(mCategoria);
        });
        mImageviewContrato3.setOnClickListener(view -> {
            mCategoria = "Media Jornada";
            mTextViewCategoria.setText(mCategoria);
        });
        mImageviewContrato4.setOnClickListener(view -> {
            mCategoria = "Eventual";
            mTextViewCategoria.setText(mCategoria);
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Los permisos han sido concedidos
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
            } else {
                // Al menos uno de los permisos fue denegado
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Muestra un cuadro de diálogo que permite al usuario seleccionar una imagen de la galería o tomar una foto.
     *
     * @param numberImage el número de imagen que se seleccionará (1 o 2).
     */
    private void selectOptionImagen(final int numberImage) {

        // Configura un cuadro de diálogo que muestra las opciones para seleccionar o tomar una foto
        AlertDialog.Builder mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción:");
        String[] options = {"Galería", "Tomar foto"};

        // Maneja la selección del usuario en el cuadro de diálogo y llama a los métodos correspondientes para seleccionar o tomar una foto
        mBuilderSelector.setItems(options, (dialogInterface, i) -> {
            if (i == 0) {
                if (numberImage == 1) {
                    openGallery(GALLERY_REQUEST_CODE);
                } else if (numberImage == 2) {
                    openGallery(GALLERY_REQUEST_CODE_2);
                }
            } else if (i == 1) {
                if (numberImage == 1) {
                    takePhoto(PHOTO_REQUEST_CODE);
                } else if (numberImage == 2) {
                    takePhoto(PHOTO_REQUEST_CODE_2);
                }
            }
        });

        // Muestra el cuadro de diálogo
        mBuilderSelector.show();
    }

    /**
     * Inicia la aplicación de la cámara para tomar una foto y guarda la imagen en un archivo temporal.
     *
     * @param requestCode el código de solicitud que se utiliza para identificar la solicitud en el método onActivityResult.
     */
    @SuppressLint("QueryPermissionsNeeded")
    private void takePhoto(int requestCode) {

        // Crea una intención para iniciar la aplicación de la cámara
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Comprueba si hay una aplicación de cámara disponible en el dispositivo
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Crea un archivo temporal para guardar la imagen capturada por la cámara
            File photoFile = null;
            try {
                photoFile = createPhotoFile(requestCode);
            } catch (Exception e) {
                Toast.makeText(this, "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            // Si se ha creado el archivo correctamente, obtiene una URI para él y añade la URI a la intención
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.example.testmenu", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                // Inicia la aplicación de la cámara y espera a que el usuario capture una imagen
                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }


    /**
     * Crea un archivo de foto temporal con un nombre de archivo único utilizando la fecha y hora actuales.
     *
     * @param requestCode el código de solicitud para la foto, para guardar la ruta absoluta del archivo
     * @return el archivo de imagen creado
     * @throws IOException si hay algún error al crear el archivo
     */
    private File createPhotoFile(int requestCode) throws IOException {
        // Crea un nombre de archivo único usando la fecha y hora actuales
        String timeStamp = String.valueOf(new Date().getTime());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Obtiene la carpeta de imágenes del directorio de la aplicación
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Crea un archivo de imagen en la carpeta de imágenes
        File imageFile = File.createTempFile(
                imageFileName, // prefijo del nombre del archivo
                ".jpg", // extensión del archivo
                storageDir // directorio donde se creará el archivo
        );

        // Guarda la ruta absoluta del archivo
        if (requestCode == PHOTO_REQUEST_CODE) {
            mAbsolutePhotoPath = imageFile.getAbsolutePath();
        } else if (requestCode == PHOTO_REQUEST_CODE_2) {
            mAbsolutePhotoPath2 = imageFile.getAbsolutePath();
        }

        // Devuelve el archivo creado
        return imageFile;
    }


    /**
     * Recupera los valores de los campos de título, precio y descripción, y verifica que no estén vacíos.
     * <p>
     * Luego verifica que se hayan seleccionado ambas imágenes y llama al método "saveImage" para guardar las imágenes
     * <p>
     * en la base de datos.
     */
    private void clickPost() {
        mTitulo = mTextInputTitulo.getText().toString().trim();
        mPrecio = mTextInputPrecio.getText().toString().trim();
        mDescripcion = mTextInputDescripcion.getText().toString().trim();
        mSector = mTextViewSector.getSelectedItem().toString().trim();

        if (mTitulo.isEmpty() || mPrecio.isEmpty() || mDescripcion.isEmpty() || mSector.equals("Sector")) {
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Selección de imagen
        if (mImageFile == null || mImageFile2 == null) {
            Toast.makeText(this, "Debes seleccionar ambas imágenes", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogoEspera.show();
        saveImage(mImageFile, mImageFile2);
    }


    /**
     * Este método se encarga de guardar las imágenes en Firebase Storage y luego guardar la información
     * <p>
     * de la publicación en Firestore.
     *
     * @param imageFile1 archivo de imagen 1 a guardar
     * @param imageFile2 archivo de imagen 2 a guardar
     */
    private void saveImage(File imageFile1, final File imageFile2) {

        // Guardar la primera imagen
        mImagenFirebase.save(PostActivity.this, imageFile1).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(PostActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                return;
            }
            // Obtener la URL de la primera imagen guardada
            mImagenFirebase.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                final String url = uri.toString();

                // Guardar la segunda imagen
                mImagenFirebase.save(PostActivity.this, imageFile2).addOnCompleteListener(taskImage2 -> {
                    if (!taskImage2.isSuccessful()) {
                        Toast.makeText(PostActivity.this, "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Obtener la URL de la segunda imagen guardada
                    mImagenFirebase.getStorage().getDownloadUrl().addOnSuccessListener(uri2 -> {
                        final String url2 = uri2.toString();


                        // Crear la publicación y guardarla en Firestore
                        Publicacion publicacion = new Publicacion(mTitulo.toLowerCase(),
                                Integer.parseInt(mPrecio), mDescripcion,
                                url, url2, mAutentificacionFirebase.getUid(),
                                mCategoria,mSector, new Date().getTime());

                        mPublicacionFribase.save(publicacion).addOnCompleteListener(taskSave -> {

                            if (taskSave.isSuccessful()) {
                                Toast.makeText(PostActivity.this, "La información se almacenó correctamente", Toast.LENGTH_SHORT).show();
                                dialogoEspera.dismiss();
                                finish();
                            } else {
                                Toast.makeText(PostActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                                dialogoEspera.dismiss();
                            }
                        });

                    });
                });
            });
        });
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
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {

                mImageFile = FileUtil.from(this, data.getData());
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                // Asignar la imagen capturada al ImageView
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mAbsolutePhotoPath));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                // Asignar la imagen capturada al ImageView
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mAbsolutePhotoPath2));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMensajeHelper.updateOnline(true,PostActivity.this);
    }



    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false,PostActivity.this);
    }


}