package com.example.testmenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private EditText editEmail, editContrasena;
    private Button btnLogin, btnRecuperar, btnRegistrar;
    private ProgressDialog barraProgreso;
    //FireBase
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.LeditMail);
        editContrasena = findViewById(R.id.LeditContrasena);

        btnLogin = findViewById(R.id.Lbtnlogin);
        btnRecuperar = findViewById(R.id.LbtnRecuperar);
        btnRegistrar = findViewById(R.id.LbtnRegistrar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarCredenciales();
            }
        });

        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        barraProgreso = new ProgressDialog(LoginActivity.this);

    }

    public void verificarCredenciales() {
        String email = editEmail.getText().toString();
        String contrasena = editContrasena.getText().toString();
        if (email.isEmpty() || !email.contains("@")) {
            mostrarError(editEmail, "Email no valido");
        } else if (contrasena.isEmpty() || contrasena.length() < 7) {
            mostrarError(editContrasena, "Password invalida");
        } else {
            mostrarBarraProgreso();
            iniciarSesion(email, contrasena);

        }
    }

    public void iniciarSesion(String email, String contrasena) {
        mAuth.signInWithEmailAndPassword(email, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //ocultar progressBar
                    barraProgreso.dismiss();
                    //redireccionar - intent a login
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //Exitoso -> Mostrar toast
                    Toast.makeText(getApplicationContext(), "Se ha Iniciado Sesion",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //ocultar progressBar
                    barraProgreso.dismiss();
                    Toast.makeText(getApplicationContext(), "Incorrecto.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void mostrarBarraProgreso() {
        //Mostrar ProgressDialog
        barraProgreso.setTitle("Proceso de Registro");
        barraProgreso.setMessage("Registrando usuario, espere un momento");
        barraProgreso.setCanceledOnTouchOutside(false);
        barraProgreso.show();
    }

    private void mostrarError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

}