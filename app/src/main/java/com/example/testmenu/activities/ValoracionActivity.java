package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.adapters.ValoracionesAdapter;
import com.example.testmenu.entidades.FCMBody;
import com.example.testmenu.entidades.FCMResponse;
import com.example.testmenu.entidades.Valoraciones;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.firebase.ValoracionFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ValoracionActivity extends AppCompatActivity {

    private String userId;

    private TextView txtValoracion;
    private TextView txtNombreUsuario;
    static TextView txtNoHayValoraciones;

    private RatingBar estrellas;

    private CircleImageView fotoPerfilValoracion;

    static RecyclerView reciclerValoraciones;

    private AutentificacioFirebase autentificacioFirebase;
    private UsuariosBBDDFirebase usuariosBBDDFirebase;

    private ArrayList<Valoraciones> listaValoraciones;
    private Dialog customDialog;

    private ValoracionesAdapter valoracionesAdapter;

    private ImageButton btnSalir, btnCrearValoracion;

    private ValoracionFirebase valoracionFirebase;
    private float suma;

    private float ratingSum = 0;


    /**
     * Método que se llama al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
    @SuppressLint({"MissingInflatedId"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoracion);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();

        valoracionFirebase = new ValoracionFirebase();

        userId = getIntent().getStringExtra("idUsuario");

        txtNombreUsuario = findViewById(R.id.nombreUsuario);
        fotoPerfilValoracion = findViewById(R.id.fotoUsuario);
        estrellas = findViewById(R.id.estrellas);
        txtValoracion = findViewById(R.id.txtValoracion);
        btnCrearValoracion = findViewById(R.id.btnValorar);
        reciclerValoraciones = findViewById(R.id.recyclerViewValoraciones);
        btnSalir = findViewById(R.id.volverAtrasValoraciones);
        txtNoHayValoraciones = findViewById(R.id.txtNoHayValoraciones);

        txtNoHayValoraciones.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ValoracionActivity.this);
        reciclerValoraciones.setLayoutManager(linearLayoutManager);

        btnSalir.setOnClickListener(view -> {
            finish();
        });

        btnCrearValoracion.setOnClickListener(view -> {
            CrearValoracionDialog dialog = new CrearValoracionDialog(userId, this);
            dialog.show(getSupportFragmentManager(), "CrearValoracionDialog");
        });

        cargarDetallesUsuario();
        cargarValoraciones();
    }

    /**
     * Carga y actualiza las valoraciones del usuario desde Firebase y actualiza la interfaz de usuario con la media de las valoraciones y el número total de valoraciones.
     * <p>
     * Obtiene documentos de las valoraciones del usuario mediante <b>getCommentsByUser<b>.
     * Si la consulta tiene éxito, se itera los documentos y se convierten en objetos de tipo <b>Valoraciones<b>, esto permite hacer una media del total de las valoraciones.
     * Se actualiza la media en la base de datos con el método <b>updateMedia()<b>
     *
     * @return void
     */
    public void cargarValoraciones() {
        // Obtener las valoraciones del usuario en tiempo real utilizando un SnapshotListener
        valoracionFirebase.getCommentsByUser(userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Manejo de errores en la obtención de las valoraciones.
                            return;
                        }

                        // Crear una nueva lista para almacenar las valoraciones
                        listaValoraciones = new ArrayList<>();

                        // Reiniciar la variable suma
                        suma = 0;

                        // Recorrer los documentos obtenidos de la consulta
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Convertir cada documento a un objeto de la clase Valoraciones
                            Valoraciones valoracion = document.toObject(Valoraciones.class);

                            // Obtener el valor de la nota y convertirlo a tipo float
                            float rating = Float.parseFloat(valoracion.getNota());

                            // Sumar el rating a la variable suma
                            suma += rating;
                        }

                        // Obtener el número de valoraciones realizadas
                        long numeroValoraciones = queryDocumentSnapshots.size();

                        // Calcular la media dividiendo la suma por el número de valoraciones
                        float media = suma / numeroValoraciones;

                        // Establecer el texto de la valoración y el número de valoraciones en un TextView
                        txtValoracion.setText(String.format("%.2f", media) + " [" + numeroValoraciones + "]");

                        // Establecer la puntuación en un RatingBar
                        estrellas.setRating(media);

                        // Actualizar la media del usuario en la base de datos
                        usuariosBBDDFirebase.updateMedia(userId, media).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Actualización de la media del usuario exitosa.
                            }
                        });
                    }
                });

    }
    /**
     * Método que se llama al iniciar la actividad.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Crear una consulta para obtener las valoraciones del usuario
        Query query = valoracionFirebase.getCommentsByUser(userId);

        // Configurar las opciones del adaptador para el RecyclerView
        FirestoreRecyclerOptions<Valoraciones> options =
                new FirestoreRecyclerOptions.Builder<Valoraciones>()
                        .setQuery(query, Valoraciones.class)
                        .build();

        // Crear un adaptador de valoraciones utilizando las opciones configuradas
        valoracionesAdapter = new ValoracionesAdapter(options, ValoracionActivity.this);

        // Establecer el adaptador en el RecyclerView
        reciclerValoraciones.setAdapter(valoracionesAdapter);

        // Iniciar la escucha del adaptador para mantenerlo actualizado con los cambios en los datos
        valoracionesAdapter.startListening();

        // Llamar al método vacio() aquí, después de cargar los datos en el RecyclerView
        vacio();
    }


    /**
     * Método que se llama al detener la actividad.
     */
    @Override
    public void onStop() {
        super.onStop();
        valoracionesAdapter.stopListening();
    }

    /**
     * Método que se llama al reanudar la actividad.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Muestra los datos del perfil del usuario que se desea ver sus valoraciones si su documento existe.
     *
     * @return void
     */
    public void cargarDetallesUsuario() {
        if (userId != null) {

            // Obtener los detalles del usuario utilizando el userId
            usuariosBBDDFirebase.getUsuarios(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {

                        // Verificar si el documento contiene el campo "usuario"
                        if (documentSnapshot.contains("usuario")) {
                            // Obtener el nombre de usuario del documento
                            String nUsuarioActivity = documentSnapshot.getString("usuario");
                            // Establecer el nombre de usuario en el TextView
                            txtNombreUsuario.setText("@" + nUsuarioActivity.toUpperCase());
                        }

                        // Verificar si el documento contiene el campo "fotoPerfil"
                        if (documentSnapshot.contains("fotoPerfil")) {
                            // Obtener la URL de la foto de perfil del documento
                            String fotoPerfilActivity = documentSnapshot.getString("fotoPerfil");
                            if (fotoPerfilActivity != null) {
                                // Cargar la foto de perfil utilizando Picasso y establecerla en la ImageView
                                Picasso.get().load(fotoPerfilActivity).into(fotoPerfilValoracion);
                            }
                        }
                    }
                }
            });
        }
    }

    public static void vacio() {
        // Verificar si el RecyclerView y su adaptador no son nulos
        if (reciclerValoraciones != null && reciclerValoraciones.getAdapter() != null) {
            // Verificar si el RecyclerView está vacío
            if (reciclerValoraciones.getAdapter().getItemCount() == 0) {
                // Mostrar el TextView txtNoHayPublicacion si el RecyclerView está vacío
                txtNoHayValoraciones.setVisibility(View.VISIBLE);
            } else {
                // Ocultar el TextView txtNoHayPublicacion si el RecyclerView no está vacío
                txtNoHayValoraciones.setVisibility(View.GONE);
            }
        }
    }
}
