package com.example.testmenu.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter2 extends FirestoreRecyclerAdapter<Publicacion, PostsAdapter2.ViewHolder> {

    Context context;
    AutentificacioFirebase autentificacioFirebase;
    PublicacionFirebase publicacionFirebase;

    UsuariosBBDDFirebase usuariosBBDDFirebase;
    FavoritosFirebase favoritosFirebase;

    Dialog customDialog;

    ListenerRegistration mListener;


    public PostsAdapter2(FirestoreRecyclerOptions<Publicacion> options, Context contexto) {
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
        holder.textViewSector.setText("Sector: " + publicacion.getSector());

        if (publicacion.getIdUser().equals(autentificacioFirebase.getUid())) {
            holder.btnCerrar.setVisibility(View.VISIBLE);
        } else {
            holder.btnCerrar.setVisibility(View.GONE);
        }

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

        holder.btnCerrar.setOnClickListener(view -> {
            mostrarAlertBorrarPublicacion(postId);
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

    private void getNumeroDeLikes(String idPost, final PostsAdapter2.ViewHolder holder) {
        mListener = favoritosFirebase.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int numberLikes = queryDocumentSnapshots.size();
                    holder.txtFavoritos.setText(String.valueOf(numberLikes) + " Me gustas");
                }

            }
        });
    }

    private void favoritos(final Favoritos favoritos, final PostsAdapter2.ViewHolder holder) {
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


    private void checkComprobarFavoritos(String idPost, String idUser, final PostsAdapter2.ViewHolder holder) {
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


    private void getUsuarioInfo(String idUser, final PostsAdapter2.ViewHolder holder) {
        usuariosBBDDFirebase.getUsuarios(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        String nUsuario = documentSnapshot.getString("usuario");
                        holder.nombreUsuario.setText("@" + nUsuario.toUpperCase());
                    }
                }

                if (documentSnapshot.contains("media")) {
                    Double mediaDouble = documentSnapshot.getDouble("media");
                    if (mediaDouble != null) {
                        float media = mediaDouble.floatValue();
                        holder.mediaUsuario.setRating(media);
                    }
                }
            }
        });
    }

    private void borrarPublicacion(String id) {
        publicacionFirebase.borrarPublicacion(id).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "La publicación se eliminó correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No se pudo eliminar la publicación", Toast.LENGTH_SHORT).show();
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
        TextView textViewTitulo, nombreUsuario, textViewTipoContrato, txtFavoritos, textViewSector;

        ImageView imageViewPost, imgFavoritos;
        View viewHolder;
        ImageButton btnCerrar;
        RatingBar mediaUsuario;

        public ViewHolder(View view) {
            super(view);
            textViewTitulo = view.findViewById(R.id.textViewTituloPostCard);
            textViewTipoContrato = view.findViewById(R.id.textViewTipoContrato);
            textViewSector = view.findViewById(R.id.textViewSector);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            nombreUsuario = view.findViewById(R.id.nombreUsuario);
            btnCerrar = view.findViewById(R.id.btnBorrarNoticia);
            txtFavoritos = view.findViewById(R.id.txtFavoritos);
            imgFavoritos = view.findViewById(R.id.imgFavoritos);
            mediaUsuario = view.findViewById(R.id.mediaUsuarios);
            viewHolder = view;
        }

    }

    public void mostrarAlertBorrarPublicacion(String idPublicacion) {
        // con este tema personalizado evitamos los bordes por defecto
        customDialog = new Dialog(context, R.style.Theme_Translucent);
        //deshabilitamos el título por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setCancelable(false);
        //establecemos el contenido de nuestro dialog
        customDialog.setContentView(R.layout.alert_dialog_cerrar_sesion);

        TextView titulo = (TextView) customDialog.findViewById(R.id.titulo);
        titulo.setText("Borrar Publicación");

        TextView contenido = (TextView) customDialog.findViewById(R.id.contenido);
        contenido.setText("Estas seguro que quieres borrar esta Publicación permanentemente");

        (customDialog.findViewById(R.id.aceptar)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                borrarPublicacion(idPublicacion);
                customDialog.dismiss();

            }
        });

        (customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }


}
