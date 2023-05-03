package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testmenu.R;
import com.example.testmenu.adapters.SliderAdapter;
import com.example.testmenu.entidades.SliderItem;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    PublicacionFirebase mPublicacionFirebase;
    UsuariosBBDDFirebase mUsuariosFribase;

    String mExtraPostId;

    TextView mTextViewTitulo;
    TextView mTextViewDescripcion;
    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewNameCategoria;
    ImageView mImageViewCategoria;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSliderView = findViewById(R.id.imageSlider);
        mTextViewTitulo = findViewById(R.id.textViewTitulo);
        mTextViewDescripcion = findViewById(R.id.textViewDescripcion);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewNameCategoria = findViewById(R.id.textViewNameCategoria);
        mImageViewCategoria = findViewById(R.id.imageViewCategoria);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mButtonShowProfile = findViewById(R.id.btnShowProfile);




        mPublicacionFirebase = new PublicacionFirebase();
        mUsuariosFribase = new UsuariosBBDDFirebase();

        mExtraPostId = getIntent().getStringExtra("id");

        getPost();
    }

    private void instanceSlider() {
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost() {
        mPublicacionFirebase.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("image1")) {
                    String image1 = documentSnapshot.getString("image1");
                    SliderItem item = new SliderItem();
                    item.setImageUrl(image1);
                    mSliderItems.add(item);
                }
                if (documentSnapshot.contains("image2")) {
                    String image2 = documentSnapshot.getString("image2");
                    SliderItem item = new SliderItem();
                    item.setImageUrl(image2);
                    mSliderItems.add(item);
                }
                if(documentSnapshot.contains("titulo")){
                    String titulo = documentSnapshot.getString("titulo");
                    mTextViewTitulo.setText(titulo);
                }
                if(documentSnapshot.contains("descripcion")){
                    String descripcion = documentSnapshot.getString("descripcion");
                    mTextViewDescripcion.setText(descripcion);
                }
                if(documentSnapshot.contains("categoria")){
                    String categoria = documentSnapshot.getString("categoria");
                    mTextViewNameCategoria.setText(categoria);
                }
                if(documentSnapshot.contains("idUser")){
                    String idUser = documentSnapshot.getString("idUser");
                    getUserInfo(idUser);

                }

                instanceSlider();

            }
        });
    }

    private void getUserInfo(String idUser) {
        mUsuariosFribase.getUsuarios(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        String usuario = documentSnapshot.getString("usuario");
                        mTextViewUsername.setText(usuario);
                    }
                    if(documentSnapshot.contains("telefono")){
                        String telefono = documentSnapshot.getString("telefono");
                        mTextViewPhone.setText(telefono);
                    }
                    if(documentSnapshot.contains("fotoPerfil")){
                        String fotoPerfil = documentSnapshot.getString("fotoPerfil");
                        Picasso.get().load(fotoPerfil).into(mCircleImageViewProfile);
                    }

                }
            }
        });
    }
}