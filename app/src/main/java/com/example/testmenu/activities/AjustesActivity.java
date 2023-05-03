package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.fragmentMenu.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class AjustesActivity extends AppCompatActivity {

    private ImageButton btnSalir;
    private TextView btnCerrarSesion, btnBorrarCuenta, btnEditarPerfil, btnRestablecerContrasena;
    AutentificacioFirebase mAutentificacionFirebase;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnSalir = findViewById(R.id.volver_perfil);

        btnCerrarSesion = findViewById(R.id.txtCloseSesion);
        btnEditarPerfil = findViewById(R.id.txtPerfil);
        btnRestablecerContrasena = findViewById(R.id.txtResetPWD);
        btnBorrarCuenta = findViewById(R.id.txtDeletePerfil);

        mAutentificacionFirebase = new AutentificacioFirebase();

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjustesActivity.this, EditarPerfilActivity.class);
                startActivity(intent);
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnRestablecerContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjustesActivity.this, Recuperar_Contrasena_Usuario_Logueado.class);
                startActivity(intent);
            }
        });

        btnBorrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BorrarUsuario();


            }
        });


    }

    private void logout() {
        mAutentificacionFirebase.logout();
        Intent intent = new Intent(AjustesActivity.this, PagPrincipalAtivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Borra el usuario actualmente autenticado de la base de datos de usuarios y elimina su cuenta de autenticación.
     * Si se realiza con éxito, se cierra la sesión y se redirige al usuario a la pantalla de inicio de sesión.
     *
     * @throws NullPointerException si el usuario actual no está autenticado
     */
    private void BorrarUsuario() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UsuariosBBDDFirebase usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        usuariosBBDDFirebase.deleteUsuarios(userID).addOnSuccessListener(aVoid -> {
            AutentificacioFirebase autentificacioFirebase = new AutentificacioFirebase();
            autentificacioFirebase.deleteAccount().addOnSuccessListener(result -> {
                autentificacioFirebase.logout();
                Intent intent = new Intent(AjustesActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                // Manejar el error aquí
                String errorMessage = "No se pudo eliminar la cuenta de autenticación. Inténtelo de nuevo más tarde.";
                Toast.makeText(AjustesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Manejar el error aquí
            String errorMessage = "No se pudo eliminar la cuenta. Inténtelo de nuevo más tarde.";
            Toast.makeText(AjustesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        });
    }


}