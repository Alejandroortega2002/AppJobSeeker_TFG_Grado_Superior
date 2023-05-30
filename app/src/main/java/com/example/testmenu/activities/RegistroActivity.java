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
        String nusuario = Nusuario.getText().toString();
        String telefono = Telefono.getText().toString();
        String email = Email.getText().toString();
        String contrasena = editContrasena.getText().toString();
        String confirmContrasena = editConfirmContrasena.getText().toString();

        if (nusuario.isEmpty() || nusuario.length() < 5) {
            mostrarError(Nusuario, "Nombre de usuario no válido. Debe tener al menos 5 caracteres.");
            return;
        }

        existeNombreUsuario(nusuario, new VerificacionCallback() {
            @Override
            public void onVerificacionCompleta(boolean existe) {
                if (existe) {
                    return;
                }

                if (telefono.isEmpty() || !esNumeroTelefonoValido(telefono)) {
                    mostrarError(Telefono, "Número de teléfono no válido. Debe contener solo dígitos.");
                    return;
                }

                if (email.isEmpty() || !esEmailValido(email)) {
                    mostrarError(Email, "Correo electrónico no válido. Debe tener un formato válido.");
                    return;
                }

                existeCorreoElectronico(email, new VerificacionCallback() {
                    @Override
                    public void onVerificacionCompleta(boolean existe) {
                        if (existe) {
                            return;
                        }

                        if (contrasena.isEmpty() || contrasena.length() < 8) {
                            mostrarError(editContrasena, "Contraseña no válida. Debe tener al menos 8 caracteres.");
                            return;
                        }

                        if (!requisitosContrasena(contrasena)) {
                            mostrarError(editContrasena, "La contraseña no cumple con los requisitos de seguridad.(Tiene que tener al menos una mayúscula, una minúscula, un número y un caracter especial)");
                            return;
                        }

                        if (!contrasena.equals(confirmContrasena)) {
                            mostrarError(editConfirmContrasena, "Las contraseñas no coinciden.");
                            return;
                        }

                        mDialog.show();
                        registrarUsuario(nusuario, telefono, email, contrasena);
                    }
                });
            }
        });
    }


    public boolean esNumeroTelefonoValido(String telefono) {
        return telefono.matches("\\d+");
    }

    public boolean esEmailValido(String email) {
        return email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    }

    public boolean requisitosContrasena(String contrasena) {
        return contrasena.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^_&+=]).{8,}");
    }

    public interface VerificacionCallback {
        void onVerificacionCompleta(boolean existe);
    }


    public void existeNombreUsuario(String nombreUser, VerificacionCallback callback) {
        usuariosBBDDFirebase.getNombreUser(nombreUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean existe = !task.getResult().isEmpty();
                    if (existe) {
                        mostrarError(Nusuario, "El nombre de usuario ya está registrado.");
                    }
                    callback.onVerificacionCompleta(existe);
                } else {
                    mostrarError(Nusuario, "Error al verificar el nombre de usuario.");
                    callback.onVerificacionCompleta(true);
                }
            }
        });
    }

    public void existeCorreoElectronico(String email, VerificacionCallback callback) {
        usuariosBBDDFirebase.getCorreoUser(email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean existe = !task.getResult().isEmpty();
                    if (existe) {
                        mostrarError(Email, "El correo del usuario ya está registrado.");
                    }
                    callback.onVerificacionCompleta(existe);
                } else {
                    mostrarError(Email, "Error al verificar el correo del usuario.");
                    callback.onVerificacionCompleta(true);
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
        mDialog.show();
        authFirebase.registro(email, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = authFirebase.getUid();

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


                    usuariosBBDDFirebase.createUsuarios(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.show();
                            if (task.isSuccessful()) {
                                //Exitoso -> Mostrar toast
                                Toast.makeText(getApplicationContext(), "Se ha creado el usuario y se ha guardado en la base de datos",
                                        Toast.LENGTH_SHORT).show();
                                //ocultar progressBar
                                barraProgreso.dismiss();
                                //redireccionar - intent a login
                                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "No se ha podido guardar el usuario en la base de datos",
                                        Toast.LENGTH_SHORT).show();
                                //ocultar progressBar
                                mDialog.dismiss();
                                // Agregar una excepción para detectar cualquier error al guardar los datos
                                task.getException().printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //ocultar progressBar
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
     * Este método muestra una barra de progreso en la pantalla con un título y un mensaje mientras se realiza un proceso de registro.
     * Primero, se establece el título de la barra de progreso en "Proceso de Registro" y el mensaje en "Registrando usuario, espere un momento".
     * Luego, se desactiva la opción de cancelar la barra de progreso cuando se toca fuera de ella y se muestra en la pantalla llamando al método "show()"
     * de la variable "barraProgreso".
     */
    //   public void mostrarBarraProgreso() {
    //Mostrar ProgressBar
    //      barraProgreso.setTitle("Proceso de Registro");
    //    barraProgreso.setMessage("Registrando usuario, espere un momento");
    ////barraProgreso.show();
    //   }

    /**
     * Muestra un mensaje de error en el EditText especificado y coloca el foco en ese EditText.
     *
     * @param input el EditText en el que se mostrará el error.
     * @param s     el mensaje de error que se mostrará en el EditText.
     */
    public void mostrarError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }


}