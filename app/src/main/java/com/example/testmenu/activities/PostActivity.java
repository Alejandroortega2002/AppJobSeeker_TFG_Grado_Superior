package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.testmenu.R;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.ImagenFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import de.hdodenhof.circleimageview.CircleImageView;

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
    File mImageFile;
    File mImageFile2;

    String mCategoria = "";
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
    File mPhotoFile;
    //FOTO2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImagenFirebase = new ImagenFirebase();
        mPublicacionFribase = new PublicacionFirebase();
        mAutentificacionFirebase = new AutentificacioFirebase();


        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Imagen de galeria", "Tomar foto"};


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

    private void selectOptionImagen(final int numberImage) {

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
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.example.testmenu", photoFile);
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
        if (requestCode == PHOTO_REQUEST_CODE) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        } else if (requestCode == PHOTO_REQUEST_CODE_2) {
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    private void clickPost() {
        mTitulo = mTextInputTitulo.getText().toString().trim();
        mPrecio = mTextInputPrecio.getText().toString().trim();
        mDescripcion = mTextInputDescripcion.getText().toString().trim();

        if (!mTitulo.isEmpty() && !mPrecio.isEmpty() && !mDescripcion.isEmpty()) {
            //SELECCIONO AMBAS IMAGENES DE LA GALERIA
            if (mImageFile != null && mImageFile != null) {
                saveImage(mImageFile, mImageFile2);
            }
            //TOMO AMBAS FOTOS DE LA CAMARA
            else if (mPhotoFile != null && mPhotoFile2 != null) {
                saveImage(mPhotoFile, mPhotoFile2);
            }
            //SELECIONO UNA DE LA GALERIA Y TOMO FOTO EN LA OTRA
            else if (mImageFile != null && mPhotoFile2 != null) {
                saveImage(mImageFile, mPhotoFile2);
            }
            //TOMO FOTO EN UNA Y SELECCIONO IMAGEN DE LA GALERIA
            else if(mPhotoFile !=null && mImageFile2!=null){
                saveImage(mPhotoFile,mImageFile2);
            }else {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveImage(File imageFile1, final File imageFile2) {

        mImagenFirebase.save(PostActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImagenFirebase.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();

                            mImagenFirebase.save(PostActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()) {
                                        mImagenFirebase.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2 = uri2.toString();
                                                Publicacion publicacion = new Publicacion();
                                                publicacion.setImage1(url);
                                                publicacion.setImage2(url2);
                                                publicacion.setTitulo(mTitulo);
                                                publicacion.setPrecio(Integer.parseInt(mPrecio));
                                                publicacion.setDescripcion(mDescripcion);
                                                publicacion.setCategoria(mCategoria);
                                                publicacion.setIdUser(mAutentificacionFirebase.getUid());
                                                publicacion.setTimeStamp(new Date().getTime());
                                                mPublicacionFribase.save(publicacion).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {

                                                        if (taskSave.isSuccessful()) {

                                                            Toast.makeText(PostActivity.this, "La informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            Toast.makeText(PostActivity.this, "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else {

                                        Toast.makeText(PostActivity.this, "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else {

                    Toast.makeText(PostActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
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

                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        /**
         * SELECCION DE FOTOGRAFIA
         */
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.get().load(mPhotoPath).into(mImageViewPost1);
        }

        /**
         * SELECCION DE FOTOGRAFIA
         */
        if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.get().load(mPhotoPath2).into(mImageViewPost2);
        }
    }


}


