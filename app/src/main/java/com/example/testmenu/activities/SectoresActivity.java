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

    /**
     * Método que se llama al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
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


    }

    /**
     * Método que se llama cuando la actividad está a punto de hacerse visible para el usuario.
     */
    @Override
    public void onStart() {
        super.onStart();
        Query query = publicacionFirebase.getPostBySectorAndTimestamp(sector);
        FirestoreRecyclerOptions<Publicacion> options =
                new FirestoreRecyclerOptions.Builder<Publicacion>()
                        .setQuery(query, Publicacion.class)
                        .build();
        postsAdapter = new PostsAdapter(options, SectoresActivity.this);
        reciclerPorSectores.setAdapter(postsAdapter);
        postsAdapter.startListening();

        vacio();

    }

    /**
     * Método que se llama cuando la actividad ya no es visible para el usuario.
     */
    @Override
    public void onStop() {
        super.onStop();
        postsAdapter.stopListening();
    }

    /**
     * Método para verificar si el RecyclerView está vacío y mostrar u ocultar un TextView en consecuencia.
     */
    public static void vacio() {
        if (reciclerPorSectores.getAdapter() != null) {
            if (reciclerPorSectores.getAdapter().getItemCount() == 0) {
                txtNoHayPublicacion.setVisibility(View.VISIBLE);
            } else {
                txtNoHayPublicacion.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Método para manejar las acciones del elemento seleccionado en la barra de acciones.
     *
     * @param item El elemento de la barra de acciones seleccionado.
     * @return `true` si el evento se ha consumido, `false` de lo contrario.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
