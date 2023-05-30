/**
 * La clase AjustesActivity representa la actividad de configuración de la aplicación.
 * Permite al usuario realizar acciones como cerrar sesión, editar el perfil,
 * restablecer la contraseña y eliminar la cuenta.
 */
package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.ChatsFirebase;
import com.example.testmenu.firebase.FavoritosFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.firebase.ValoracionFirebase;
import com.example.testmenu.fragmentMenu.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AjustesActivity extends AppCompatActivity {
    private ImageButton btnSalir;
    private TextView btnCerrarSesion, btnBorrarCuenta, btnEditarPerfil, btnRestablecerContrasena;


    private ChatsFirebase chatsFirebase;
    private PublicacionFirebase publicacionFirebase;
    private ValoracionFirebase valoracionFirebase;
    private FavoritosFirebase favoritosFirebase;
    private UsuariosBBDDFirebase usuariosBBDDFirebase;
    private AutentificacioFirebase autentificacioFirebase;

    private Dialog customDialog;

    /**
     * Inicializa la actividad y configura los componentes de la interfaz de usuario y los
     * escuchadores de eventos.
     *
     * @param savedInstanceState El paquete de estado guardado.
     */
    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnSalir = findViewById(R.id.volver_perfil);

        btnCerrarSesion = findViewById(R.id.txtCloseSesion);
        btnEditarPerfil = findViewById(R.id.txtPerfil);
        btnRestablecerContrasena = findViewById(R.id.txtResetPWD);
        btnBorrarCuenta = findViewById(R.id.txtDeletePerfil);

        chatsFirebase = new ChatsFirebase();
        publicacionFirebase = new PublicacionFirebase();
        valoracionFirebase = new ValoracionFirebase();
        favoritosFirebase = new FavoritosFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        autentificacioFirebase = new AutentificacioFirebase();

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
                mostrarAlertCerrarSesion(v);
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
                mostrarAlertBorrarCueta(v);
            }
        });
    }

    /**
     * Cierra sesión de la cuenta actual cambiando de vista.
     * <p>
     * Estableciendo los flags FLAG_ACTIVITY_CLEAR_TASK y FLAG_ACTIVITY_NEW_TASK para asegurarse de que se eliminen las actividades
     * previas en la pila de actividades y se inicie una nueva tarea en la actividad principal.
     *
     * @return void
     */
    public void logout() {
        autentificacioFirebase.logout();
        Intent intent = new Intent(AjustesActivity.this, PagPrincipalAtivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Dialogo customizado, alerta al usuario antes de ejecutar una acción
     *
     * @param view vista donde mostrar alert
     * @return void
     */
    public void mostrarAlertCerrarSesion(View view) {
        // Con este tema personalizado evitamos los bordes por defecto.
        customDialog = new Dialog(this, R.style.Theme_Translucent);
        // Deshabilitamos el título por defecto.
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Obligamos al usuario a pulsar los botones para cerrarlo.
        customDialog.setCancelable(false);
        // Establecemos el contenido de nuestro diálogo.
        customDialog.setContentView(R.layout.alert_dialog_cerrar_sesion);

        TextView titulo = (TextView) customDialog.findViewById(R.id.titulo);
        titulo.setText("Cerrar Sesión");

        TextView contenido = (TextView) customDialog.findViewById(R.id.contenido);
        contenido.setText("¿Estás seguro de que quieres cerrar la sesión de esta cuenta?");

        (customDialog.findViewById(R.id.aceptar)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                logout();
                customDialog.dismiss();
            }
        });

        (customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }

    /**
     * Dialog customizado que alerta al usuario antes de una acción
     *
     * @param view vista donde mostrar alert
     * @return void
     */
    public void mostrarAlertBorrarCueta(View view) {
        // Con este tema personalizado evitamos los bordes por defecto.
        customDialog = new Dialog(this, R.style.Theme_Translucent);
        // Deshabilitamos el título por defecto.
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Obligamos al usuario a pulsar los botones para cerrarlo.
        customDialog.setCancelable(false);
        // Establecemos el contenido de nuestro diálogo.
        customDialog.setContentView(R.layout.alert_dialog_cerrar_sesion);

        TextView titulo = (TextView) customDialog.findViewById(R.id.titulo);
        titulo.setText("Borrar Cuenta");

        TextView contenido = (TextView) customDialog.findViewById(R.id.contenido);
        contenido.setText("¿Estás seguro de que quieres borrar esta cuenta?");

        (customDialog.findViewById(R.id.aceptar)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                BorrarUsuario();
                customDialog.dismiss();
            }
        });

        (customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }

    /**
     * Borra el usuario actualmente autenticado de la base de datos y elimina su cuenta de autenticación.
     * <p>
     * Si se realiza con éxito, se cierra la sesión y se redirige al usuario a la pantalla de inicio de sesión.
     *
     * @return void
     * @throws NullPointerException si el usuario actual no está autenticado
     */

    public void BorrarUsuario() {
        String userID = autentificacioFirebase.getUid();
        // Borrado de chat
        chatsFirebase.deleteChatsByUserId(userID);
        // Borrado de publicaciones
        publicacionFirebase.borrarPublicacionesDeUsuario(userID)
                .addOnSuccessListener(result1 -> {
                    // Borrado de comentarios
                    valoracionFirebase.deleteCommentsByUser(userID)
                            .addOnSuccessListener(aVoid1 -> {
                                // Borrado de favoritos
                                favoritosFirebase.deleteFavoritesByUser(userID)
                                        .addOnSuccessListener(aVoid2 -> {
                                            // Borrado de usuarios
                                            usuariosBBDDFirebase.deleteUsuarios(userID)
                                                    .addOnSuccessListener(aVoid3 -> {
                                                        // Eliminación de cuenta de autenticación
                                                        autentificacioFirebase.deleteAccount()
                                                                .addOnSuccessListener(aVoid4 -> {
                                                                    autentificacioFirebase.logout();
                                                                    Intent intent = new Intent(AjustesActivity.this, LoginActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    String errorMessage = "No se pudo eliminar la cuenta de la autenticación. Inténtelo de nuevo más tarde.";
                                                                    Toast.makeText(AjustesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                                });
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        String errorMessage = "No se pudo eliminar la cuenta de la base de datos. Inténtelo de nuevo más tarde.";
                                                        Toast.makeText(AjustesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            String errorMessage = "No se pudo eliminar los favoritos de la cuenta. Inténtelo de nuevo más tarde.";
                                            Toast.makeText(AjustesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                String errorMessage = "No se pudo eliminar los comentarios de la cuenta. Inténtelo de nuevo más tarde.";
                                Toast.makeText(AjustesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    String errorMessage = "No se pudo eliminar las publicaciones creadas por la cuenta. Inténtelo de nuevo más tarde.";
                    Toast.makeText(AjustesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
    }
}