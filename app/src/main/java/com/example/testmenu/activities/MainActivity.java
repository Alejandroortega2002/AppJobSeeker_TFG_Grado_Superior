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
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    TokenFirebase mTokenFirebase;
    AutentificacioFirebase mAutentificationFirebase;

    UsuariosBBDDFirebase mUsuarioFirebase;

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
        mTokenFirebase = new TokenFirebase();
        mAutentificationFirebase = new AutentificacioFirebase();
        mUsuarioFirebase = new UsuariosBBDDFirebase();
        createToken();
        askNotificationPermission();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }



        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_filtro, R.id.navigation_perfil, R.id.navigation_chat, R.id.navigation_inicio)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMensajeHelper.updateOnline(true,MainActivity.this);
    }



    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false,MainActivity.this);
    }



    private void createToken(){
        mTokenFirebase.create(mAutentificationFirebase.getUid());
    }
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


}