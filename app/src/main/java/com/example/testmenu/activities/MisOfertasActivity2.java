package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.testmenu.R;
import com.example.testmenu.adapters.PostsAdapter;
import com.example.testmenu.adapters.PostsAdapter2;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.utils.ViewedMensajeHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * La clase MisOfertasActivity2 muestra las publicaciones creadas por un usuario específico.
 * Permite al usuario ver y gestionar las publicaciones de otro usuario.
 */
public class MisOfertasActivity2 extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private ImageButton btnSalir;

    private AutentificacioFirebase mAutentificacionFirebase;
    private PublicacionFirebase mPublicacionfirebase;
    private PostsAdapter2 mPostsAdapter2;
    private String idUserCarta;

    /**
     * Método llamado al crear la actividad.
     *
     * @param savedInstanceState Los datos guardados del estado anterior de la actividad.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_ofertas);

        idUserCarta = getIntent().getStringExtra("idUserCarta");
        mToolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewInicio);
        btnSalir = findViewById(R.id.volverAtrasMisOfertas);

        mAutentificacionFirebase = new AutentificacioFirebase();
        mPublicacionfirebase = new PublicacionFirebase();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        btnSalir.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * Método llamado al iniciar la actividad.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Obtener las publicaciones del usuario especificado
        Query query = mPublicacionfirebase.getPublicacionDeUsuario(idUserCarta);
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query, Publicacion.class)
                .build();

        mPostsAdapter2 = new PostsAdapter2(options, this);
        recyclerView.setAdapter(mPostsAdapter2);
        mPostsAdapter2.startListening();
        ViewedMensajeHelper.updateOnline(true, MisOfertasActivity2.this);
    }

    /**
     * Método llamado al detener la actividad.
     */
    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter2.stopListening();
    }

    /**
     * Método llamado al pausar la actividad.
     */
    @Override
    public void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false, MisOfertasActivity2.this);
    }
}
