package com.example.testmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class PagPrincipalAtivity extends AppCompatActivity {

    private Button login1,registro;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag_principal_ativity);
        login1 = findViewById((R.id.PbtnLogin));
        registro = findViewById(R.id.PbtnRegistro);


        login1.setOnClickListener(view -> {
            Intent intent = new Intent(PagPrincipalAtivity.this, LoginActivity.class);
            startActivity(intent);
        });

        registro.setOnClickListener(view -> {
            Intent intent = new Intent(PagPrincipalAtivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }
}