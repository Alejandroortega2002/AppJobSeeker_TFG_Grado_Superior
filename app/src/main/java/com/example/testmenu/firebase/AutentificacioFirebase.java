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

    public AutentificacioFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> registro(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public void logout() {
        if (mAuth != null) {
            mAuth.signOut();
        }
    }
    public Task<AuthResult> loginGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credenciales = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        return mAuth.signInWithCredential(credenciales);
    }

    public Task<Void> recuperarContrasena(String email) {
        return mAuth.sendPasswordResetEmail(email);
    }

    public FirebaseUser getUsers() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser();
        } else {
            return null;
        }

    }

    public String getUid() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        } else {
            return null;
        }

    }

    public FirebaseUser getUserSession() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser();
        } else {
            return null;
        }

    }

    public String getEmail() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getEmail();
        } else {
            return null;
        }

    }

    public String getNuser() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getDisplayName();
        } else {
            return null;
        }

    }

    public String getTelefono() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getPhoneNumber();
        } else {
            return null;
        }

    }
    public Task<Void> deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.delete();
        } else {
            return null;
        }
    }



}
