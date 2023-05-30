package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.testmenu.R;
import com.example.testmenu.adapters.PostsAdapter2;
import com.example.testmenu.entidades.Favoritos;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.FavoritosFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase MisFavoritosActivity muestra las publicaciones marcadas como favoritas por el usuario.
 * Permite al usuario ver y navegar por las publicaciones que ha marcado como favoritas.
 */
public class MisFavoritosActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView recyclerView;

    private ImageButton btnSalir;

    private AutentificacioFirebase mAutentificacionFirebase;
    private PublicacionFirebase mPublicacionfirebase;

    private FavoritosFirebase favoritosFirebase;
    private PostsAdapter2 mPostsAdapter2;

    private List<String> postIds;
    private Favoritos favoritos;

    /**
     * Método llamado al crear la actividad.
     *
     * @param savedInstanceState Los datos guardados del estado anterior de la actividad.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_favoritos);

        mToolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewInicioFavorito);
        btnSalir = findViewById(R.id.volverAtrasFavoritos);

        postIds = new ArrayList<>();
        mAutentificacionFirebase = new AutentificacioFirebase();
        mPublicacionfirebase = new PublicacionFirebase();
        favoritosFirebase = new FavoritosFirebase();

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

        // Obtener los IDs de los posts a los que el usuario ha dado like
        Query query = favoritosFirebase.getLikesByUser(mAutentificacionFirebase.getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    favoritos = document.toObject(Favoritos.class);
                    postIds.add(favoritos.getIdPost());
                }
            }
            if (!postIds.isEmpty()) {
                // Obtener la información detallada de cada post
                Query queryPublicaciones = mPublicacionfirebase.getPostByIdList(postIds);
                FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                        .setQuery(queryPublicaciones, Publicacion.class)
                        .build();

                mPostsAdapter2 = new PostsAdapter2(options, this);
                recyclerView.setAdapter(mPostsAdapter2);
                mPostsAdapter2.startListening();
            }
        });
    }

    /**
     * Método llamado al detener la actividad.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mPostsAdapter2 != null) {
            mPostsAdapter2.stopListening();
        }
    }
}
