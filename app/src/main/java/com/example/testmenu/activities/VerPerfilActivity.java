package com.example.testmenu.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testmenu.R;
import com.example.testmenu.databinding.FragmentPerfilBinding;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.utils.ViewedMensajeHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerPerfilActivity extends AppCompatActivity {

    private String VPidUser;

    private TextView telefono, nombreU, email, descripcion, numeroDeOfertas, txtOfertas;

    private ImageView fotoBanner;
    private CircleImageView fotoPerfil;

    private ImageButton btnAjustes, btnSalir;

    private LinearLayout btnverOfertas, btnverFavortitos, btnValoracion;

    private AutentificacioFirebase autentificacioFirebase;
    private UsuariosBBDDFirebase usuariosBBDDFirebase;
    private PublicacionFirebase publicacionFirebase;

    private ListenerRegistration mListener;

    /**
     * Método que se llama al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_perfil);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        publicacionFirebase = new PublicacionFirebase();

        btnAjustes = findViewById(R.id.VPbtnAjustes);
        btnSalir = findViewById(R.id.VPvolver_perfil);
        btnverFavortitos = findViewById(R.id.VPbtnFavoritos);
        btnverOfertas = findViewById(R.id.VPbtnMisOfertas);
        btnValoracion = findViewById(R.id.VPbtnValoraciones);

        telefono = findViewById(R.id.VPnTelefono);
        nombreU = findViewById(R.id.VPnPerfil);
        email = findViewById(R.id.VPpEmail);
        descripcion = findViewById(R.id.VPpDescripcion);
        numeroDeOfertas = findViewById(R.id.VPnPublicaciones);
        fotoBanner = findViewById(R.id.VPbanner);
        fotoPerfil = findViewById(R.id.VPfotoPerfil);
        txtOfertas = findViewById(R.id.txtOfertas);
        VPidUser = getIntent().getStringExtra("idUser");
        checkUser();
        rellenarInformacionUsuario();
        getNumeroPublicaciones();

        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAjustes();
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnverOfertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VerPerfilActivity.this, MisOfertasActivity2.class);
                i.putExtra("idUserCarta", VPidUser);
                startActivity(i);
            }
        });

        btnverFavortitos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VerPerfilActivity.this, MisFavoritosActivity.class);
                startActivity(i);
            }
        });

        btnValoracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerPerfilActivity.this, ValoracionActivity.class);
                intent.putExtra("idUsuario", VPidUser);
                startActivity(intent);
            }
        });
    }

    /**
     * Cambio a la activity Ajustes
     * @return void
     */
    public void irAjustes() {
        Intent i = new Intent(this, AjustesActivity.class);
        startActivity(i);
    }

    /**
     * Rellena los campos de la activity con los datos del usuario desde la base de datos
     * <p>
     * Se consulta un documento, en caso de que exista, se rellena los datos del perfil que se desea observar.En caso de error, se registra en el Log con un mensaje
     *
     * @return void
     */
    public void rellenarInformacionUsuario() {
        DocumentReference documentReference = usuariosBBDDFirebase.refereciaColeccion(VPidUser);

        // Agregar un SnapshotListener al DocumentReference para obtener los cambios en tiempo real
        mListener = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    if (error != null) {
                        // Manejar el error de Firebase Firestore
                        Log.w(TAG, "Error al obtener el documento.", error);
                        return;
                    }

                    // Verificar si el DocumentSnapshot existe y contiene valores
                    if (value != null && value.exists()) {
                        // Obtener los valores del objeto DocumentSnapshot
                        String nombre = value.getString("usuario");
                        String correo = value.getString("email");
                        String ntelefono = value.getString("telefono");
                        String descrip = value.getString("descripcion");

                        // Verificar si los valores obtenidos son nulos antes de establecer el texto en los TextViews

                        // Verificar si el campo "fotoPerfil" existe en el DocumentSnapshot
                        if (value.contains("fotoPerfil")) {
                            String perfil = value.getString("fotoPerfil");
                            if (perfil != null && !perfil.isEmpty()) {
                                // Cargar la foto de perfil utilizando Picasso y establecerla en la ImageView "fotoPerfil"
                                Picasso.get().load(perfil).into(fotoPerfil);
                            }
                        }

                        // Verificar si el campo "banner" existe en el DocumentSnapshot
                        if (value.contains("banner")) {
                            String banner = value.getString("banner");
                            if (banner != null && !banner.isEmpty()) {
                                // Cargar el banner utilizando Picasso y establecerlo en la ImageView "fotoBanner"
                                Picasso.get().load(banner).into(fotoBanner);
                            }
                        }

                        // Verificar si el nombre no es nulo y establecerlo en el TextView "nombreU"
                        if (nombre != null) {
                            nombreU.setText(nombre);
                        } else {
                            nombreU.setText("Sin nombre");
                        }

                        // Verificar si el correo no es nulo y establecerlo en el TextView "email"
                        if (correo != null) {
                            email.setText(correo);
                        } else {
                            email.setText("Sin correo");
                        }

                        // Verificar si el número de teléfono no es nulo y establecerlo en el TextView "telefono"
                        if (telefono != null) {
                            telefono.setText(ntelefono);
                        } else {
                            telefono.setText("Sin teléfono");
                        }

                        // Verificar si la descripción no es nula y establecerla en el TextView "descripcion"
                        if (descripcion != null) {
                            descripcion.setText(descrip);
                        } else {
                            descripcion.setText("Sin descripción");
                        }
                    } else {
                        // Manejar el caso en que el objeto DocumentSnapshot es nulo o no existe
                        Log.d(TAG, "El objeto DocumentSnapshot no existe");
                    }
                }
            }
        });
    }

    /**
     * Muestra la cifra de ofertas que el usuario posee
     *
     * @return void
     */
    public void getNumeroPublicaciones() {
        publicacionFirebase.getPublicacionDeUsuario(VPidUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Obtener el tamaño de la lista de QueryDocumentSnapshots, que representa el número de publicaciones
                int numeroPublicaciones = queryDocumentSnapshots.size();

                // Convertir el número de publicaciones a String
                String numeroOfertas = String.valueOf(numeroPublicaciones);

                // Establecer el número de publicaciones en el TextView "numeroDeOfertas"
                numeroDeOfertas.setText(numeroOfertas);
            }
        });
    }


    /**
     * Comprueba que el id del usuario coincide con el que se solicita ver el perfil.
     * <p>
     *  Si coinciden, se muetran funciones unicas.
     *
     * @return void
     */
    public void checkUser() {
        if (!VPidUser.equals(autentificacioFirebase.getUid())) {
            // Si el VPidUser no es igual al ID del usuario autenticado actualmente

            // Desactivar el botón "btnAjustes" haciendo que no sea clickable y estableciéndolo como invisible
            btnAjustes.setClickable(false);
            btnAjustes.setVisibility(View.INVISIBLE);

            // Desactivar el botón "btnverFavortitos" haciendo que no sea clickable y estableciéndolo como invisible
            btnverFavortitos.setClickable(false);
            btnverFavortitos.setVisibility(View.GONE);

            // Cambiar el texto del TextView "txtOfertas" a "Ver Ofertas"
            txtOfertas.setText("Ver Ofertas");

            // Mover el botón "btnValoracion" a la posición X -40 (desplazarlo hacia la izquierda)
            btnValoracion.setX(-40);

            // Mover el botón "btnverOfertas" a la posición X -20 (desplazarlo hacia la izquierda)
            btnverOfertas.setX(-20);
        }
    }

    /**
     * Método que se llama al iniciar la actividad y actualiza el estado del usuario a "en línea".
     */
    @Override
    public void onStart() {
        super.onStart();
        ViewedMensajeHelper.updateOnline(true, VerPerfilActivity.this);
    }

    /**
     * Método que se llama al pausar la actividad y actualiza el estado del usuario a "desconectado".
     */
    @Override
    public void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false, VerPerfilActivity.this);
    }

    /**
     * Método que se llama al destruir la actividad y elimina el listener de la base de datos si está activo.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.remove();
        }
    }
}
