package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

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

public class MisFavoritosActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    RecyclerView recyclerView;

    AutentificacioFirebase mAutentificacionFirebase;
    PublicacionFirebase mPublicacionfirebase;

    FavoritosFirebase favoritosFirebase;
    PostsAdapter2 mPostsAdapter2;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_favoritos);

        mToolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewInicioFavorito);

        mAutentificacionFirebase = new AutentificacioFirebase();
        mPublicacionfirebase = new PublicacionFirebase();
        favoritosFirebase = new FavoritosFirebase();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ((AppCompatActivity) this).setSupportActionBar(mToolbar);
        ((AppCompatActivity) this).getSupportActionBar().setTitle("Mis Favoritos");
    }

    @Override
    public void onStart() {
        super.onStart();
//        Query query = mPublicacionfirebase.getPublicacionDeUsuario(mAutentificacionFirebase.getUid());
//        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
//                .setQuery(query, Publicacion.class)
//                .build();



        // Obtener los IDs de los posts a los que el usuario ha dado like
        Query query = favoritosFirebase.getLikesByUser(mAutentificacionFirebase.getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> postIds = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Favoritos favoritos = document.toObject(Favoritos.class);
                    postIds.add(favoritos.getIdPost());
                }

                // Obtener la información detallada de cada post
                FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                        .setQuery(mPublicacionfirebase.getPostByIdList(postIds), Publicacion.class)
                        .build();

                mPostsAdapter2 = new PostsAdapter2(options, this);
                recyclerView.setAdapter(mPostsAdapter2);
                mPostsAdapter2.startListening();
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter2.stopListening();
    }

}