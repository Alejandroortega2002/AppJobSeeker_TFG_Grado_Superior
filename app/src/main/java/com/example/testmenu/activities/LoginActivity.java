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

     Este método se llama cuando la actividad se está iniciando y comprueba si hay una sesión de usuario activa.
     Si hay una sesión de usuario activa, se inicia una nueva actividad MainActivity y se eliminan las actividades anteriores de la pila.
     Si no hay una sesión de usuario activa, no se realiza ninguna acción adicional y se muestra la actividad LoginActivity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(authFirebase.getUserSession()!=null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    //Inicio de Sesión con Google


    /**
     * Este método se utiliza para iniciar sesión con una cuenta de Google. Primero, se obtiene un intent para iniciar sesión a través del cliente de inicio de sesión de Google (mGoogleSignInClient)
     * mediante la llamada a getSignInIntent(). Luego, se inicia la actividad para obtener los datos del usuario utilizando startActivityForResult(), pasando el intent
     * obtenido y un código de solicitud (RC_SIGN_IN). Cuando se complete la actividad de inicio de sesión, se recibirá un resultado en el método onActivityResult() con el mismo código
     * de solicitud (RC_SIGN_IN), donde se pueden obtener los datos del usuario y realizar las acciones correspondientes.
     */
    public void singIn() {
        Intent singInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent, RC_SIGN_IN); // Iniciamos la actividad para obtener los datos del usuario
    }


    /**
     * Este método es llamado después de que el usuario selecciona una cuenta de Google para iniciar sesión en la aplicación. El método recibe
     * tres parámetros: requestCode, resultCode y data.
     * El requestCode es un código de solicitud que se utiliza para identificar la solicitud de inicio de sesión de Google en la actividad que se llama.
     * El resultCode es un código que indica si la solicitud fue exitosa o no.
     * El data es un objeto Intent que contiene información sobre la cuenta de Google seleccionada por el usuario.
     * Dentro del método, se verifica si el requestCode coincide con el código de solicitud de inicio de sesión de Google (RC_SIGN_IN).
     * Si es así, se obtiene la cuenta de Google del objeto Intent mediante el método GoogleSignIn.getSignedInAccountFromIntent(data).
     * Si se puede obtener la cuenta del usuario, se llama al método firebaseAuthWithGoogle() para iniciar sesión en Firebase con las credenciales de Google del usuario.
     * Si no se puede obtener la cuenta del usuario, se muestra un mensaje de error en el registro de eventos de Android (Log) y en una ventana emergente Toast para informar al usuario que el inicio de sesión no fue exitoso.
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
     * Este método se encarga de autenticar al usuario utilizando las credenciales de Google. Este método recibe como parámetro un token de identificación
     * de Google (idToken) que se utiliza para crear una credencial de autenticación (credential). A continuación, se llama al método signInWithCredential
     * de la instancia de mAuth (objeto de autenticación de Firebase) para autenticar al usuario con la credencial de Google creada.
     * El método signInWithCredential retorna un Task que indica si la autenticación fue exitosa o no. Si la autenticación es exitosa,
     * se muestra un mensaje de éxito y se inicia la actividad principal de la aplicación (MainActivity). Si la autenticación falla, se muestra un mensaje de error.
     * Este método utiliza una clase anónima que implementa la interfaz OnCompleteListener<AuthResult> para manejar el resultado
     * de la autenticación. En el método onComplete de esta clase anónima, se verifica si la autenticación fue exitosa o no, y se realiza la acción correspondiente en cada caso.
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        //mDialog.show();
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
     * Este método se encarga de verificar las credenciales
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
     * Este método se encarga de iniciar sesion con firebase
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
     */
    private void mostrarError(EditText input, String s) {
        // Mostrar un mensaje de error en caso de que el correo electrónico o la contraseña sean inválidos
        input.setError(s);
        input.requestFocus();
    }


}