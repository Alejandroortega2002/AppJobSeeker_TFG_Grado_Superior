package com.example.testmenu.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import com.example.testmenu.R;

/**
 * La clase PagPrincipalAtivity es la actividad principal de la aplicación.
 * Muestra las opciones de inicio de sesión y registro para los usuarios.
 */
public class PagPrincipalAtivity extends AppCompatActivity {

    private Button login1, registro;

    /**
     * Método llamado al crear la actividad.
     *
     * @param savedInstanceState Los datos guardados del estado anterior de la actividad.
     */
    @SuppressLint({"MissingInflatedId", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag_principal_ativity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        login1 = findViewById((R.id.PbtnLogin));
        registro = findViewById(R.id.PbtnRegistro);

        // Acción al hacer clic en el botón de inicio de sesión
        login1.setOnClickListener(view -> {
            Intent intent = new Intent(PagPrincipalAtivity.this, LoginActivity.class);
            startActivity(intent);
            PagPrincipalAtivity.this.finish();
        });

        // Acción al hacer clic en el botón de registro
        registro.setOnClickListener(view -> {
            Intent intent = new Intent(PagPrincipalAtivity.this, RegistroActivity.class);
            startActivity(intent);
            PagPrincipalAtivity.this.finish();
        });
    }
}
