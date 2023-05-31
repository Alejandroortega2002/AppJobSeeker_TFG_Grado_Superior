package com.example.testmenu.firebase;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AutentificacioFirebase {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * Constructor de la clase AutentificacioFirebase.
     * Inicializa la instancia de FirebaseAuth.
     */
    public AutentificacioFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Registra un usuario en Firebase.
     *
     * @param email    Dirección de correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return Tarea que representa la operación de registro del usuario en Firebase.
     */
    public Task<AuthResult> registro(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    /**
     * Inicia sesión de un usuario en Firebase.
     *
     * @param email    Dirección de correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return Tarea que representa la operación de inicio de sesión del usuario en Firebase.
     */
    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    public void logout() {
        if (mAuth != null) {
            mAuth.signOut();
        }
    }

    /**
     * Inicia sesión utilizando las credenciales de Google.
     *
     * @param googleSignInAccount Cuenta de inicio de sesión de Google.
     * @return Tarea que representa la operación de inicio de sesión utilizando las credenciales de Google.
     */
    public Task<AuthResult> loginGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credenciales = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        return mAuth.signInWithCredential(credenciales);
    }

    /**
     * Restablece la contraseña de un usuario.
     *
     * @param email Dirección de correo electrónico del usuario.
     * @return Tarea que representa la operación de restablecimiento de contraseña.
     * Se envía un correo electrónico al usuario para restablecer su contraseña.
     */
    public Task<Void> recuperarContrasena(String email) {
        return mAuth.sendPasswordResetEmail(email);
    }

    /**
     * Obtiene el objeto FirebaseUser del usuario actual.
     *
     * @return Objeto FirebaseUser del usuario actual o null si no hay usuario autenticado.
     */
    public FirebaseUser getUsers() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser();
        } else {
            return null;
        }
    }

    /**
     * Obtiene el UID del usuario actual.
     *
     * @return UID del usuario actual o null si no hay usuario autenticado.
     */
    public String getUid() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        } else {
            return null;
        }
    }

    /**
     * Obtiene el objeto FirebaseUser de la sesión actual del usuario.
     *
     * @return Objeto FirebaseUser de la sesión actual del usuario o null si no hay usuario autenticado.
     */
    public FirebaseUser getUserSession() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser();
        } else {
            return null;
        }
    }

    /**
     * Obtiene el correo electrónico del usuario actual.
     *
     * @return Correo electrónico del usuario actual o null si no hay usuario autenticado.
     */
    public String getEmail() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getEmail();
        } else {
            return null;
        }
    }

    /**
     * Obtiene el nombre de usuario del usuario actual.
     *
     * @return Nombre de usuario del usuario actual o null si no hay usuario autenticado o el nombre no está disponible.
     */
    public String getNuser() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getDisplayName();
        } else {
            return null;
        }
    }

    /**
     * Obtiene el número de teléfono del usuario actual.
     *
     * @return Número de teléfono del usuario actual o null si no hay usuario autenticado o el número no está disponible.
     */
    public String getTelefono() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getPhoneNumber();
        } else {
            return null;
        }
    }

    /**
     * Elimina la cuenta del usuario actual.
     *
     * @return Tarea que representa la operación de eliminación de la cuenta del usuario.
     */
    public Task<Void> deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.delete();
        } else {
            return null;
        }
    }
}
