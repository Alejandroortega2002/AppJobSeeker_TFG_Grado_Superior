package com.example.testmenu.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.utils.ViewedMensajeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class Recuperar_Contrasena_Usuario_Logueado extends AppCompatActivity {

    private Button recuperarContrasena;
    private ImageButton btnVolverEditar;
    private EditText emailRecuperar;
    private AutentificacioFirebase autentificacioFirebase;
    private UsuariosBBDDFirebase usuariosBBDDFirebase;
    private ListenerRegistration mListener;

    /**
     * Método que se llama al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado anteriormente guardado de la actividad.
     */
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena_usuario_logueado);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        recuperarContrasena = findViewById(R.id.RecBtnRecuperar);
        emailRecuperar = findViewById(R.id.RecuperacionUSLEditEmail);
        btnVolverEditar = findViewById(R.id.volver_Ajustes);

        recuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarEmail();
            }
        });

        btnVolverEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        rellenarInformacionUsuario();
    }

    /**Comprobar si el correo asignado es valido y se envía por <b>emailRecuperar()</b>
     *
     * @return void
     */
    public void validarEmail() {
        // Obtener el correo electrónico ingresado por el usuario
        String email = emailRecuperar.getText().toString();

        // Verificar si el campo está vacío o si el formato del correo electrónico es inválido
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Mostrar un error en el campo de correo electrónico
            emailRecuperar.setError("Correo inválido");
            return;
        }

        // Si el correo electrónico es válido, llamar al método enviarEmail() pasando el correo electrónico como argumento
        enviarEmail(email);
    }


    /**
     * Rellena la información del usuario en la interfaz de usuario.
     * <p>
     * Obtiene la información del usuario de la base de datos de Firebase Firestore
     * y actualiza la interfaz de usuario con la información obtenida.
     *
     * @return void
     */

    public void rellenarInformacionUsuario() {
        // Obtener la referencia del documento del usuario actual utilizando su ID de autenticación
        DocumentReference documentReference = usuariosBBDDFirebase.refereciaColeccion(autentificacioFirebase.getUid());

        // Establecer un listener para escuchar los cambios en el documento del usuario
        mListener = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                // Verificar si el objeto DocumentSnapshot es nulo o si se produjo un error al obtenerlo
                if (value != null) {
                    if (error != null) {
                        // Manejar el error de Firebase Firestore
                        Log.w(TAG, "Error al obtener el documento.", error);
                        return;
                    }

                    // Verificar si el objeto DocumentSnapshot existe
                    if (value != null && value.exists()) {
                        // Obtener los valores del objeto DocumentSnapshot
                        String correo = value.getString("email");

                        // Verificar si se recuperó el valor del correo electrónico y establecerlo en el TextView correspondiente
                        if (correo != null) {
                            emailRecuperar.setText(correo);
                        } else {
                            emailRecuperar.setText("Sin correo");
                        }

                    } else {
                        // Manejar el caso en que el objeto DocumentSnapshot es nulo o no existe
                        Log.d(TAG, "El objeto DocumentSnapshot no existe");
                    }
                }
            }
        });
    }

    /**Se manda un mail al correo asignado por parámetro.
     * <p>
     * Se manda una tarea para mandar un correo de reseteo de contraseña al correo pasado por parámetro.
     * Si al completarse tiene éxito, se avisa al usuario por un toast del evento.
     *
     * @param email correo
     * @return void
     */

    public void enviarEmail(String email) {
        // Utilizar el método recuperarContrasena() de la instancia de autenticación de Firebase para enviar un correo electrónico de recuperación de contraseña al usuario
        autentificacioFirebase.recuperarContrasena(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Si el envío del correo electrónico fue exitoso, mostrar un mensaje de éxito
                    Toast.makeText(getApplicationContext(), "Se ha enviado un mensaje a tu correo electrónico.", Toast.LENGTH_SHORT).show();

                    // Crear un intent para abrir la actividad AjustesActivity
                    Intent intent = new Intent(Recuperar_Contrasena_Usuario_Logueado.this, AjustesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Si el envío del correo electrónico no fue exitoso, mostrar un mensaje de error
                    Toast.makeText(getApplicationContext(), "El correo no es correcto, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Método que se llama cuando la actividad se vuelve visible para el usuario.
     */
    @Override
    public void onStart() {
        super.onStart();
        ViewedMensajeHelper.updateOnline(true, Recuperar_Contrasena_Usuario_Logueado.this);
    }

    /**
     * Método que se llama cuando la actividad pierde el foco y no es visible para el usuario.
     */
    @Override
    public void onPause() {
        super.onPause();
        ViewedMensajeHelper.updateOnline(false, Recuperar_Contrasena_Usuario_Logueado.this);
    }

    /**
     * Método que se llama cuando la actividad está a punto de ser destruida.
     */
    public void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.remove();
        }
    }
}
