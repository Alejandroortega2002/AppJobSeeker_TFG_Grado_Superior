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

    private AutentificacioFirebase authFirebase;

    /**
     * Método que se llama al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
     * Comprobar si el correo asignado es valido y se envía por <b>enviarEmail()</b>
     *
     * @return void
     */
    public void validarEmail() {
        // Obtener el correo electrónico ingresado en el campo de texto
        String email = emailRecuperar.getText().toString();

        // Validar si el campo de correo electrónico está vacío o si no coincide con el patrón de dirección de correo electrónico
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Si el correo electrónico es inválido, mostrar un mensaje de error en el campo de texto
            emailRecuperar.setError("Correo inválido");
            return;
        }

        // Si el correo electrónico es válido, llamar al método enviarEmail() para enviar el correo de recuperación de contraseña
        enviarEmail(email);
    }


    /**Se manda un mail al correo asignado y se avisa al usuario del envio
     *
     * @param email correo al que se le enviará el mail
     * @return void
     */
    public void enviarEmail(String email) {
        // Llamar al método de Firebase para enviar el correo de recuperación de contraseña
        authFirebase.recuperarContrasena(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Mostrar un mensaje de éxito indicando que se ha enviado el correo de recuperación
                    Toast.makeText(getApplicationContext(), "Se ha enviado un mensaje a tu correo electrónico.", Toast.LENGTH_SHORT).show();

                    // Crear un intent para redirigir al usuario a la pantalla de inicio de sesión
                    Intent intent = new Intent(Restablecer_Contrasena.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Mostrar un mensaje de error indicando que el correo no es correcto
                    Toast.makeText(getApplicationContext(), "El correo no es correcto, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
