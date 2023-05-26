package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.adapters.ValoracionesAdapter;
import com.example.testmenu.entidades.FCMBody;
import com.example.testmenu.entidades.FCMResponse;
import com.example.testmenu.entidades.Valoraciones;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.firebase.ValoracionFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ValoracionActivity extends AppCompatActivity {

    private String userId;

    private TextView txtValoracion, txtNombreUsuario;

    private RatingBar estrellas;

    private CircleImageView fotoPerfilValoracion;

    private RecyclerView reciclerValoraciones;

    AutentificacioFirebase autentificacioFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;

    private ArrayList<Valoraciones> listaValoraciones;
    private Dialog customDialog;

    private ValoracionesAdapter valoracionesAdapter;

    private ImageButton btnSalir, btnCrearValoracion;

    ValoracionFirebase valoracionFirebase;
    private float suma;

    private float ratingSum = 0;


    /**
     * Método que se llama al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoracion);


        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();

        valoracionFirebase = new ValoracionFirebase();

        userId = getIntent().getStringExtra("idUsuario");

        txtNombreUsuario = findViewById(R.id.nombreUsuario);
        fotoPerfilValoracion = findViewById(R.id.fotoUsuario);
        estrellas = findViewById(R.id.estrellas);
        txtValoracion = findViewById(R.id.txtValoracion);
        btnCrearValoracion = findViewById(R.id.btnValorar);
        reciclerValoraciones = findViewById(R.id.recyclerViewValoraciones);
        btnSalir = findViewById(R.id.volverAtrasValoraciones);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ValoracionActivity.this);
        reciclerValoraciones.setLayoutManager(linearLayoutManager);

        btnSalir.setOnClickListener(view -> {
            finish();
        });

        btnCrearValoracion.setOnClickListener(view -> {
            CrearValoracionDialog dialog = new CrearValoracionDialog(userId);
            dialog.show(getSupportFragmentManager(), "CrearValoracionDialog");
        });

        cargarDetallesUsuario();
        cargarValoraciones();
    }

    /**
     * Método para cargar las valoraciones del usuario y calcular su media.
     */
    private void cargarValoraciones() {
        valoracionFirebase.getCommentsByUser(userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaValoraciones = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Valoraciones valoracion = document.toObject(Valoraciones.class);
                    float rating = Float.parseFloat(valoracion.getNota());
                    suma = suma + rating;
                }

                long numeroValoraciones = queryDocumentSnapshots.size();
                float media = suma / numeroValoraciones;

                txtValoracion.setText(String.format("%.2f", media) + " [" + numeroValoraciones + "]");
                estrellas.setRating(media);

                usuariosBBDDFirebase.updateMedia(userId, media).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Actualización de la media del usuario exitosa.
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejo de la falla en la obtención de las valoraciones.
            }
        });
    }

    /**
     * Método que se llama al iniciar la actividad.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Query query = valoracionFirebase.getCommentsByUser(userId);
        FirestoreRecyclerOptions<Valoraciones> options =
                new FirestoreRecyclerOptions.Builder<Valoraciones>()
                        .setQuery(query, Valoraciones.class)
                        .build();
        valoracionesAdapter = new ValoracionesAdapter(options, ValoracionActivity.this);
        reciclerValoraciones.setAdapter(valoracionesAdapter);
        valoracionesAdapter.startListening();
    }

    /**
     * Método que se llama al detener la actividad.
     */
    @Override
    protected void onStop() {
        super.onStop();
        valoracionesAdapter.stopListening();
    }

    /**
     * Método que se llama al reanudar la actividad.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Método para cargar los detalles del usuario (nombre de usuario y foto de perfil) en la vista.
     */
    private void cargarDetallesUsuario() {
        if (userId != null) {
            usuariosBBDDFirebase.getUsuarios(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("usuario")) {
                            String nUsuarioActivity = documentSnapshot.getString("usuario");
                            txtNombreUsuario.setText("@" + nUsuarioActivity.toUpperCase());
                        }
                        if (documentSnapshot.contains("fotoPerfil")) {
                            String fotoPerfilActivity = documentSnapshot.getString("fotoPerfil");
                            if (fotoPerfilActivity != null) {
                                Picasso.get().load(fotoPerfilActivity).into(fotoPerfilValoracion);
                            }
                        }
                    }
                }
            });
        }
    }
}
