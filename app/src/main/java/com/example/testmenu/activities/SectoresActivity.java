package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.testmenu.R;
import com.example.testmenu.adapters.PostsAdapter;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SectoresActivity extends AppCompatActivity {
    ImageButton btnSalir;
    TextView textViewSectores;
    static TextView txtNoHayPublicacion;
    String sector;

    static RecyclerView reciclerPorSectores;
    AutentificacioFirebase autentificacioFirebase;
    PublicacionFirebase publicacionFirebase;
    PostsAdapter postsAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sectores);
        reciclerPorSectores = findViewById(R.id.recyclerViewSectores);
        textViewSectores = findViewById(R.id.textViewSectores);
        btnSalir = findViewById(R.id.volverAtrasSectores);
        txtNoHayPublicacion = findViewById(R.id.txtNoHayPublicacion);

        txtNoHayPublicacion.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SectoresActivity.this);
        reciclerPorSectores.setLayoutManager(linearLayoutManager);

        sector = getIntent().getStringExtra("sector");

        publicacionFirebase = new PublicacionFirebase();
        autentificacioFirebase = new AutentificacioFirebase();

        textViewSectores.setText("Sector de " + sector);

        btnSalir.setOnClickListener(view -> {
            finish();
        });

        if (reciclerPorSectores != null) {
            reciclerPorSectores.setAdapter(postsAdapter);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = publicacionFirebase.getPostBySectorAndTimestamp(sector);
        FirestoreRecyclerOptions<Publicacion> options =
                new FirestoreRecyclerOptions.Builder<Publicacion>()
                        .setQuery(query, Publicacion.class)
                        .build();
        postsAdapter = new PostsAdapter(options, SectoresActivity.this);

        if (reciclerPorSectores != null) {
            reciclerPorSectores.setAdapter(postsAdapter);
        }

        if (postsAdapter != null) {
            postsAdapter.startListening();
        }

        vacio();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (postsAdapter != null) {
            postsAdapter.stopListening();
        }
    }

    public static void vacio() {
        if (reciclerPorSectores != null && reciclerPorSectores.getAdapter() != null) {
            // De esta manera sabes si tu RecyclerView está vacío
            if (reciclerPorSectores.getAdapter().getItemCount() == 0) {
                txtNoHayPublicacion.setVisibility(View.VISIBLE); // HOLA QUE TAL Mostrar el TextView si el RecyclerView está vacío
            } else {
                txtNoHayPublicacion.setVisibility(View.GONE); // Ocultar el TextView si el RecyclerView no está vacío
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}