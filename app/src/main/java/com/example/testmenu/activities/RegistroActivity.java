package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Usuarios;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import dmax.dialog.SpotsDialog;

public class RegistroActivity extends AppCompatActivity {

    private Button btnRegistrar, btnLogin;
    private EditText Nusuario, Email, Telefono, editContrasena, editConfirmContrasena;
    private ProgressDialog barraProgreso;
    private AlertDialog mDialog;

    //Firebase

    private AutentificacioFirebase authFirebase;
    private UsuariosBBDDFirebase usuariosBBDDFirebase;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Nusuario = findViewById(R.id.editUsername);
        Email = findViewById(R.id.editEmail);
        Telefono = findViewById(R.id.editTelefono);
        editContrasena = findViewById(R.id.editPassword);
        editConfirmContrasena = findViewById(R.id.editConfirmPassword);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLogin = findViewById(R.id.btnLogin);

        btnRegistrar.setOnClickListener(view -> verificarCredenciales());

        btnLogin.setOnClickListener(v -> startActivity(new Intent(RegistroActivity.this, LoginActivity.class)));

        authFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        barraProgreso = new ProgressDialog(RegistroActivity.this);


        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("espere un momento")
                .setCancelable(false).build();
    }


    /**
     * Comprueba los datos del usuario siguiendo unos criterios necesarios.
     * <p>
     * Se verifica la longitud del nombre de usuario, email, contraseña introducidos,
     * y la coincidencia de la contraseña de confirmación. Si las credenciales son válidas, llama al método <b>registrarUsuario()<b>
     * para registrar el usuario con Firebase.
     *
     * @return void
     */
    public void verificarCredenciales() {
        // Obtener los valores de los campos de texto
        String nusuario = Nusuario.getText().toString();
        String telefono = Telefono.getText().toString();
        String email = Email.getText().toString();
        String contrasena = editContrasena.getText().toString();
        String confirmContrasena = editConfirmContrasena.getText().toString();

        // Validar el nombre de usuario
        if (nusuario.isEmpty() || nusuario.length() < 5) {
            mostrarError(Nusuario, "Nombre de usuario no válido. Debe tener al menos 5 caracteres.");
            return;
        }

        // Verificar si el nombre de usuario ya existe
        existeNombreUsuario(nusuario, new VerificacionCallback() {
            @Override
            public void onVerificacionCompleta(boolean existe) {
                if (existe) {
                    return;
                }

                // Validar el número de teléfono
                if (telefono.isEmpty() || !esNumeroTelefonoValido(telefono)) {
                    mostrarError(Telefono, "Número de teléfono no válido. Debe contener solo dígitos.");
                    return;
                }

                // Validar el correo electrónico
                if (email.isEmpty() || !esEmailValido(email)) {
                    mostrarError(Email, "Correo electrónico no válido. Debe tener un formato válido.");
                    return;
                }

                // Verificar si el correo electrónico ya existe
                existeCorreoElectronico(email, new VerificacionCallback() {
                    @Override
                    public void onVerificacionCompleta(boolean existe) {
                        if (existe) {
                            return;
                        }

                        // Validar la contraseña
                        if (contrasena.isEmpty() || contrasena.length() < 8) {
                            mostrarError(editContrasena, "Contraseña no válida. Debe tener al menos 8 caracteres.");
                            return;
                        }

                        // Verificar si la contraseña cumple con los requisitos de seguridad
                        if (!requisitosContrasena(contrasena)) {
                            mostrarError(editContrasena, "La contraseña no cumple con los requisitos de seguridad. (Tiene que tener al menos una mayúscula, una minúscula, un número y un carácter especial)");
                            return;
                        }

                        // Verificar que las contraseñas coinciden
                        if (!contrasena.equals(confirmContrasena)) {
                            mostrarError(editConfirmContrasena, "Las contraseñas no coinciden.");
                            return;
                        }

                        // Mostrar el diálogo de carga
                        mDialog.show();

                        // Registrar al usuario
                        registrarUsuario(nusuario, telefono, email, contrasena);
                    }
                });
            }
        });
    }

    /**
     * Verifica si un número de teléfono es válido.
     *
     * @param telefono El número de teléfono a verificar.
     * @return true si el número de teléfono es válido, false de lo contrario.
     */
    public boolean esNumeroTelefonoValido(String telefono) {
        return telefono.matches("\\d+"); // Utiliza la función matches() para verificar si el número de teléfono contiene solo dígitos.
    }

    /**
     * Verifica si un correo electrónico es válido.
     *
     * @param email El correo electrónico a verificar.
     * @return true si el correo electrónico es válido, false de lo contrario.
     */
    public boolean esEmailValido(String email) {
        return email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"); // Utiliza la función matches() para verificar si el correo electrónico tiene un formato válido.
    }

    /**
     * Verifica si una contraseña cumple con los requisitos de seguridad.
     *
     * @param contrasena La contraseña a verificar.
     * @return true si la contraseña cumple con los requisitos de seguridad, false de lo contrario.
     */
    public boolean requisitosContrasena(String contrasena) {
        return contrasena.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^_&+=]).{8,}"); // Utiliza la función matches() para verificar si la contraseña cumple con los requisitos de seguridad: al menos una minúscula, una mayúscula, un dígito y un carácter especial, con una longitud mínima de 8 caracteres.
    }

    /**
     * Interfaz de callback para la verificación.
     */
    public interface VerificacionCallback {
        /**
         * Método llamado cuando la verificación está completa.
         *
         * @param existe Indica si existe o no la verificación.
         */
        void onVerificacionCompleta(boolean existe);
    }

    /**
     * Verifica si un nombre de usuario ya existe.
     *
     * @param nombreUser El nombre de usuario a verificar.
     * @param callback   El callback para la verificación.
     */
    public void existeNombreUsuario(String nombreUser, VerificacionCallback callback) {
        usuariosBBDDFirebase.getNombreUser(nombreUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean existe = !task.getResult().isEmpty();
                    if (existe) {
                        mostrarError(Nusuario, "El nombre de usuario ya está registrado."); // Muestra un error si el nombre de usuario ya está registrado.
                    }
                    callback.onVerificacionCompleta(existe); // Llama al método onVerificacionCompleta() del callback indicando si existe o no la verificación.
                } else {
                    mostrarError(Nusuario, "Error al verificar el nombre de usuario."); // Muestra un error si ocurre un error al verificar el nombre de usuario.
                    callback.onVerificacionCompleta(true); // Llama al método onVerificacionCompleta() del callback indicando que existe la verificación debido a un error.
                }
            }
        });
    }

    /**
     * Verifica si un correo electrónico ya existe.
     *
     * @param email    El correo electrónico a verificar.
     * @param callback El callback para la verificación.
     */
    public void existeCorreoElectronico(String email, VerificacionCallback callback) {
        usuariosBBDDFirebase.getCorreoUser(email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean existe = !task.getResult().isEmpty();
                    if (existe) {
                        mostrarError(Email, "El correo del usuario ya está registrado."); // Muestra un error si el correo electrónico ya está registrado.
                    }
                    callback.onVerificacionCompleta(existe); // Llama al método onVerificacionCompleta() del callback indicando si existe o no la verificación.
                } else {
                    mostrarError(Email, "Error al verificar el correo del usuario."); // Muestra un error si ocurre un error al verificar el correo electrónico.
                    callback.onVerificacionCompleta(true); // Llama al método onVerificacionCompleta() del callback indicando que existe la verificación debido a un error.
                }
            }
        });
    }

    /**
     * Este método registra al usuario en Firebase. Muestra una barra de progreso
     * durante el proceso de registro y redirige a la actividad de inicio de sesión si el registro se realiza correctamente.
     *
     * @param nUsuario    username
     * @param numTelefono telefono
     * @param email       correo
     * @param contrasena  contrasena
     * @return void
     */

    public void registrarUsuario(final String nUsuario, final String numTelefono, final String email, final String contrasena) {
        mDialog.show(); // Muestra un diálogo de carga o progreso.

        // Realiza el registro del usuario en Firebase Authentication.
        authFirebase.registro(email, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = authFirebase.getUid(); // Obtiene el ID del usuario autenticado.

                    // Crea un objeto Usuarios con los datos del usuario.
                    Usuarios usuario = new Usuarios();
                    usuario.setId(id);
                    usuario.setUsuario(nUsuario);
                    usuario.setEmail(email);
                    usuario.setTelefono(numTelefono);
                    usuario.setDescripcion(null);
                    usuario.setBanner(null);
                    usuario.setMedia(0.0F);
                    usuario.setFotoPerfil(null);
                    usuario.setTimeStamp(new Date().getTime());

                    // Crea el usuario en la base de datos de Firebase Firestore.
                    usuariosBBDDFirebase.createUsuarios(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.show(); // Muestra un diálogo de carga o progreso.

                            if (task.isSuccessful()) {
                                // Registro exitoso -> Mostrar toast
                                Toast.makeText(getApplicationContext(), "Se ha creado el usuario y se ha guardado en la base de datos",
                                        Toast.LENGTH_SHORT).show();

                                // Oculta la barra de progreso.
                                barraProgreso.dismiss();

                                // Redirecciona al usuario a la actividad de inicio de sesión (LoginActivity).
                                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "No se ha podido guardar el usuario en la base de datos",
                                        Toast.LENGTH_SHORT).show();

                                // Oculta la barra de progreso.
                                mDialog.dismiss();

                                // Agrega una excepción para detectar cualquier error al guardar los datos en la base de datos.
                                task.getException().printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();

                    // Oculta la barra de progreso.
                    mDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barraProgreso != null && barraProgreso.isShowing()) {
            barraProgreso.dismiss();
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * Muestra un mensaje de error en el EditText especificado y coloca el foco en ese EditText.
     *
     * @param input el EditText en el que se mostrará el error.
     * @param s     el mensaje de error que se mostrará en el EditText.
     */
    public void mostrarError(EditText input, String s) {
        input.setError(s); // Establece un mensaje de error en el campo de entrada.
        input.requestFocus(); // Pone el foco en el campo de entrada, haciendo que obtenga el enfoque.
    }


}