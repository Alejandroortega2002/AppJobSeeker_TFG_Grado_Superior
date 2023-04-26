package com.example.testmenu.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


    TextView mTextViewCategoria;
    File mImageFile;

    String mCategoria="";
    String mTitulo ="";
    String mPrecio="";
    String mDescripcion = "";
    private final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImagenFirebase = new ImagenFirebase();
        mPublicacionFribase = new PublicacionFirebase();
        mAutentificacionFirebase = new AutentificacioFirebase();


        mImageViewPost1 = findViewById(R.id.imagePost1);
        mButtonPost = findViewById(R.id.btnPost);
        mTextInputTitulo = findViewById(R.id.textInputnombrePublicacion);
        mTextInputPrecio = findViewById(R.id.textInputPagar);
        mTextInputDescripcion = findViewById(R.id.textInputDescription);
        mImageviewContrato1 = findViewById(R.id.contrato1);
        mImageviewContrato2 = findViewById(R.id.contrato2);
        mImageviewContrato3 = findViewById(R.id.contrato3);
        mImageviewContrato4 = findViewById(R.id.contrato4);
        mTextViewCategoria = findViewById(R.id.textViewCategoria);






        mButtonPost.setOnClickListener(view -> clickPost());
        mImageViewPost1.setOnClickListener(view -> openGallery());

        mImageviewContrato1.setOnClickListener(view -> mCategoria = "Contrato 1");
        mImageviewContrato2.setOnClickListener(view -> mCategoria = "Contrato 2");
        mImageviewContrato3.setOnClickListener(view -> mCategoria = "Contrato 3");
        mImageviewContrato4.setOnClickListener(view -> mCategoria = "Contrato 4");


    }

    private void clickPost() {
         mTitulo = mTextInputTitulo.getText().toString();
         mPrecio = mTextInputPrecio.getText().toString();
         mDescripcion = mTextInputDescripcion.getText().toString();

        if(!mTitulo.isEmpty() && !mPrecio.isEmpty() && !mDescripcion.isEmpty()){
            if(mImageFile!=null){
                saveImage();
            }else {
                Toast.makeText(this,"Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        mImagenFirebase.save(PostActivity.this,mImageFile).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                mImagenFirebase.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    String url =  uri.toString();
                    Publicacion publicacion = new Publicacion();
                    publicacion.setImage1(url);
                    publicacion.setTitulo(mTitulo);
                    publicacion.setPrecio(Integer.parseInt(mPrecio));
                    publicacion.setDescripcion(mDescripcion);
                    publicacion.setCategoria(mCategoria);
                    publicacion.setIdUser(mAutentificacionFirebase.getUid());

                    mPublicacionFribase.save(publicacion).addOnCompleteListener(taskSave -> {
                        if(taskSave.isSuccessful()){
                            Toast.makeText(PostActivity.this, "La información se almaceno correctamente", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PostActivity.this, "La información no se pudo  almacenar correctamente", Toast.LENGTH_SHORT).show();

                        }
                    });
                });

            }else{
                Toast.makeText(PostActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            mImageFile = FileUtil.from(PostActivity.this, result.getData().getData());
                            mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                        } catch (Exception e) {
                            Log.d("ERROR", "se produjo un error" + e.getMessage());
                            Toast.makeText(PostActivity.this, "se produjo un error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);

    }


}


