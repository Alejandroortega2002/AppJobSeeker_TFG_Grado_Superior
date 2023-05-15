package com.example.testmenu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmenu.R;
import com.example.testmenu.activities.PostDetailActivity;
import com.example.testmenu.entidades.Favoritos;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.FavoritosFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Publicacion, PostsAdapter.ViewHolder> {

    Context context;
    AutentificacioFirebase autentificacioFirebase;
    PublicacionFirebase publicacionFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;

    FavoritosFirebase favoritosFirebase;


    public PostsAdapter(FirestoreRecyclerOptions<Publicacion> options, Context contexto) {
        super(options);
        this.context = contexto;
        autentificacioFirebase = new AutentificacioFirebase();
        publicacionFirebase = new PublicacionFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        favoritosFirebase = new FavoritosFirebase();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Publicacion publicacion) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        holder.textViewTitulo.setText(publicacion.getTitulo().toUpperCase());
        holder.textViewTipoContrato.setText("Contrato: " + publicacion.getCategoria());
        // holder.fechaPublicacion.setText((int) publicacion.getTimeStamp());

        if (publicacion.getImage1() != null) {
            if (!publicacion.getImage1().isEmpty()) {
                Picasso.get().load(publicacion.getImage1()).into(holder.imageViewPost);
            }
        }

        holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("id", postId);
            context.startActivity(intent);
        });

        holder.imgFavoritos.setOnClickListener(view -> {

            Favoritos favoritos = new Favoritos();
            favoritos.setIdUser(autentificacioFirebase.getUid());
            favoritos.setIdPost(postId);
            favoritos.setTimestamp(new Date().getTime());

            favoritos(favoritos, holder);
        });

        getUsuarioInfo(publicacion.getIdUser(), holder);
        getNumeroDeLikes(postId, holder);
        checkComprobarFavoritos(postId, autentificacioFirebase.getUid(), holder);
    }

    private void getNumeroDeLikes(String idPost, final ViewHolder holder) {
        favoritosFirebase.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int numberLikes = queryDocumentSnapshots.size();
                    holder.txtFavoritos.setText(String.valueOf(numberLikes) + " Me gustas");
                }

            }
        });
    }

    private void favoritos(final Favoritos favoritos, final ViewHolder holder) {
        favoritosFirebase.getLikeByPostAndUser(favoritos.getIdPost(), autentificacioFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imgFavoritos.setImageResource(R.drawable.ic_me_gusta_sin_marcar);
                    favoritosFirebase.delete(idLike);
                } else {
                    holder.imgFavoritos.setImageResource(R.drawable.ic_me_gusta_marcado);
                    favoritosFirebase.create(favoritos);
                }
            }
        });

    }


    private void checkComprobarFavoritos(String idPost, String idUser, final ViewHolder holder) {
        favoritosFirebase.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    holder.imgFavoritos.setImageResource(R.drawable.ic_me_gusta_marcado);
                } else {
                    holder.imgFavoritos.setImageResource(R.drawable.ic_me_gusta_sin_marcar);
                }
            }
        });

    }


    private void getUsuarioInfo(String idUser, final ViewHolder holder) {
        usuariosBBDDFirebase.getUsuarios(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        String nUsuario = documentSnapshot.getString("usuario");
                        holder.nombreUsuario.setText("@" + nUsuario.toUpperCase());
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview_post, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo, nombreUsuario, textViewTipoContrato, fechaPublicacion, txtFavoritos;
        ImageView imageViewPost, imgFavoritos;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewTitulo = view.findViewById(R.id.textViewTituloPostCard);
            textViewTipoContrato = view.findViewById(R.id.textViewTipoContrato);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            nombreUsuario = view.findViewById(R.id.nombreUsuario);
            // fechaPublicacion = view.findViewById(R.id.fechaPublicacion);
            txtFavoritos = view.findViewById(R.id.txtFavoritos);
            imgFavoritos = view.findViewById(R.id.imgFavoritos);
            viewHolder = view;
        }
    }


}
