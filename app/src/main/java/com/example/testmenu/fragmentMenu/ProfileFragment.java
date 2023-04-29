package com.example.testmenu.fragmentMenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.testmenu.activities.LoginActivity;
import com.example.testmenu.activities.Restablecer_Contrasena;
import com.example.testmenu.databinding.FragmentPerfilBinding;

import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.checkerframework.common.subtyping.qual.Bottom;

public class ProfileFragment extends Fragment {

    /*Declaramos su Xml correspondiente a traves del ViewBiding*/
    private FragmentPerfilBinding binding;
    private TextView telefono, nombreU, email;

    private ImageButton btnEditarPerfil;
    private ImageButton btnCerrarSesion;
    private ImageButton btnBorrarCuenta;


    AutentificacioFirebase autentificacioFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;

    private String idUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        /*Instanciamos la clase ViewModel correspondiente*/
//        ProfileViewModel profileViewModel =
//                new ViewModelProvider(this).get(ProfileViewModel.class);

        /*Una vez inflado, con el metodo getRoot() podemos concretar los identificadores de nuestro diseño biding*/
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        



        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();

        telefono = (TextView) binding.nTelefono;
        nombreU = (TextView) binding.nPerfil;
        email = (TextView) binding.pEmail;

        btnCerrarSesion = binding.btnCerrarSesion;
        btnBorrarCuenta = binding.btnBorrarCuenta;
        btnEditarPerfil = binding.btnEditarPerfil;




        final FirebaseUser user = autentificacioFirebase.getUsers();
        idUser = autentificacioFirebase.getUid();


        reyenarInformacionUsuario();
//        btnBorrarCuenta.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//
//        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//                cerrarSesion();
//            }
//        });

        return root;

    }

    public void reyenarInformacionUsuario() {
        DocumentReference documentReference = usuariosBBDDFirebase.refereciaColeccion(idUser);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nombreU.setText(value.getString("usuario"));
                email.setText(value.getString("email"));
                telefono.setText(value.getString("telefono"));

            }
        });
    }

    public void cerrarSesion(){

    }

    public void cerrarSesionGoogle(){

    }
    public void borrarCuenta(){

    }

    public void editarPerfil(){

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}