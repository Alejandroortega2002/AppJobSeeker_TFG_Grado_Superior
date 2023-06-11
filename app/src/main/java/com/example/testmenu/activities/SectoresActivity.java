
package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
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
    private ImageButton btnSalir;
    private TextView textViewSectores;
    static TextView txtNoHayPublicacion;
    private String sector;

    static RecyclerView reciclerPorSectores;
    private AutentificacioFirebase autentificacioFirebase;
    private PublicacionFirebase publicacionFirebase;
    private PostsAdapter postsAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sectores);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        // Obtener la consulta de Firebase Firestore para obtener las publicaciones según el sector y el timestamp
        Query query = publicacionFirebase.getPostBySectorAndTimestamp(sector);

        // Configurar las opciones del adaptador de FirestoreRecycler
        FirestoreRecyclerOptions<Publicacion> options =
                new FirestoreRecyclerOptions.Builder<Publicacion>()
                        .setQuery(query, Publicacion.class)
                        .build();

        // Crear una instancia del adaptador de Posts y pasar las opciones y el contexto (SectoresActivity)
        postsAdapter = new PostsAdapter(options, SectoresActivity.this);

        // Establecer el adaptador en el RecyclerView reciclerPorSectores
        if (reciclerPorSectores != null) {
            reciclerPorSectores.setAdapter(postsAdapter);
        }

        // Iniciar la escucha del adaptador
        if (postsAdapter != null) {
            postsAdapter.startListening();
        }

        // Verificar si la lista de publicaciones está vacía
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
        // Verificar si el RecyclerView y su adaptador no son nulos
        if (reciclerPorSectores != null && reciclerPorSectores.getAdapter() != null) {
            // Verificar si el RecyclerView está vacío
            if (reciclerPorSectores.getAdapter().getItemCount() == 0) {
                // Mostrar el TextView txtNoHayPublicacion si el RecyclerView está vacío
                txtNoHayPublicacion.setVisibility(View.VISIBLE);
            } else {
                // Ocultar el TextView txtNoHayPublicacion si el RecyclerView no está vacío
                txtNoHayPublicacion.setVisibility(View.GONE);
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Verificar si el elemento seleccionado es el botón de inicio predeterminado del sistema
        if (item.getItemId() == android.R.id.home) {
            // Finalizar la actividad actual y regresar a la actividad anterior
            finish();
        }
        // Indicar que el evento de selección ha sido manejado correctamente
        return true;
    }


}