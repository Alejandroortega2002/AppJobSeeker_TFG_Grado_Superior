package com.example.testmenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Restablecer_Contrasena extends AppCompatActivity {

    private Button recuperarContrasena;
    private EditText emailRecuperar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer_contrasena);

        recuperarContrasena = findViewById(R.id.RecBtnRecuperar);
        emailRecuperar = findViewById(R.id.RecuperacionEditEmail);

        recuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarEmail();
            }
        });
    }

    public void validarEmail(){
        String email = emailRecuperar.getText().toString();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailRecuperar.setError("Correo Invalido");
            return;
        }
        enviarEmail(email);
    }

    public void enviarEmail(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailRecuperacion = email;

        auth.sendPasswordResetEmail(emailRecuperacion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    // Mostrar un mensaje de inicio de sesión exitoso
                    Toast.makeText(getApplicationContext(), "Se ha Enviado un mensaje a tu correo electrónico.",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Restablecer_Contrasena.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "El correo no es correcto, intentelo de nuevo.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}