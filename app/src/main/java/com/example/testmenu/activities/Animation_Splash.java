package com.example.testmenu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testmenu.R;

public class Animation_Splash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_splash);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(Animation_Splash.this, PagPrincipalAtivity.class));
            finish();
        }, 2000);

    }

}

