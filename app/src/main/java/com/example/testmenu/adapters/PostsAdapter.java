package com.example.testmenu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmenu.R;
import com.example.testmenu.activities.PostDetailActivity;
import com.example.testmenu.activities.SectoresActivity;
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

public class PostsAdapter extends FirestoreRecyclerAdapter<Publicacion, PostsAdapter.ViewHolder> {

    Context context;
    AutentificacioFirebase autentificacioFirebase;
    PublicacionFirebase publicacionFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;

    FavoritosFirebase favoritosFirebase;

    ListenerRegistration mListener;


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
        holder.textViewSector.setText("Sector: "+publicacion.getSector());
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

    /**
     * Obtiene el número de "Me gusta" para una publicación específica de la base de datos de Firebase y actualiza
     * el campo de texto correspondiente en el ViewHolder con el número de "Me gusta".
     *
     * @param idPost  el ID de la publicación para la que se desea obtener el número de "Me gusta"
     * @param holder  el ViewHolder que contiene el campo de texto donde se mostrará el número de "Me gusta"
     * @return void
     */
    private void getNumeroDeLikes(String idPost, final ViewHolder holder) {
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

    /**
     * Realiza la acción de dar o quitar un "me gusta" a una publicación.
     *
     * @param favoritos representa el "me gusta" que se quiere dar/quitar.
     * @param holder objeto de la clase ViewHolder que contiene la vista del elemento de la lista de publicaciones.
     */
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

    /**
     * Comprueba si un post ha sido marcado como favorito por el usuario actual y actualiza la imagen de la vista correspondiente.
     *
     * @param idPost el ID del post que se quiere comprobar.
     * @param idUser el ID del usuario que se quiere comprobar si ha marcado el post como favorito.
     * @param holder el ViewHolder que contiene la imagen del botón de favoritos.
     * @return void
     */
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

    /**
     * Se muestra el nombre del usuario deseado.
     *
     * @param idUser id del usuario que se desea obtener
     * @param holder el ViewHolder que contiene el textview para mostrar el usuario
     * @return void
     */
    private void getUsuarioInfo(String idUser, final ViewHolder holder) {
        usuariosBBDDFirebase.getUsuarios(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        String nUsuario = documentSnapshot.getString("usuario");
                        holder.nombreUsuario.setText("@" + nUsuario.toUpperCase());
                    }
                    if (documentSnapshot.contains("media")) {
                        Double mediaDouble = documentSnapshot.getDouble("media");
                        if (mediaDouble != null) {
                            float media = mediaDouble.floatValue();
                            holder.mediaUsuario.setRating(media);
                        }
                    }

                }
            }
        });
    }


    public ListenerRegistration getListener() {
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview_post, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo, nombreUsuario, textViewTipoContrato,textViewSector, fechaPublicacion, txtFavoritos;
        ImageView imageViewPost, imgFavoritos;
        RatingBar mediaUsuario;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewTitulo = view.findViewById(R.id.textViewTituloPostCard);
            textViewTipoContrato = view.findViewById(R.id.textViewTipoContrato);
            textViewSector = view.findViewById(R.id.textViewSector);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            nombreUsuario = view.findViewById(R.id.nombreUsuario);
            // fechaPublicacion = view.findViewById(R.id.fechaPublicacion);
            txtFavoritos = view.findViewById(R.id.txtFavoritos);
            imgFavoritos = view.findViewById(R.id.imgFavoritos);
            mediaUsuario = view.findViewById(R.id.mediaUsuarios);
            viewHolder = view;
        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        SectoresActivity.vacio(); // Llamar al método vacio() después de que los datos se hayan cargado en el adaptador
    }


}
