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

public class PagPrincipalAtivity extends AppCompatActivity {

    private Button login1,registro;

    @SuppressLint({"MissingInflatedId", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag_principal_ativity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        login1 = findViewById((R.id.PbtnLogin));
        registro = findViewById(R.id.PbtnRegistro);



        login1.setOnClickListener(view -> {
            Intent intent = new Intent(PagPrincipalAtivity.this, LoginActivity.class);
            startActivity(intent);
            PagPrincipalAtivity.this.finish();
        });

        registro.setOnClickListener(view -> {
            Intent intent = new Intent(PagPrincipalAtivity.this, RegistroActivity.class);
            startActivity(intent);
            PagPrincipalAtivity.this.finish();
        });


    }



}