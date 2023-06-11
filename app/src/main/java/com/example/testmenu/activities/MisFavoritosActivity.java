package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
    static RecyclerView recyclerView;
    private ImageButton btnSalir;

    static TextView txtNoHayFavoritos;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mToolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewInicioFavorito);
        btnSalir = findViewById(R.id.volverAtrasFavoritos);
        txtNoHayFavoritos = findViewById(R.id.txtNoHayFavoritos);

        txtNoHayFavoritos.setVisibility(View.VISIBLE);

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

        vacio();
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

    public static void vacio() {
        // Verificar si el RecyclerView y su adaptador no son nulos
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            // Verificar si el RecyclerView está vacío
            if (recyclerView.getAdapter().getItemCount() == 0) {
                // Mostrar el TextView txtNoHayPublicacion si el RecyclerView está vacío
                txtNoHayFavoritos.setVisibility(View.VISIBLE);
            } else {
                // Ocultar el TextView txtNoHayPublicacion si el RecyclerView no está vacío
                txtNoHayFavoritos.setVisibility(View.GONE);
            }
        }
    }
}
