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
import android.graphics.Bitmap;
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

    private ImageView mImageViewPost1;
    private ImageView mImageViewPost2;
    private Button mButtonPost;
    private ImagenFirebase mImagenFirebase;
    private PublicacionFirebase mPublicacionFribase;
    private AutentificacioFirebase mAutentificacionFirebase;
    private TextInputEditText mTextInputTitulo;
    private TextInputEditText mTextInputPrecio;
    private TextInputEditText mTextInputDescripcion;
    private ImageView mImageviewContrato1;
    private ImageView mImageviewContrato2;
    private ImageView mImageviewContrato3;
    private ImageView mImageviewContrato4;
    private CircleImageView mCircleBack;
    private TextView mTextViewCategoria;
    private Spinner mTextViewSector;
    private File mImageFile;
    private File mImageFile2;
    private String mCategoria = "";
    private String mSector = "";
    private String mTitulo = "";
    private String mPrecio = "";
    private String mDescripcion = "";
    private AlertDialog.Builder mBuilderSelector;
    private CharSequence options[];

    private int GALLERY_REQUEST_CODE = 1;
    private int GALLERY_REQUEST_CODE_2 = 2;
    private int PHOTO_REQUEST_CODE = 3;
    private int PHOTO_REQUEST_CODE_2 = 4;

    //FOTO1
    private String mAbsolutePhotoPath;
    private String mPhotoPath;

    //FOTO2
    private String mAbsolutePhotoPath2;
    private String mPhotoPath2;
    private AlertDialog dialogoEspera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

    /**
     * Método llamado cuando se obtienen los resultados de la solicitud de permisos.
     *
     * @param requestCode  El código de solicitud de permisos.
     * @param permissions  Los permisos solicitados.
     * @param grantResults Los resultados de la solicitud de permisos.
     */
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
    public void selectOptionImagen(int numberImage) {
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
     * @param requestCode el código de solicitud que se utiliza para identificar la solicitud.
     * @return void
     */
    @SuppressLint("QueryPermissionsNeeded")
    public void takePhoto(int requestCode) {
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

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.example.testmenu", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                // Asigna el archivo de imagen a la variable correspondiente
                if (requestCode == PHOTO_REQUEST_CODE) {
                    mImageFile = photoFile;
                } else if (requestCode == PHOTO_REQUEST_CODE_2) {
                    mImageFile2 = photoFile;
                }

                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }


    /**
     * Crea un archivo de foto temporal con un nombre de archivo único utilizando la fecha y hora actuales.
     *
     * @param requestCode el código de solicitud para la foto, para guardar la ruta absoluta del archivo
     * @return archivo de imagen creado
     * @throws IOException si hay algún error al crear el archivo
     */
    public File createPhotoFile(int requestCode) throws IOException {
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
     * en la base de datos.
     *
     * @return void
     */

    public void clickPost() {
        // Obtener los valores de los campos de entrada
        mTitulo = mTextInputTitulo.getText().toString().trim();
        mPrecio = mTextInputPrecio.getText().toString().trim();
        mDescripcion = mTextInputDescripcion.getText().toString().trim();
        mSector = mTextViewSector.getSelectedItem().toString().trim();

        // Verificar si algún campo está vacío o no se ha seleccionado un sector
        if (mTitulo.isEmpty() || mPrecio.isEmpty() || mDescripcion.isEmpty() || mSector.equals("Sector")) {
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si se ha seleccionado ambas imágenes
        if (mImageFile == null || mImageFile2 == null) {
            Toast.makeText(this, "Debes seleccionar ambas imágenes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar el diálogo de espera
        dialogoEspera.show();

        // Guardar las imágenes
        saveImage(mImageFile, mImageFile2);
    }


    /**
     * Este método se encarga de guardar las imágenes en Firebase Storage y luego guardar la información
     * de la publicación en Firestore.
     *
     * @param imageFile1 archivo de imagen 1 a guardar
     * @param imageFile2 archivo de imagen 2 a guardar
     */

    public void saveImage(File imageFile1, File imageFile2) {
        // Guardar la primera imagen
        mImagenFirebase.save(PostActivity.this, imageFile1).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(PostActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_LONG).show();
                return;
            }
            // Obtener la URL de la primera imagen guardada
            mImagenFirebase.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();

                // Guardar la segunda imagen
                mImagenFirebase.save(PostActivity.this, imageFile2).addOnCompleteListener(taskImage2 -> {
                    if (!taskImage2.isSuccessful()) {
                        Toast.makeText(PostActivity.this, "La segunda imagen no se pudo guardar", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Obtener la URL de la segunda imagen guardada
                    mImagenFirebase.getStorage().getDownloadUrl().addOnSuccessListener(uri2 -> {
                        String url2 = uri2.toString();

                        // Crear la publicación y guardarla en Firestore
                        Publicacion publicacion = new Publicacion(mTitulo.toLowerCase(),
                                Integer.parseInt(mPrecio), mDescripcion,
                                url, url2, mAutentificacionFirebase.getUid(),
                                mCategoria, mSector, new Date().getTime());

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

    /**
     * Método que se llama cuando se recibe un resultado de una actividad iniciada por un intent.
     *
     * @param requestCode El código de solicitud utilizado para iniciar la actividad.
     * @param resultCode  El código de resultado devuelto por la actividad.
     * @param data        El intent que contiene el resultado de la actividad.
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Selección de imagen desde la galería
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                // Obtener el archivo de imagen seleccionado desde la galería
                mImageFile = FileUtil.from(this, data.getData());
                // Mostrar la imagen en el ImageView correspondiente
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                // Obtener el archivo de imagen seleccionado desde la galería
                mImageFile2 = FileUtil.from(this, data.getData());
                // Mostrar la imagen en el ImageView correspondiente
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        // Captura de foto desde la cámara
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                // Asignar la imagen capturada al ImageView correspondiente
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mAbsolutePhotoPath));

            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                // Asignar la imagen capturada al ImageView correspondiente
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mAbsolutePhotoPath2));

            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        // Manejo adicional de la selección de imágenes desde la galería
        if (data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            File imageFile = null;
            try {
                // Obtener el archivo de imagen seleccionado desde la galería
                imageFile = FileUtil.from(this, imageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (requestCode == GALLERY_REQUEST_CODE) {
                // Asignar el archivo de imagen al objeto mImageFile correspondiente
                mImageFile = imageFile;
            } else if (requestCode == GALLERY_REQUEST_CODE_2) {
                // Asignar el archivo de imagen al objeto mImageFile2 correspondiente
                mImageFile2 = imageFile;
            }

            try {
                // Mostrar la imagen en el ImageView correspondiente
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (requestCode == GALLERY_REQUEST_CODE) {
                    mImageViewPost1.setImageBitmap(bitmap);
                } else if (requestCode == GALLERY_REQUEST_CODE_2) {
                    mImageViewPost2.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        ViewedMensajeHelper.updateOnline(true, PostActivity.this);
    }


    @Override
    public void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false, PostActivity.this);
    }


}