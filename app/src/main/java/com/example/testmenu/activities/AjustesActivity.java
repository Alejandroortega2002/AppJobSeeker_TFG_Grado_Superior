package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.testmenu.R;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.fragmentMenu.ProfileFragment;

public class AjustesActivity extends AppCompatActivity {

    private ImageButton btnSalir;
    private TextView btnCerrarSesion, btnBorrarCuenta, btnEditarPerfil, btnRestablecerContrasena;

    private Switch modoTema;

    private boolean isDarkTheme = false;

    AutentificacioFirebase mAutentificacionFirebase;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        modoTema = findViewById(R.id.switch2);

        btnSalir = findViewById(R.id.volver_perfil);

        btnCerrarSesion = findViewById(R.id.txtCloseSesion);
        btnEditarPerfil = findViewById(R.id.txtPerfil);
        btnRestablecerContrasena = findViewById(R.id.txtResetPWD);
        btnBorrarCuenta = findViewById(R.id.txtDeletePerfil);

        mAutentificacionFirebase = new AutentificacioFirebase();

        if (isDarkTheme) {
            setTheme(R.style.NightTheme);
        } else {
            setTheme(R.style.Theme_TestMenu);
        }


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
                Intent intent = new Intent(AjustesActivity.this, Restablecer_Contrasena.class);
                startActivity(intent);
            }
        });

        btnBorrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        modoTema.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setTheme(R.style.NightTheme);
                    isDarkTheme = true;
                } else {
                    setTheme(R.style.Theme_TestMenu);
                    isDarkTheme = false;
                }
                recreate();
            }
        });



    }

    private void logout() {
        mAutentificacionFirebase.logout();
        Intent intent = new Intent(AjustesActivity.this, PagPrincipalAtivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}