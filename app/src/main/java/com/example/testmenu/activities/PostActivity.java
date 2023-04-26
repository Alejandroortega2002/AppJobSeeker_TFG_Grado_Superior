package com.example.testmenu.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.io.File;

public class PostActivity extends AppCompatActivity {

    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    File mImageFile1;
    File mImageFile2;
    Button mButtonPost;
    ImagenFirebase mImagenFirebase;
    PublicacionFirebase mPublicacionFirebase;
    AutentificacioFirebase mAutenticacionFirebase;
    TextInputEditText mTextInpuntNombrePublicacion;
    TextInputEditText mTextInpuntPrecio;
    TextInputEditText mTextInpuntDescripcion;
    ImageView mImageViewContrato1;
    ImageView mImageViewContrato2;
    ImageView mImageViewContrato3;
    ImageView mImageViewContrato4;
    TextView mTextViewCategoria;

    String mCategoria = "";
    String mTitle = "";
    String mPrecio = "";
    String mDescripcion = "";


    private final int GALLERY_REQUEST_CODE_1 = 1;
    private final int GALLERY_REQUEST_CODE_2 = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImagenFirebase = new ImagenFirebase();
        mPublicacionFirebase  = new PublicacionFirebase();
        mAutenticacionFirebase = new AutentificacioFirebase();

        mImageViewPost1 = findViewById(R.id.imagePost1);
        mImageViewPost2 = findViewById(R.id.imagePost2);
        mButtonPost = findViewById(R.id.btnPost);
        mTextInpuntNombrePublicacion = findViewById(R.id.textInputnombrePublicacion);
        mTextInpuntPrecio = findViewById(R.id.textInputPagar);
        mTextInpuntDescripcion = findViewById(R.id.textInputDescription);
        mImageViewContrato1 = findViewById(R.id.contrato1);
        mImageViewContrato2 = findViewById(R.id.contrato2);
        mImageViewContrato3 = findViewById(R.id.contrato3);
        mImageViewContrato4 = findViewById(R.id.contrato4);
        mTextViewCategoria = findViewById(R.id.textViewCategoria);


        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });

        mImageViewPost1.setOnClickListener(view -> openGallery(GALLERY_REQUEST_CODE_1));

        mImageViewPost2.setOnClickListener(view -> openGallery(GALLERY_REQUEST_CODE_2));


        mImageViewContrato1.setOnClickListener(view -> mCategoria = "Indefinido");
        mImageViewContrato2.setOnClickListener(v -> mCategoria = "Temporal");
        mImageViewContrato3.setOnClickListener(v -> mCategoria = "Media Jornada");
        mImageViewContrato4.setOnClickListener(v -> mCategoria = "Eventual");
    }

    private void clickPost() {
       mTitle = mTextInpuntNombrePublicacion.getText().toString();
       mPrecio = mTextInpuntPrecio.getText().toString();
        mDescripcion = mTextInpuntDescripcion.getText().toString();

        if(!mTitle.isEmpty() && !mPrecio.isEmpty() && !mDescripcion.isEmpty() && !mCategoria.isEmpty()){
            if(mImageFile1!=null){
                saveImage();
            }

        }else{
            Toast.makeText(this,"Completa los campos para publicar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        if (mImageFile1 == null && mImageFile2 == null) {
            Toast.makeText(PostActivity.this, "Debes seleccionar al menos una imagen", Toast.LENGTH_LONG).show();
            return;
        }
        if (mImageFile1 != null) {
            mImagenFirebase.save(PostActivity.this, mImageFile1).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    mImagenFirebase.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Publicacion publicacion = new Publicacion();
                            publicacion.setImage1(url);
                            publicacion.setTitulo(mTitle);
                            publicacion.setPrecio(Integer.parseInt(mPrecio));
                            publicacion.setDescripcion(mDescripcion);
                            publicacion.setCategoria(mCategoria);
                            publicacion.setIdUser(mAutenticacionFirebase.getUid());
                            mPublicacionFirebase.save(publicacion).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskSave) {
                                    if(taskSave.isSuccessful()){
                                        Toast.makeText(PostActivity.this, "La información se almacenó  correctamente",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(PostActivity.this, "No se almacenó correctamente la información",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                        }
                    });
                }else{
                    Toast.makeText(PostActivity.this,"Hubo un error al alamacenar la imagen 1", Toast.LENGTH_LONG).show();
                }
            });
        }
        if (mImageFile2 != null) {
            mImagenFirebase.save(PostActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(PostActivity.this, "La imagen 2 se almaceno correctamente",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(PostActivity.this,"Hubo un error al alamacenar la imagen 2", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    ActivityResultLauncher<Intent> galleryLauncher1 = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            mImageFile1 = FileUtil.from(PostActivity.this, result.getData().getData());
                            mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile1.getAbsolutePath()));
                        } catch (Exception e) {
                            Log.d("Error", "se produjo un error" + e.getMessage());
                            Toast.makeText(PostActivity.this, "se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> galleryLauncher2 = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            mImageFile2 = FileUtil.from(PostActivity.this, result.getData().getData());
                            mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
                        } catch (Exception e) {
                            Log.d("Error", "se produjo un error" + e.getMessage());
                            Toast.makeText(PostActivity.this, "se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
    );

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        if (requestCode == GALLERY_REQUEST_CODE_1) {
            galleryLauncher1.launch(galleryIntent);
        } else if (requestCode == GALLERY_REQUEST_CODE_2) {
            galleryLauncher2.launch(galleryIntent);
        }
    }

}
