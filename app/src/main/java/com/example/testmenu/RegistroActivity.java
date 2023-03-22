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

public class RegistroActivity extends AppCompatActivity {

    private Button btnRegistrar, btnLogin;
    private EditText editNusuario, editEmail, editContrasena, editConfirmContrasena;
    private ProgressDialog barraProgreso;
    //FireBase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        editNusuario = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editContrasena = findViewById(R.id.editPassword);
        editConfirmContrasena = findViewById(R.id.editConfirmPassword);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLogin = findViewById(R.id.btnLogin);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarCredenciales();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        barraProgreso = new ProgressDialog(RegistroActivity.this);
    }

    public void verificarCredenciales() {
        String nusuario = editNusuario.getText().toString();
        String email = editEmail.getText().toString();
        String contrasena = editContrasena.getText().toString();
        String confirmContrasena = editConfirmContrasena.getText().toString();
        if (nusuario.isEmpty() || nusuario.length() < 5) {
            mostrarError(editNusuario, "Nombre de Usuario no valido");
        } else if (email.isEmpty() || !email.contains("@")) {
            mostrarError(editEmail, "Email no valido");
        } else if (contrasena.isEmpty() || contrasena.length() < 7) {
            mostrarError(editContrasena, "Contraseña no valida minimo 7 caracteres");
        } else if (confirmContrasena.isEmpty() || !confirmContrasena.equals(contrasena)) {
            mostrarError(editConfirmContrasena, "Contraseña no valida, no coincide.");
        } else {
            mostrarBarraProgreso();
            registrarUsuario(email, contrasena);

        }

    }

    public void registrarUsuario(String email, String contrasena) {
        mAuth.createUserWithEmailAndPassword(email, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //ocultar progressBar
                    barraProgreso.dismiss();
                    //redireccionar - intent a login
                    Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //Exitoso -> Mostrar toast
                    Toast.makeText(getApplicationContext(), "Se ha creado el usuario",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void mostrarBarraProgreso() {
        //Mostrar ProgressBar
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