package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Restablecer_Contrasena extends AppCompatActivity {

    private Button recuperarContrasena;
    private EditText emailRecuperar;

    AutentificacioFirebase authFirebase;

    /**
     * Método que se llama al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer_contrasena);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        recuperarContrasena = findViewById(R.id.RecBtnRecuperar);
        emailRecuperar = findViewById(R.id.RecuperacionEditEmail);

        recuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarEmail();
            }
        });

        authFirebase = new AutentificacioFirebase();
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
     * Método para enviar un correo electrónico de recuperación de contraseña.
     *
     * @param email La dirección de correo electrónico a la cual se enviará el correo de recuperación.
     */
    public void enviarEmail(String email) {
        authFirebase.recuperarContrasena(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Mostrar un mensaje de éxito
                    Toast.makeText(getApplicationContext(), "Se ha enviado un mensaje a tu correo electrónico.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Restablecer_Contrasena.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "El correo no es correcto, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
