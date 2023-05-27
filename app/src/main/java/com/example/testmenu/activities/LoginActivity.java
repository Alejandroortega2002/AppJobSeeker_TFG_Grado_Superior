package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Usuarios;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity {


    private EditText editEmail, editContrasena;
    private Button btnLogin, btnRecuperar, btnRegistrar, btnloginGoogle;
    private ProgressDialog barraProgreso;
    AlertDialog mDialog;
    AutentificacioFirebase authFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;


    //Google
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;
    String TAG = "GoogleSignIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editEmail = findViewById(R.id.LeditMail);
        editContrasena = findViewById(R.id.LeditContrasena);

        btnLogin = findViewById(R.id.Lbtnlogin);
        btnloginGoogle = findViewById(R.id.btnGoogle);
        btnRecuperar = findViewById(R.id.LbtnRecuperar);
        btnRegistrar = findViewById(R.id.LbtnRegistrar);

        btnLogin.setOnClickListener(view -> verificarCredenciales());

        btnRecuperar.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, Restablecer_Contrasena.class)));
        btnRegistrar.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistroActivity.class)));
        btnloginGoogle.setOnClickListener(view -> singIn());

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("espere un momento")
                .setCancelable(false).build();


        // Configuración de Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("456658469245-tgmqktro5u6ghg0uu8gr949i6l3rgvou.apps.googleusercontent.com") // Solicita el token de ID del cliente de la aplicación
                .requestEmail() // Solicita el email del usuario
                .build();

        // Creamos el cliente de Google Sign In con las opciones definidas anteriormente
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Obtenemos la instancia de FirebaseAuth y Firestore
        authFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();

        // Creamos un ProgressDialog para mostrar al usuario mientras se realiza el inicio de sesión
        barraProgreso = new ProgressDialog(LoginActivity.this);


    }

    /**
     Se llama cuando la actividad se está iniciando y comprueba si hay una sesión de usuario activa.
     <p>
     Si hay una sesión de usuario activa, se inicia una nueva actividad MainActivity y se eliminan las actividades anteriores de la pila.

     @return void
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (authFirebase.getUserSession() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    //Inicio de Sesión con Google


    /**
     * Iniciar sesión con una cuenta de Google.
     * <p>
     * Se obtiene un intent para iniciar sesión a través del cliente de inicio de sesión de Google (mGoogleSignInClient)
     * mediante la llamada a <b>getSignInIntent()</b>. Se inicia la actividad <b> startActivityForResult()</b>, pasando el intent
     * obtenido y un código de solicitud (RC_SIGN_IN).
     * <p>
     * Se recibirá un resultado en el método onActivityResult() con el mismo código
     * de solicitud (RC_SIGN_IN), permitiendo obtener los datos del usuario y realizar las acciones correspondientes.
     *
     * @return void
     */
    public void singIn() {
        Intent singInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent, RC_SIGN_IN); // Iniciamos la actividad para obtener los datos del usuario
    }


    /**
     * El usuario selecciona una cuenta de Google para iniciar sesión.
     * Se verifica si el requestCode coincide con el código de solicitud de inicio de sesión de Google (RC_SIGN_IN).
     * <p>
     * Si es así, se obtiene la cuenta de Google del objeto Intent mediante el método GoogleSignIn.getSignedInAccountFromIntent(data).
     * Si se puede obtener la cuenta del usuario, se llama al método <b>firebaseAuthWithGoogle()</b> para iniciar sesión en Firebase con las credenciales de Google del usuario.
     * Si no se puede obtener la cuenta, se anota en el registro de eventos de Android (Log) y muestra una ventana emergente.
     *
     * @param requestCode código de solicitud
     * @param resultCode confirmación de solicitud
     * @param data contiene info de la cuenta seleccionada
     * @return void
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithoogle:" + account.getId());

                    // Si se obtiene la cuenta del usuario, se procede con el proceso de autenticación con Firebase
                    firebaseAuthWithGoogle(account);

                } catch (ApiException e) {
                    Log.w(TAG, "Google sing in failed", e);
                }
            } else {
                // Si no se pudo obtener la cuenta del usuario, se muestra un mensaje de error
                Log.d(TAG, "Error, login no exitoso:" + task.getException().toString());
                Toast.makeText(this, "Ocurrio un error. " + task.getException().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Autenticar al usuario utilizando las credenciales de Google.
     * <p>
     * Se llama al método <b>signInWithCredential()</b>
     * para autenticar al usuario con la credencial de Google creada, este retorna un Task y se comprueba la autenticación.
     * Uso de clase anónima que implementa la interfaz <b>"OnCompleteListener<AuthResult>"</b> para manejar el resultado
     * de la autenticación.
     * <p>
     * Se muestra un mensaje de éxito y se inicia la actividad (MainActivity).
     * Si la autenticación falla, se muestra un mensaje de error.
     *
     * @param googleSignInAccount token de Google
     * @return void
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        mDialog.show();
        authFirebase.loginGoogle(googleSignInAccount).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String id = authFirebase.getUid();
                    validarUser(id);

                } else {
                    mDialog.dismiss();
                    // Si la autenticación falla, se muestra un mensaje de error
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                }
            }
        });
    }

    /**
     * Esta función valida si un usuario existe en la base de datos de Firebase.
     * <p>
     * Si el usuario existe, inicia sesión y muestra un mensaje de éxito.
     * Si no existe, crea un nuevo usuario en la base de datos y muestra un mensaje de éxito.
     * Si no se puede almacenar la información del usuario en la base de datos, muestra un mensaje de error.
     *
     * @param id el ID del usuario a validar
     * @return void
     */
    private void validarUser(String id) {

        usuariosBBDDFirebase.getUsuarios(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    mDialog.dismiss();
                    // Si la autenticación es exitosa, se muestra un mensaje de éxito y se inicia la actividad principal de la aplicación
                    Log.d(TAG, "signInWithCredential:success");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Se ha Iniciado Sesion",
                            Toast.LENGTH_SHORT).show();
                    LoginActivity.this.finish();

                } else {

                    String email = authFirebase.getEmail();
                    String nUser = authFirebase.getNuser();
                    String telefono = authFirebase.getTelefono();

                    Usuarios usuario = new Usuarios();
                    usuario.setId(id);
                    usuario.setUsuario(nUser);
                    usuario.setEmail(email);
                    usuario.setTelefono(telefono);
                    usuario.setDescripcion(null);
                    usuario.setBanner(null);
                    usuario.setFotoPerfil(null);

                    usuariosBBDDFirebase.createUsuarios(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mDialog.dismiss();
                                // Si la autenticación es exitosa, se muestra un mensaje de éxito y se inicia la actividad principal de la aplicación
                                Log.d(TAG, "signInWithCredential:success");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Se ha Iniciado Sesion y se han guardado los datos",
                                        Toast.LENGTH_SHORT).show();
                                LoginActivity.this.finish();

                            } else {
                                Toast.makeText(getApplicationContext(), "No se pudo almacenar la información del usuario en la base de datos",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

//Inicio de sesión con Correo Y Contraseña


    /**
     * Este método se encarga de verificar las credenciales y si cumplen los requisitos minimos para el inicio de sesión
     *
     * @return void
     */
    public void verificarCredenciales() {
        String email = editEmail.getText().toString();
        String contrasena = editContrasena.getText().toString();
        // Verificar si el correo electrónico es válido
        if (email.isEmpty() || !email.contains("@")) {
            mostrarError(editEmail, "Email no valido");
            // Verificar si la contraseña es válida
        } else if (contrasena.isEmpty() || contrasena.length() < 7) {
            mostrarError(editContrasena, "Password invalida");
        } else {
            // Si las credenciales son válidas, mostrar la barra de progreso y tratar de iniciar sesión
            mDialog.show();
            iniciarSesionCorreo(email, contrasena);
        }
    }

    /**
     * Este método se encarga de iniciar sesion con firebase.
     * <p>
     * Si el inicio de sesión fue exitoso, ocultar la barra de progreso y redirigir al usuario a MainActivity
     * Si falla, ocultar la barra de progreso y mostrar un mensaje de error.
     *
     * @param email el correo que usamos para iniciar sesión
     * @param contrasena el pwd que asociamos al email
     * @return void
     */

    public void iniciarSesionCorreo(String email, String contrasena) {
        // Iniciar sesión con el correo electrónico y la contraseña ingresados
        mDialog.show();
        authFirebase.login(email, contrasena).addOnCompleteListener(this, task -> {

            if (task.isSuccessful()) {
                // Si el inicio de sesión fue exitoso, ocultar la barra de progreso y redirigir al usuario a MainActivity
                mDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // Mostrar un mensaje de inicio de sesión exitoso
                Toast.makeText(getApplicationContext(), "Se ha Iniciado Sesion",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Si el inicio de sesión falla, ocultar la barra de progreso y mostrar un mensaje de error
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Incorrecto.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Este método se encarga de mostrar la barra de progreso
     */
    /** public void mostrarBarraProgreso() {
     // Mostrar una barra de progreso mientras se procesa el inicio de sesión
     barraProgreso.setTitle("Proceso de Registro");
     barraProgreso.setMessage("Registrando usuario, espere un momento");
     barraProgreso.setCanceledOnTouchOutside(false);
     barraProgreso.show();
     }*/

    /**
     * Este método se encarga de mostrar los errores si algo ha salido mal
     *
     * @param input edittext donde se muestre el mensaje
     * @param s texto que se desea mostrar como error principal
     * @return void
     */

    private void mostrarError(EditText input, String s) {
        // Mostrar un mensaje de error en caso de que el correo electrónico o la contraseña sean inválidos
        input.setError(s);
        input.requestFocus();
    }


}