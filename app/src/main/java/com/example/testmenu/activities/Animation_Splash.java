
/**
 * La clase Animation_Splash representa la actividad de pantalla de inicio con animación.
 * Muestra una animación de splash y luego redirige al usuario a la actividad PagPrincipalActivity.
 */
package com.example.testmenu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testmenu.R;

public class Animation_Splash extends AppCompatActivity {
    /**
     * Se llama cuando se crea la actividad. Configura la vista y establece un temporizador
     * para mostrar la animación de splash y luego redirigir al usuario a la actividad PagPrincipalActivity.
     *
     * @param savedInstanceState El paquete de estado guardado.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_splash);

        // Establece un temporizador de 2 segundos para mostrar la animación de splash.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Redirige al usuario a la actividad PagPrincipalActivity.
                startActivity(new Intent(Animation_Splash.this, PagPrincipalAtivity.class));
                finish();
            }
        }, 2000);
    }

}
