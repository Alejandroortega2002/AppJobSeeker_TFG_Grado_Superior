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
 * La clase MisOfertasActivity muestra las publicaciones creadas por el usuario.
 * Permite al usuario ver y gestionar sus propias publicaciones.
 */
public class MisOfertasActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    RecyclerView recyclerView;

    ImageButton btnSalir;

    AutentificacioFirebase mAutentificacionFirebase;
    PublicacionFirebase mPublicacionfirebase;
    PostsAdapter2 mPostsAdapter2;

    /**
     * Método llamado al crear la actividad.
     *
     * @param savedInstanceState Los datos guardados del estado anterior de la actividad.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_ofertas);

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

        // Obtener las publicaciones del usuario actual
        Query query = mPublicacionfirebase.getPublicacionDeUsuario(mAutentificacionFirebase.getUid());
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query, Publicacion.class)
                .build();

        mPostsAdapter2 = new PostsAdapter2(options, this);
        recyclerView.setAdapter(mPostsAdapter2);
        mPostsAdapter2.startListening();
        ViewedMensajeHelper.updateOnline(true, MisOfertasActivity.this);
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
    protected void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false, MisOfertasActivity.this);
    }
}
