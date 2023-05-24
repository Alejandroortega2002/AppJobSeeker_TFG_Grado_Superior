package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.testmenu.R;

public class SectoresActivity extends AppCompatActivity {

    ImageButton btnSalir;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sectores);

        btnSalir = findViewById(R.id.volverAtrasSectores);

        btnSalir.setOnClickListener(view -> {
            finish();
        });
    }
}