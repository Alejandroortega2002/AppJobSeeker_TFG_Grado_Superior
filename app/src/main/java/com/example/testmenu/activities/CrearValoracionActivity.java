package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Valoraciones;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.firebase.ValoracionFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CrearValoracionActivity extends AppCompatActivity {

    private String idUser;
    private CircleImageView fotoPerfil;
    private TextView nombreUser;
    private RatingBar estrellasCrear;
    private EditText escribirValoracion;
    private Button enviar;
    AutentificacioFirebase autentificacioFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;

    ValoracionFirebase valoracionFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_valoracion);

        idUser = getIntent().getStringExtra("idUser");
        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();

        fotoPerfil = findViewById(R.id.fotoUsuarioCrearValoracion);
        nombreUser = findViewById(R.id.nombreUsuarioCrearValoracion);
        estrellasCrear = findViewById(R.id.crearEstrellas);
        escribirValoracion = findViewById(R.id.editEscribirValoracion);
        enviar = findViewById(R.id.btnEnviarValoracion);

        String userId = autentificacioFirebase.getUid();

        valoracionFirebase = new ValoracionFirebase();

        cargarDetallesUsuario(userId);

        enviar.setOnClickListener(v -> {
            meterDatos(idUser);
            finish();
        });
    }

    private void meterDatos(String idUser) {
        String ratings = String.valueOf(estrellasCrear.getRating());
        String valoracion = escribirValoracion.getText().toString().trim();

        String timestamp = String.valueOf(System.currentTimeMillis());

        Valoraciones v = new Valoraciones();
        v.setNota(ratings);
        v.setValoracion(valoracion);
        v.setUserId(idUser);
        v.setUserPostId(autentificacioFirebase.getUid());
        v.setTimeStamp(timestamp);

        valoracionFirebase.create(v).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CrearValoracionActivity.this, "El comentario se creo correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CrearValoracionActivity.this, "No se pudo crear el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarDetallesUsuario(String userId) {
        if (userId != null) {

            usuariosBBDDFirebase.getUsuarios(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("usuario")) {
                            String nUsuarioActivity = documentSnapshot.getString("usuario");
                            nombreUser.setText("@" + nUsuarioActivity.toUpperCase());
                        }
                        if (documentSnapshot.contains("fotoPerfil")) {
                            String fotoPerfilActivity = documentSnapshot.getString("fotoPerfil");
                            if (fotoPerfilActivity != null) {
                                // Carga la imagen de perfil del usuario usando la biblioteca Picasso.
                                Picasso.get().load(fotoPerfilActivity).into(fotoPerfil);
                            }
                        }
                    }
                }
            });
        }

    }
}