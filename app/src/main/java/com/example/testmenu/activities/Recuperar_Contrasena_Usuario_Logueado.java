package com.example.testmenu.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.utils.ViewedMensajeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class Recuperar_Contrasena_Usuario_Logueado extends AppCompatActivity {

    private Button recuperarContrasena;
    private ImageButton btnVolverEditar;
    private EditText emailRecuperar;
    AutentificacioFirebase autentificacioFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;
    ListenerRegistration mListener;

    /**
     * Método que se llama al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena_usuario_logueado);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        recuperarContrasena = findViewById(R.id.RecBtnRecuperar);
        emailRecuperar = findViewById(R.id.RecuperacionUSLEditEmail);
        btnVolverEditar = findViewById(R.id.volver_Ajustes);

        recuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarEmail();
            }
        });

        btnVolverEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        rellenarInformacionUsuario();
    }

    /**
     * Método para validar la dirección de correo electrónico ingresada.
     */
    public void validarEmail() {
        String email = emailRecuperar.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRecuperar.setError("Correo inválido");
            return;
        }
        enviarEmail(email);
    }

    /**
     * Método para obtener y mostrar la información del usuario actualmente logueado.
     */
    public void rellenarInformacionUsuario() {
        DocumentReference documentReference = usuariosBBDDFirebase.refereciaColeccion(autentificacioFirebase.getUid());
        mListener = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    if (error != null) {
                        // Manejar el error de Firebase Firestore
                        Log.w(TAG, "Error al obtener el documento.", error);
                        return;
                    }
                    if (value != null && value.exists()) {
                        // Obtener los valores del objeto DocumentSnapshot
                        String correo = value.getString("email");

                        if (correo != null) {
                            emailRecuperar.setText(correo);
                        } else {
                            emailRecuperar.setText("Sin correo");
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
     * Método para enviar un correo electrónico para recuperar la contraseña.
     *
     * @param email La dirección de correo electrónico a la cual se enviará el correo de recuperación.
     */
    public void enviarEmail(String email) {
        autentificacioFirebase.recuperarContrasena(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Mostrar un mensaje de inicio de sesión exitoso
                    Toast.makeText(getApplicationContext(), "Se ha enviado un mensaje a tu correo electrónico.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Recuperar_Contrasena_Usuario_Logueado.this, AjustesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "El correo no es correcto, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Método que se llama cuando la actividad se vuelve visible para el usuario.
     */
    @Override
    protected void onStart() {
        super.onStart();
        ViewedMensajeHelper.updateOnline(true, Recuperar_Contrasena_Usuario_Logueado.this);
    }

    /**
     * Método que se llama cuando la actividad pierde el foco y no es visible para el usuario.
     */
    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false, Recuperar_Contrasena_Usuario_Logueado.this);
    }

    /**
     * Método que se llama cuando la actividad está a punto de ser destruida.
     */
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.remove();
        }
    }
}
