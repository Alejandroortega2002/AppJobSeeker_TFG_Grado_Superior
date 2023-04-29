package com.example.testmenu.fragmentMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.testmenu.R;
import com.example.testmenu.activities.AjustesActivity;
import com.example.testmenu.activities.EditarPerfilActivity;
import com.example.testmenu.databinding.FragmentPerfilBinding;

import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileFragment extends Fragment {

    /*Declaramos su Xml correspondiente a traves del ViewBiding*/
    private FragmentPerfilBinding binding;
    private TextView telefono, nombreU, email;

    private ImageButton btnAjustesPerfil;

    FrameLayout mLinearLayoutEditProfile;

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

        btnAjustesPerfil = binding.btnAjustes;


        final FirebaseUser user = autentificacioFirebase.getUsers();
        idUser = autentificacioFirebase.getUid();


        rellenarInformacionUsuario();

        btnAjustesPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAjustes();
            }
        });


        return root;

    }

    public void irAjustes() {
        Intent i = new Intent(getActivity(), AjustesActivity.class);
        startActivity(i);
    }


    public void rellenarInformacionUsuario() {
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

    public void cerrarSesion() {

    }

    public void cerrarSesionGoogle() {

    }

    public void borrarCuenta() {

    }

    public void editarPerfil() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}