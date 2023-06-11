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
                mostrarAlertBorrarCuenta(v);
            }
        });
    }

    /**
     * Cierra sesión de la cuenta actual cambiando de vista a la actividad principal.
     * <p>
     * Estableciendo los flags FLAG_ACTIVITY_CLEAR_TASK y FLAG_ACTIVITY_NEW_TASK para asegurarse de que se eliminen las actividades
     * previas en la pila de actividades y se inicie una nueva tarea en la actividad principal.
     *
     * @return void
     */
    public void logout() {
        autentificacioFirebase.logout(); // Cierra la sesión del usuario actual llamando al método logout() de autentificacioFirebase.

        // Crea un nuevo Intent para redirigir a la actividad principal.
        Intent intent = new Intent(AjustesActivity.this, PagPrincipalAtivity.class);

        // Establece las banderas del Intent para controlar el comportamiento de la actividad que se va a iniciar.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Inicia la actividad principal utilizando el Intent creado anteriormente.
        startActivity(intent);
    }

    /**
     * Dialogo customizado, alerta al usuario antes de ejecutar una acción
     *
     * @param view vista donde mostrar alert
     * @return void
     */
    public void mostrarAlertCerrarSesion(View view) {
        // Crea un nuevo diálogo personalizado.
        customDialog = new Dialog(this, R.style.Theme_Translucent);

        // Deshabilita el título del diálogo.
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Evita que el diálogo se cierre al tocar fuera de él.
        customDialog.setCancelable(false);

        // Establece el diseño de contenido del diálogo.
        customDialog.setContentView(R.layout.alert_dialog_cerrar_sesion);

        // Obtiene y establece el título del diálogo.
        TextView titulo = (TextView) customDialog.findViewById(R.id.titulo);
        titulo.setText("Cerrar Sesión");

        // Obtiene y establece el contenido del diálogo.
        TextView contenido = (TextView) customDialog.findViewById(R.id.contenido);
        contenido.setText("¿Estás seguro de que quieres cerrar la sesión de esta cuenta?");

        // Configura el botón "Aceptar" del diálogo.
        (customDialog.findViewById(R.id.aceptar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(); // Llama al método logout() para cerrar la sesión.
                customDialog.dismiss(); // Cierra el diálogo.
            }
        });

        // Configura el botón "Cancelar" del diálogo.
        (customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss(); // Cierra el diálogo sin cerrar la sesión.
            }
        });

        customDialog.show(); // Muestra el diálogo.
    }

    /**
     * Dialog customizado que alerta al usuario antes de una acción
     *
     * @param view vista donde mostrar alert
     * @return void
     */
    public void mostrarAlertBorrarCuenta(View view) {
        // Crea un nuevo diálogo personalizado.
        customDialog = new Dialog(this, R.style.Theme_Translucent);

        // Deshabilita el título del diálogo.
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Evita que el diálogo se cierre al tocar fuera de él.
        customDialog.setCancelable(false);

        // Establece el diseño de contenido del diálogo.
        customDialog.setContentView(R.layout.alert_dialog_cerrar_sesion);

        // Obtiene y establece el título del diálogo.
        TextView titulo = (TextView) customDialog.findViewById(R.id.titulo);
        titulo.setText("Borrar Cuenta");

        // Obtiene y establece el contenido del diálogo.
        TextView contenido = (TextView) customDialog.findViewById(R.id.contenido);
        contenido.setText("¿Estás seguro de que quieres borrar esta cuenta?");

        // Configura el botón "Aceptar" del diálogo.
        (customDialog.findViewById(R.id.aceptar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BorrarUsuario(); // Llama al método BorrarUsuario() para eliminar la cuenta.
                customDialog.dismiss(); // Cierra el diálogo.
            }
        });

        // Configura el botón "Cancelar" del diálogo.
        (customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss(); // Cierra el diálogo sin borrar la cuenta.
            }
        });

        customDialog.show(); // Muestra el diálogo.
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
        String userID = autentificacioFirebase.getUid(); // Obtiene el ID del usuario actual.

        // Borrado de chat
        chatsFirebase.deleteChatsByUserId(userID); // Borra los chats asociados al usuario.

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
                                                                    autentificacioFirebase.logout(); // Cierra la sesión

                                                                    // Redirige a la actividad de inicio de sesión
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