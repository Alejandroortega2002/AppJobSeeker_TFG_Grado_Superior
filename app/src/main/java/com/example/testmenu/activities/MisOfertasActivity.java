package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.example.testmenu.R;
import com.example.testmenu.adapters.PostsAdapter;
import com.example.testmenu.adapters.PostsAdapter2;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.utils.ViewedMensajeHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class MisOfertasActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    RecyclerView recyclerView;

    AutentificacioFirebase mAutentificacionFirebase;
    PublicacionFirebase mPublicacionfirebase;
    PostsAdapter2 mPostsAdapter2;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_ofertas);

        mToolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewInicio);

        mAutentificacionFirebase = new AutentificacioFirebase();
        mPublicacionfirebase = new PublicacionFirebase();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ((AppCompatActivity) this).setSupportActionBar(mToolbar);
        ((AppCompatActivity) this).getSupportActionBar().setTitle("Mis ofertas");
    }

    @Override
    public void onStart() {
        super.onStart();
            Query query = mPublicacionfirebase.getPublicacionDeUsuario(mAutentificacionFirebase.getUid());
            FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                    .setQuery(query, Publicacion.class)
                    .build();

            mPostsAdapter2 = new PostsAdapter2(options, this);
            recyclerView.setAdapter(mPostsAdapter2);
            mPostsAdapter2.startListening();
            ViewedMensajeHelper.updateOnline(true,MisOfertasActivity.this);
        }



    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter2.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false,MisOfertasActivity.this);
    }

}