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
import com.example.testmenu.activities.MisFavoritosActivity;
import com.example.testmenu.activities.MisOfertasActivity;
import com.example.testmenu.activities.PostDetailActivity;
import com.example.testmenu.activities.SectoresActivity;
import com.example.testmenu.activities.ValoracionActivity;
import com.example.testmenu.entidades.Favoritos;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.FavoritosFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.fragmentMenu.ChatFragment;
import com.example.testmenu.fragmentMenu.InicioFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Publicacion, PostsAdapter.ViewHolder> {

   private Context context;
   private AutentificacioFirebase autentificacioFirebase;
   private PublicacionFirebase publicacionFirebase;
   private UsuariosBBDDFirebase usuariosBBDDFirebase;
   private FavoritosFirebase favoritosFirebase;
   private ListenerRegistration mListener;


    public PostsAdapter(FirestoreRecyclerOptions<Publicacion> options, Context contexto) {
        super(options);
        this.context = contexto;
        autentificacioFirebase = new AutentificacioFirebase();
        publicacionFirebase = new PublicacionFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        favoritosFirebase = new FavoritosFirebase();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Publicacion publicacion) {
        // Obtiene el DocumentSnapshot de la posición actual
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postId = document.getId();

        // Configura los valores en el ViewHolder
        holder.textViewTitulo.setText(publicacion.getTitulo().toUpperCase());
        holder.textViewTipoContrato.setText("Contrato: " + publicacion.getCategoria());
        holder.textViewSector.setText("Sector: " + publicacion.getSector());
        // holder.fechaPublicacion.setText((int) publicacion.getTimeStamp());

        // Carga la imagen de la publicación en el ImageView si existe
        if (publicacion.getImage1() != null) {
            if (!publicacion.getImage1().isEmpty()) {
                Picasso.get().load(publicacion.getImage1()).into(holder.imageViewPost);
            }
        }

        // Configura un OnClickListener para el ViewHolder que abre la actividad de detalle de la publicación
        holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("id", postId);
            context.startActivity(intent);
        });

        // Configura un OnClickListener para el botón de favoritos en el ViewHolder
        holder.imgFavoritos.setOnClickListener(view -> {
            // Crea un objeto Favoritos con los datos necesarios
            Favoritos favoritos = new Favoritos();
            favoritos.setIdUser(autentificacioFirebase.getUid());
            favoritos.setIdPost(postId);
            favoritos.setTimestamp(new Date().getTime());

            // Llama al método favoritos para agregar o eliminar el favorito
            favoritos(favoritos, holder);
        });

        // Obtiene la información del usuario y configura los datos en el ViewHolder
        getUsuarioInfo(publicacion.getIdUser(), holder);

        // Obtiene el número de likes y configura el texto en el ViewHolder
        getNumeroDeLikes(postId, holder);

        // Verifica si la publicación es un favorito del usuario actual y configura el estado en el ViewHolder
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
    public void getNumeroDeLikes(String idPost, ViewHolder holder) {
        // Se establece un listener para recibir actualizaciones en tiempo real de los likes
        mListener = favoritosFirebase.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // Cuando se recibe una actualización de los likes
                if (queryDocumentSnapshots != null) {
                    // Se obtiene el número de likes contando los documentos en el resultado de la consulta
                    int numberLikes = queryDocumentSnapshots.size();
                    // Se actualiza el texto del TextView en el ViewHolder con el número de likes
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
    public void favoritos(final Favoritos favoritos, final ViewHolder holder) {
        // Obtén el like por publicación y usuario actual
        favoritosFirebase.getLikeByPostAndUser(favoritos.getIdPost(), autentificacioFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Obtiene el número de documentos en la consulta
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    // Si hay documentos, obtén el ID del primer documento y realiza la eliminación del like
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imgFavoritos.setImageResource(R.drawable.ic_me_gusta_sin_marcar);
                    favoritosFirebase.delete(idLike);
                } else {
                    // Si no hay documentos, realiza la creación del like y actualiza el estado del botón de favoritos
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
    public void checkComprobarFavoritos(String idPost, String idUser, final ViewHolder holder) {
        // Obtén el like por publicación y usuario actual
        favoritosFirebase.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Obtiene el número de documentos en la consulta
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    // Si hay documentos, actualiza el estado del botón de favoritos como marcado
                    holder.imgFavoritos.setImageResource(R.drawable.ic_me_gusta_marcado);
                } else {
                    // Si no hay documentos, actualiza el estado del botón de favoritos como sin marcar
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
    public void getUsuarioInfo(String idUser, final ViewHolder holder) {
        // Obtén la información del usuario por su ID
        usuariosBBDDFirebase.getUsuarios(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Verifica si el documento existe
                    if (documentSnapshot.contains("usuario")) {
                        // Obtiene el nombre de usuario del documento
                        String nUsuario = documentSnapshot.getString("usuario");
                        // Configura el nombre de usuario en el holder
                        holder.nombreUsuario.setText("@" + nUsuario.toUpperCase());
                    }
                    if (documentSnapshot.contains("media")) {
                        // Obtiene el valor de la media del documento
                        Double mediaDouble = documentSnapshot.getDouble("media");
                        if (mediaDouble != null) {
                            // Convierte el valor de la media a float
                            float media = mediaDouble.floatValue();
                            // Configura la media en el holder
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
            txtFavoritos = view.findViewById(R.id.txtFavoritos);
            imgFavoritos = view.findViewById(R.id.imgFavoritos);
            mediaUsuario = view.findViewById(R.id.mediaUsuarios);
            viewHolder = view;
        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        // Llamar al método vacio() después de que los datos se hayan cargado en el adaptador
        SectoresActivity.vacio();
        InicioFragment.vacio();
    }


}
