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
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;

import com.example.testmenu.utils.ViewedMensajeHelper;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    private SliderView mSliderView;
    private SliderAdapter mSliderAdapter;
    private List<SliderItem> mSliderItems = new ArrayList<>();
    private PublicacionFirebase mPublicacionFirebase;
    private UsuariosBBDDFirebase mUsuariosFribase;
    private AutentificacioFirebase mAutentificacioFirebase;
    private String mExtraPostId;
    private TextView mTextViewTitulo;
    private TextView mTextViewDescripcion;
    private TextView mTextViewUsername;
    private TextView mTextViewPhone;
    private TextView mTextViewNameCategoria;
    private ImageView mImageViewCategoria;
    private CircleImageView mCircleImageViewProfile;
    private Button mButtonShowProfile;
    private ImageButton btnChat;
    private ImageButton btnSalir;
    private String idUser = "";

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        btnChat = findViewById(R.id.btnChat);


        mPublicacionFirebase = new PublicacionFirebase();
        mUsuariosFribase = new UsuariosBBDDFirebase();
        mAutentificacioFirebase = new AutentificacioFirebase();

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
                Intent i = new Intent(PostDetailActivity.this, VerPerfilActivity.class);
                i.putExtra("idUser", idUser);
                startActivity(i);

            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity();
            }
        });

        getPost();


    }

    /**
     * Intent que dirige al <b>ChatActivity</b>
     *
     * @return void
     */
    public void goToChatActivity() {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("idUser1", mAutentificacioFirebase.getUid());
        i.putExtra("idUser2", idUser);
        startActivity(i);

    }

    /**
     * Configura el SliderView y establece el adaptador SliderAdapter para mostrar los elementos del slider.
     * <p>
     * Se establecen diferentes propiedades del SliderView, como la animación de indicador, la dirección de ciclo automático,
     * el tiempo de desplazamiento, etc.
     *
     * @return void
     */
    public void instanceSlider() {
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
    public void getPost() {
        // Obtener la publicación por su ID desde Firebase Firestore
        mPublicacionFirebase.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
            // Verificar si la publicación existe
            if (documentSnapshot.exists()) {
                // Obtener la URL de la primera imagen y agregarla al slider
                if (documentSnapshot.contains("image1")) {
                    String image1 = documentSnapshot.getString("image1");
                    SliderItem item = new SliderItem();
                    item.setImageUrl(image1);
                    mSliderItems.add(item);
                }
                // Obtener la URL de la segunda imagen y agregarla al slider
                if (documentSnapshot.contains("image2")) {
                    String image2 = documentSnapshot.getString("image2");
                    SliderItem item = new SliderItem();
                    item.setImageUrl(image2);
                    mSliderItems.add(item);
                }
                // Obtener el título y establecerlo en el TextView correspondiente
                if (documentSnapshot.contains("titulo")) {
                    String titulo = documentSnapshot.getString("titulo");
                    mTextViewTitulo.setText(titulo);
                }
                // Obtener la descripción y establecerla en el TextView correspondiente
                if (documentSnapshot.contains("descripcion")) {
                    String descripcion = documentSnapshot.getString("descripcion");
                    mTextViewDescripcion.setText(descripcion);
                }
                // Obtener la categoría y establecerla en el TextView correspondiente
                if (documentSnapshot.contains("categoria")) {
                    String categoria = documentSnapshot.getString("categoria");
                    mTextViewNameCategoria.setText(categoria);
                }
                // Obtener el ID de usuario propietario de la publicación
                if (documentSnapshot.contains("idUser")) {
                    idUser = documentSnapshot.getString("idUser");
                    // Verificar si el usuario actual es el propietario de la publicación y ocultar el botón de chat
                    if (mAutentificacioFirebase.getUid().equals(idUser)) {
                        btnChat.setVisibility(View.GONE);
                    }
                    // Obtener la información del usuario propietario de la publicación
                    getUserInfo(idUser);
                }
                // Configurar el slider con las imágenes obtenidas
                instanceSlider();
            }
        });
    }

    /**
     * Obtiene la información de un usuario a partir de su identificador y la muestra en la interfaz de usuario.
     *
     * @param idUser El identificador del usuario a consultar.
     */
    public void getUserInfo(String idUser) {
        // Obtener la información del usuario por su ID desde Firebase Firestore
        mUsuariosFribase.getUsuarios(idUser).addOnSuccessListener(documentSnapshot -> {
            // Verificar si la información del usuario existe
            if (documentSnapshot.exists()) {
                // Obtener el nombre de usuario y establecerlo en el TextView correspondiente
                if (documentSnapshot.contains("usuario")) {
                    String usuario = documentSnapshot.getString("usuario");
                    mTextViewUsername.setText(usuario);
                }
                // Obtener el número de teléfono y establecerlo en el TextView correspondiente
                if (documentSnapshot.contains("telefono")) {
                    String telefono = documentSnapshot.getString("telefono");
                    mTextViewPhone.setText(telefono);
                }
                // Obtener la URL de la foto de perfil y cargarla en el ImageView correspondiente usando Picasso
                if (documentSnapshot.contains("fotoPerfil")) {
                    String fotoPerfil = documentSnapshot.getString("fotoPerfil");
                    Picasso.get().load(fotoPerfil).into(mCircleImageViewProfile);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewedMensajeHelper.updateOnline(true, PostDetailActivity.this);
    }


    @Override
    public void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false, PostDetailActivity.this);
    }


}