package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.testmenu.R;
import com.example.testmenu.adapters.PostsAdapter;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class MisOfertasActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    RecyclerView recyclerView;

    AutentificacioFirebase mAutentificacionFirebase;
    PublicacionFirebase mPublicacionfirebase;
    PostsAdapter mPostsAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_ofertas);
        mToolbar = findViewById(R.id.toolbar);
       recyclerView = findViewById(R.id.recyclerViewInicio);

        mAutentificacionFirebase = new AutentificacioFirebase();
        mPublicacionfirebase = new PublicacionFirebase();

        ((AppCompatActivity) this).setSupportActionBar(mToolbar);
        ((AppCompatActivity) this).getSupportActionBar().setTitle("Mis ofertas");
//        onCreateOptionsMenu(true);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        Query query = mPublicacionfirebase.getPublicacionDeUsuario(mAutentificacionFirebase.getUid());
//        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
//                .setQuery(query, Publicacion.class)
//                .build();
//
//        mPostsAdapter = new PostsAdapter(options, this);
//        recyclerView.setAdapter(mPostsAdapter);
//        mPostsAdapter.startListening();
//    }
//
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mPostsAdapter.stopListening();
//    }
}