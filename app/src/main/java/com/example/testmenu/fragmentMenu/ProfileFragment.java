package com.example.testmenu.fragmentMenu;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testmenu.activities.AjustesActivity;
import com.example.testmenu.activities.MisFavoritosActivity;
import com.example.testmenu.activities.MisOfertasActivity;
import com.example.testmenu.activities.ValoracionActivity;
import com.example.testmenu.databinding.FragmentPerfilBinding;

import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    /*Declaramos su Xml correspondiente a traves del ViewBiding*/
    private FragmentPerfilBinding binding;
    private TextView telefono, nombreU, email, descripcion, numeroDeOrfetas;

    private ImageView fotoBanner;
    private CircleImageView fotoPerfil;

    private LinearLayout btnMisOfertas, btnFavoritos, btnValoracion;


    private ImageButton btnAjustesPerfil;

    FrameLayout mLinearLayoutEditProfile;

    AutentificacioFirebase autentificacioFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;
    PublicacionFirebase publicacionFirebase;
    ListenerRegistration mListener;


    private String idUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        /*Una vez inflado, con el metodo getRoot() podemos concretar los identificadores de nuestro diseño biding*/
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        publicacionFirebase = new PublicacionFirebase();

        telefono = (TextView) binding.nTelefono;
        nombreU = (TextView) binding.nPerfil;
        email = (TextView) binding.pEmail;
        descripcion = (TextView) binding.pDescripcion;
        numeroDeOrfetas = (TextView) binding.nPublicaciones;
        fotoBanner = (ImageView) binding.banner;
        fotoPerfil = (CircleImageView) binding.fotoPerfil;
        btnFavoritos = binding.btnFavoritos;
        btnMisOfertas = binding.btnMisOfertas;

        btnValoracion = binding.btnValoraciones;


        btnAjustesPerfil = binding.btnAjustes;


        final FirebaseUser user = autentificacioFirebase.getUsers();
        idUser = autentificacioFirebase.getUid();


        rellenarInformacionUsuario();
        getNumeroPublicaciones();

        btnAjustesPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAjustes();
            }
        });

        btnMisOfertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irOfertas();
            }
        });

        btnValoracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ValoracionActivity.class);
                intent.putExtra("idUsuario", autentificacioFirebase.getUid());
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MisFavoritosActivity.class);
                startActivity(intent);
            }
        });

        return root;

    }

    public void getNumeroPublicaciones() {
        publicacionFirebase.getPublicacionDeUsuario(autentificacioFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPublicaciones = queryDocumentSnapshots.size();
                String numeroOfertas = String.valueOf(numeroPublicaciones);
                numeroDeOrfetas.setText(numeroOfertas);
            }
        });
    }

    public void irAjustes() {
        Intent i = new Intent(getActivity(), AjustesActivity.class);
        startActivity(i);
    }

    //    public void irFavoritos() {
//        Intent i = new Intent(getActivity(), Favoritos.class);
//        startActivity(i);
//    }
    public void irOfertas() {
        Intent i = new Intent(getActivity(), MisOfertasActivity.class);
        startActivity(i);
    }

    public void rellenarInformacionUsuario() {
        DocumentReference documentReference = usuariosBBDDFirebase.refereciaColeccion(idUser);
         mListener =documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value !=null){
                    if (error != null) {
                        // Manejar el error de Firebase Firestore
                        Log.w(TAG, "Error al obtener el documento.", error);
                        return;
                    }
                    if (value != null && value.exists()) {
                        // Obtener los valores del objeto DocumentSnapshot
                        String nombre = value.getString("usuario");
                        String correo = value.getString("email");
                        String ntelefono = value.getString("telefono");
                        String descrip = value.getString("descripcion");


                        // Verificar si los valores obtenidos son nulos antes de establecer el texto en los TextViews
                        if (value.contains("fotoPerfil")) {
                            String perfil = value.getString("fotoPerfil");
                            if (perfil != null) {
                                if (!perfil.isEmpty()) {
                                    Picasso.get().load(perfil).into(fotoPerfil);
                                }
                            }
                        }
                        if (value.contains("banner")) {
                            String banner = value.getString("banner");
                            if (banner != null) {
                                if (!banner.isEmpty()) {
                                    Picasso.get().load(banner).into(fotoBanner);
                                }
                            }
                        }
                        if (nombre != null) {
                            nombreU.setText(nombre);
                        } else {
                            nombreU.setText("Sin nombre");
                        }
                        if (correo != null) {
                            email.setText(correo);
                        } else {
                            email.setText("Sin correo");
                        }
                        if (telefono != null) {
                            telefono.setText(ntelefono);
                        } else {
                            telefono.setText("Sin teléfono");
                        }
                        if (descripcion != null) {
                            descripcion.setText(descrip);
                        } else {
                            descripcion.setText("Sin descripción");
                        }
                    } else {
                        // Manejar el caso en que el objeto DocumentSnapshot es nulo o no existe
                        Log.d(TAG, "El objeto DocumentSnapshot no existe");
                    }
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mListener!=null){
            mListener.remove();
        }
        binding = null;
    }
}