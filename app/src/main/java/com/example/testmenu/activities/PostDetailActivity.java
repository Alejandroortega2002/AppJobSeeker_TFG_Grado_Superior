package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testmenu.R;
import com.example.testmenu.adapters.SliderAdapter;
import com.example.testmenu.entidades.SliderItem;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;

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

    private ImageButton btnSalir;
    private String idUser = "";

    @SuppressLint("MissingInflatedId")
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
        btnSalir = findViewById(R.id.volver_inicio);


        mPublicacionFirebase = new PublicacionFirebase();
        mUsuariosFribase = new UsuariosBBDDFirebase();

        mExtraPostId = getIntent().getStringExtra("id");

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostDetailActivity.this,VerPerfilActivity.class);
                i.putExtra("idUser",idUser);
                startActivity(i);

            }
        });

        getPost();
    }

    /**
     * Configura el SliderView y establece el adaptador SliderAdapter para mostrar los elementos del slider.
     * <p>
     * Se establecen diferentes propiedades del SliderView, como la animación de indicador, la dirección de ciclo automático, el tiempo de desplazamiento, etc.
     */
    private void instanceSlider() {
        // Instancia el adaptador SliderAdapter y lo configura para mostrar los elementos del slider
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);

        // Configura la animación de indicador y la animación de transformación del slider
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

        // Establece la dirección de ciclo automático y los colores del indicador
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);

        // Configura el tiempo de desplazamiento y la activación del ciclo automático
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);

        // Inicia el ciclo automático del slider
        mSliderView.startAutoCycle();
    }

    /**
     * Obtiene la publicación correspondiente al ID de la publicación adicional proporcionado como extra en la actividad.
     * <p>
     * Si la publicación existe, se recuperan los detalles de la publicación, incluyendo las imágenes, el título, la descripción, la categoría y el ID del usuario que publicó la publicación.
     * <p>
     * Se crea una instancia del slider para mostrar las imágenes de la publicación.
     */
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
                if (documentSnapshot.contains("titulo")) {
                    String titulo = documentSnapshot.getString("titulo");
                    mTextViewTitulo.setText(titulo);
                }
                if (documentSnapshot.contains("descripcion")) {
                    String descripcion = documentSnapshot.getString("descripcion");
                    mTextViewDescripcion.setText(descripcion);
                }
                if (documentSnapshot.contains("categoria")) {
                    String categoria = documentSnapshot.getString("categoria");
                    mTextViewNameCategoria.setText(categoria);
                }
                if (documentSnapshot.contains("idUser")) {
                     idUser = documentSnapshot.getString("idUser");
                    getUserInfo(idUser);
                }
                instanceSlider();
            }
        });
    }

    /**
     * Obtiene la información de un usuario a partir de su identificador y la muestra en la interfaz de usuario.
     *
     * @param idUser El identificador del usuario a consultar.
     */
    private void getUserInfo(String idUser) {
        mUsuariosFribase.getUsuarios(idUser).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("usuario")) {
                    String usuario = documentSnapshot.getString("usuario");
                    mTextViewUsername.setText(usuario);
                }
                if (documentSnapshot.contains("telefono")) {
                    String telefono = documentSnapshot.getString("telefono");
                    mTextViewPhone.setText(telefono);
                }
                if (documentSnapshot.contains("fotoPerfil")) {
                    String fotoPerfil = documentSnapshot.getString("fotoPerfil");
                    // Carga la imagen de perfil del usuario usando la biblioteca Picasso.
                    Picasso.get().load(fotoPerfil).into(mCircleImageViewProfile);
                }
            }
        });
    }




}