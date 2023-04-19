package com.example.testmenu.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testmenu.databinding.FragmentPerfilBinding;
import com.example.testmenu.ui.perfil.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileFragment extends Fragment {

    /*Declaramos su Xml correspondiente a traves del ViewBiding*/
    private FragmentPerfilBinding binding;
    private TextView telefono, nombreU, email;
    private FirebaseAuth mAuth;
    //FirestoreDataBase
    private FirebaseFirestore mFirestore;
    private String idUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*Instanciamos la clase ViewModel correspondiente*/
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        /*Una vez inflado, con el metodo getRoot() podemos concretar los identificadores de nuestro diseño biding*/
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        telefono = (TextView) binding.nTelefono;
        nombreU = (TextView) binding.nPerfil;
        email = (TextView) binding.pEmail;

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

        reyenarInformacionUsuario();

        return root;

    }

    public void reyenarInformacionUsuario(){
        DocumentReference documentReference = mFirestore.collection("Usuarios").document(idUser);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nombreU.setText(value.getString("usuario"));
                email.setText(value.getString("email"));
                telefono.setText(value.getString("telefono"));

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}