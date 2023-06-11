package com.example.testmenu.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.testmenu.R;
import com.example.testmenu.databinding.ActivityMainBinding;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.TokenFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.utils.ViewedMensajeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
 * La clase MainActivity es la actividad principal de la aplicación.
 * Se encarga de mostrar la interfaz de usuario y gestionar la navegación entre los diferentes fragmentos.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private TokenFirebase mTokenFirebase;
    private AutentificacioFirebase mAutentificationFirebase;

    private UsuariosBBDDFirebase mUsuarioFirebase;

    // Declare the launcher at the top of your Activity/Fragment:
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    /**
     * Método llamado al crear la actividad.
     *
     * @param savedInstanceState Los datos guardados del estado anterior de la actividad.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bloquear la orientación de la pantalla en modo retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Inflar el diseño de la actividad utilizando el enlace de datos generados por ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Habilitar la recopilación de análisis de Firebase
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);

        // Inicializar instancias de Firebase y solicitar el token de Firebase
        mTokenFirebase = new TokenFirebase();
        mAutentificationFirebase = new AutentificacioFirebase();
        mUsuarioFirebase = new UsuariosBBDDFirebase();
        createToken();

        // Solicitar permiso de notificación
        askNotificationPermission();

        // Solicitar permiso de cámara
        askCameraPermission();

        // Crear un canal de notificación para versiones de Android posteriores a Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Configurar la barra de navegación inferior utilizando Navigation Component
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_filtro, R.id.navigation_perfil, R.id.navigation_chat, R.id.navigation_inicio)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**
     * Método llamado al iniciar la actividad.
     */
    @Override
    public void onStart() {
        super.onStart();
        ViewedMensajeHelper.updateOnline(true, MainActivity.this);
    }

    /**
     * Método llamado al pausar la actividad.
     */
    @Override
    public void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false, MainActivity.this);
    }

    /**
     * Crea un token del usuario que tiene la sesión iniciada actual
     *
     * @return void
     */
    public void createToken() {
        mTokenFirebase.create(mAutentificationFirebase.getUid());
    }

    /**
     * Pide por un UI activar las notificaciones en el usuario
     *
     * @return void
     */
    public void askNotificationPermission() {
        // Esto es solo necesario para API nivel >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // El SDK de FCM (y tu aplicación) puede publicar notificaciones.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: mostrar una interfaz educativa que explique al usuario las características que se habilitarán
                //       al otorgar el permiso POST_NOTIFICATIONS. Esta interfaz debe proporcionar al usuario los botones
                //       "Aceptar" y "No, gracias". Si el usuario selecciona "Aceptar", solicitar directamente el permiso.
                //       Si el usuario selecciona "No, gracias", permitir que el usuario continúe sin notificaciones.
            } else {
                // Solicitar directamente el permiso
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    public void askCameraPermission() {
        // Verificar y solicitar los permisos necesarios
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Si alguno de los permisos no está concedido, solicitar los permisos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }




}
