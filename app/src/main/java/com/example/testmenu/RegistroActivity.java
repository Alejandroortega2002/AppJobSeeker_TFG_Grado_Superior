package com.example.testmenu;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private Button btnRegistrar, btnLogin;
    private EditText Nusuario, Email, Telefono, editContrasena, editConfirmContrasena;
    private ProgressDialog barraProgreso;
    //FireBase
    private FirebaseAuth mAuth;
    //FirestoreDataBase
    private FirebaseFirestore mFirestore;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Nusuario = findViewById(R.id.editUsername);
        Email = findViewById(R.id.editEmail);
        Telefono = findViewById(R.id.editTelefono);
        editContrasena = findViewById(R.id.editPassword);
        editConfirmContrasena = findViewById(R.id.editConfirmPassword);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLogin = findViewById(R.id.btnLogin);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarCredenciales();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        barraProgreso = new ProgressDialog(RegistroActivity.this);
    }

    /**
     * Este método verifica las credenciales introducidas comprobando la longitud del nombre de usuario, email, contraseña introducidos,
     * y la coincidencia de la contraseña de confirmación. Si las credenciales son válidas, llama al método registrarUsuario
     * para registrar el usuario con Firebase.
     */
    public void verificarCredenciales() {
        String nusuario = Nusuario.getText().toString();
        String telefono = Telefono.getText().toString();
        String email = Email.getText().toString();
        String contrasena = editContrasena.getText().toString();
        String confirmContrasena = editConfirmContrasena.getText().toString();
        if (nusuario.isEmpty() || nusuario.length() < 5) {
            mostrarError(Nusuario, "Nombre de Usuario no valido");
        } else if (email.isEmpty() || !email.contains("@")) {
            mostrarError(Email, "Email no valido");
        } else if (contrasena.isEmpty() || contrasena.length() < 7) {
            mostrarError(editContrasena, "Contraseña no valida minimo 7 caracteres");
        } else if (confirmContrasena.isEmpty() || !confirmContrasena.equals(contrasena)) {
            mostrarError(editConfirmContrasena, "Contraseña no valida, no coincide.");
        } else {
            mostrarBarraProgreso();
            registrarUsuario(nusuario, telefono, email, contrasena);

        }

    }

    /**
     * Este método registra al usuario en Firebase utilizando el correo electrónico y la contraseña proporcionados. Muestra una barra de progreso
     * durante el proceso de registro y redirige a la actividad de inicio de sesión si el registro se realiza correctamente.
     */

    public void registrarUsuario(final String nUsuario, final String numTelefono, final String email, final String contrasena) {
        mAuth.createUserWithEmailAndPassword(email, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid();

                    Map<String, Object> map = new HashMap<>();
                    map.put("usuario", nUsuario);
                    map.put("email", email);
                    map.put("telefono", numTelefono);
                    Log.d(TAG, "Data to be saved: " + map.toString());

                    mFirestore.collection("Usuarios").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
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
                                barraProgreso.dismiss();
                                // Agregar una excepción para detectar cualquier error al guardar los datos
                                task.getException().printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //ocultar progressBar
                    barraProgreso.dismiss();
                }
            }
        });
    }

    /**
     * Este método muestra una barra de progreso en la pantalla con un título y un mensaje mientras se realiza un proceso de registro.
     * Primero, se establece el título de la barra de progreso en "Proceso de Registro" y el mensaje en "Registrando usuario, espere un momento".
     * Luego, se desactiva la opción de cancelar la barra de progreso cuando se toca fuera de ella y se muestra en la pantalla llamando al método "show()"
     * de la variable "barraProgreso".
     */
    public void mostrarBarraProgreso() {
        //Mostrar ProgressBar
        barraProgreso.setTitle("Proceso de Registro");
        barraProgreso.setMessage("Registrando usuario, espere un momento");
        barraProgreso.setCanceledOnTouchOutside(false);
        barraProgreso.show();
    }

    /**
     * Muestra un mensaje de error en el EditText especificado y coloca el foco en ese EditText.
     *
     * @param input el EditText en el que se mostrará el error.
     * @param s     el mensaje de error que se mostrará en el EditText.
     */
    private void mostrarError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }


}