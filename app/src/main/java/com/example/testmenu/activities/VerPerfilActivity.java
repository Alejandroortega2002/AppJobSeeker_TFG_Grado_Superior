package com.example.testmenu.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testmenu.R;
import com.example.testmenu.databinding.FragmentPerfilBinding;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerPerfilActivity extends AppCompatActivity {

    private String VPidUser;

    private TextView telefono, nombreU, email, descripcion, numeroDeOrfetas;

    private ImageView fotoBanner;
    private CircleImageView fotoPerfil;

    private ImageButton btnAjustes, btnSalir;

    private Button btnverOfertas, btnverFavortitos;

    AutentificacioFirebase autentificacioFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;
    PublicacionFirebase publicacionFirebase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_perfil);
        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        publicacionFirebase = new PublicacionFirebase();

        btnAjustes = findViewById(R.id.VPbtnAjustes);
        btnSalir = findViewById(R.id.VPvolver_perfil);
        btnverFavortitos = findViewById(R.id.VPbtnFavoritos);
        btnverOfertas = findViewById(R.id.VPbtnMisOfertas);

        telefono = findViewById(R.id.VPnTelefono);
        nombreU = findViewById(R.id.VPnPerfil);
        email = findViewById(R.id.VPpEmail);
        descripcion = findViewById(R.id.VPpDescripcion);
        numeroDeOrfetas = findViewById(R.id.VPnPublicaciones);
        fotoBanner = findViewById(R.id.VPbanner);
        fotoPerfil = findViewById(R.id.VPfotoPerfil);
        VPidUser = getIntent().getStringExtra("idUser");
        checkUser();
        rellenarInformacionUsuario();
        getNumeroPublicaciones();


        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAjustes();
            }
        });


        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnverOfertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VerPerfilActivity.this,MisOfertasActivity.class);
                i.putExtra("idCarta",VPidUser);
                startActivity(i);
            }
        });
    }

    public void irAjustes() {
        Intent i = new Intent(this, AjustesActivity.class);
        startActivity(i);
    }


    public void rellenarInformacionUsuario() {
        DocumentReference documentReference = usuariosBBDDFirebase.refereciaColeccion(VPidUser);
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
                    String correo = value.getString("email");
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
                        nombreU.setText(nombre);
                    } else {
                        nombreU.setText("Sin nombre");
                    }
                    if (correo != null) {
                        email.setText(correo);
                    } else {
                        email.setText("Sin correo");
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

    public void getNumeroPublicaciones() {
        publicacionFirebase.getPublicacionDeUsuario(VPidUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPublicaciones = queryDocumentSnapshots.size();
                String numeroOfertas = String.valueOf(numeroPublicaciones);
                numeroDeOrfetas.setText(numeroOfertas);
            }
        });
    }

    public void checkUser(){
        if (!VPidUser.equals(autentificacioFirebase.getUid())){
            btnAjustes.setClickable(false);
            btnAjustes.setVisibility(View.INVISIBLE);
            btnverFavortitos.setClickable(false);
            btnverFavortitos.setVisibility(View.GONE);
            btnverOfertas.setText("Ver Ofertas");
            btnverOfertas.setX(30);
        }
    }
}