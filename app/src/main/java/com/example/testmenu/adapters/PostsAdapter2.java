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

    private Context context;
    private AutentificacioFirebase autentificacioFirebase;
    private PublicacionFirebase publicacionFirebase;

    private UsuariosBBDDFirebase usuariosBBDDFirebase;
    private FavoritosFirebase favoritosFirebase;

    private Dialog customDialog;

    private ListenerRegistration mListener;


    public PostsAdapter2(FirestoreRecyclerOptions<Publicacion> options, Context contexto) {
        super(options);
        this.context = contexto;
        autentificacioFirebase = new AutentificacioFirebase();
        publicacionFirebase = new PublicacionFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        favoritosFirebase = new FavoritosFirebase();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Publicacion publicacion) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        // Configura los valores de la publicación en el ViewHolder
        holder.textViewTitulo.setText(publicacion.getTitulo().toUpperCase());
        holder.textViewTipoContrato.setText("Contrato: " + publicacion.getCategoria());
        holder.textViewSector.setText("Sector: " + publicacion.getSector());

        // Verifica si el ID del usuario de la publicación coincide con el ID del usuario autenticado
        if (publicacion.getIdUser().equals(autentificacioFirebase.getUid())) {
            // Si es así, muestra el botón de cerrar
            holder.btnCerrar.setVisibility(View.VISIBLE);
        } else {
            // Si no, oculta el botón de cerrar
            holder.btnCerrar.setVisibility(View.GONE);
        }

        // Carga la imagen de la publicación en el ImageView utilizando Picasso
        if (publicacion.getImage1() != null) {
            if (!publicacion.getImage1().isEmpty()) {
                Picasso.get().load(publicacion.getImage1()).into(holder.imageViewPost);
            }
        }

        // Configura el listener de clic en el ViewHolder para abrir los detalles de la publicación
        holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("id", postId);
            context.startActivity(intent);
        });

        // Configura el listener de clic en el botón de cerrar para mostrar el diálogo de confirmación de borrado
        holder.btnCerrar.setOnClickListener(view -> {
            mostrarAlertBorrarPublicacion(postId);
        });

        // Configura el listener de clic en el botón de favoritos para agregar o eliminar la publicación de favoritos
        holder.imgFavoritos.setOnClickListener(view -> {
            Favoritos favoritos = new Favoritos();
            favoritos.setIdUser(autentificacioFirebase.getUid());
            favoritos.setIdPost(postId);
            favoritos.setTimestamp(new Date().getTime());
            favoritos(favoritos, holder);
        });

        // Obtiene la información del usuario de la publicación y actualiza los elementos visuales correspondientes
        getUsuarioInfo(publicacion.getIdUser(), holder);

        // Obtiene el número de likes de la publicación y actualiza el contador correspondiente
        getNumeroDeLikes(postId, holder);

        // Verifica si la publicación está en favoritos del usuario y actualiza el icono de favoritos correspondiente
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
    public void getNumeroDeLikes(String idPost, final PostsAdapter2.ViewHolder holder) {
        // Crea un oyente de cambios en la colección de "likes" para la publicación específica
        mListener = favoritosFirebase.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // Verifica si hay instantáneas de documentos disponibles
                if (queryDocumentSnapshots != null) {
                    // Obtiene el número de "likes" contando las instantáneas de documentos
                    int numberLikes = queryDocumentSnapshots.size();
                    // Actualiza el texto del contador de "likes" en el ViewHolder
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
    public void favoritos(final Favoritos favoritos, final PostsAdapter2.ViewHolder holder) {
        // Obtiene el documento de "like" por publicación y usuario
        favoritosFirebase.getLikeByPostAndUser(favoritos.getIdPost(), autentificacioFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Obtiene el número de documentos de "like" encontrados
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    // Si hay al menos un documento de "like", obtiene el ID del primer documento
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    // Actualiza la imagen del botón de "me gusta" en el ViewHolder para mostrar como sin marcar
                    holder.imgFavoritos.setImageResource(R.drawable.ic_me_gusta_sin_marcar);
                    // Elimina el documento de "like" utilizando su ID
                    favoritosFirebase.delete(idLike);
                } else {
                    // Si no hay documentos de "like", actualiza la imagen del botón de "me gusta" en el ViewHolder para mostrar como marcado
                    holder.imgFavoritos.setImageResource(R.drawable.ic_me_gusta_marcado);
                    // Crea un nuevo documento de "like" con la información de Favoritos
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
    public void checkComprobarFavoritos(String idPost, String idUser, final PostsAdapter2.ViewHolder holder) {
        // Obtiene el documento de "like" por publicación y usuario
        favoritosFirebase.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Obtiene el número de documentos de "like" encontrados
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    // Si hay al menos un documento de "like", actualiza la imagen del botón de "me gusta" en el ViewHolder para mostrar como marcado
                    holder.imgFavoritos.setImageResource(R.drawable.ic_me_gusta_marcado);
                } else {
                    // Si no hay documentos de "like", actualiza la imagen del botón de "me gusta" en el ViewHolder para mostrar como sin marcar
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
    public void getUsuarioInfo(String idUser, final PostsAdapter2.ViewHolder holder) {
        // Obtiene la información del usuario mediante el ID de usuario
        usuariosBBDDFirebase.getUsuarios(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Verifica si el documento existe
                if (documentSnapshot.exists()) {
                    // Verifica si el documento contiene el campo "usuario"
                    if (documentSnapshot.contains("usuario")) {
                        // Obtiene el valor del campo "usuario"
                        String nUsuario = documentSnapshot.getString("usuario");
                        // Actualiza el TextView del nombre de usuario en el ViewHolder
                        holder.nombreUsuario.setText("@" + nUsuario.toUpperCase());
                    }
                }

                // Verifica si el documento contiene el campo "media"
                if (documentSnapshot.contains("media")) {
                    // Obtiene el valor del campo "media" como un Double
                    Double mediaDouble = documentSnapshot.getDouble("media");
                    if (mediaDouble != null) {
                        // Convierte el Double a un float y lo asigna a la calificación del usuario en el ViewHolder
                        float media = mediaDouble.floatValue();
                        holder.mediaUsuario.setRating(media);
                    }
                }
            }
        });
    }

    /**
     * Se borra la oferta cuyo id equivalga a la del parámetro
     * @param id id de publicación
     * @return void
     */
    public void borrarPublicacion(String id) {
        // Elimina los favoritos asociados a la publicación mediante su ID
        favoritosFirebase.deleteFavoritesByPost(id).addOnCompleteListener(task -> {
            // Borra la publicación mediante su ID
            publicacionFirebase.borrarPublicacion(id).addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    // Si se completó exitosamente, muestra un mensaje de éxito
                    Toast.makeText(context, "La publicación se eliminó correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    // Si no se pudo completar, muestra un mensaje de error
                    Toast.makeText(context, "No se pudo eliminar la publicación", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview_post, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitulo, nombreUsuario, textViewTipoContrato, txtFavoritos, textViewSector;

        private ImageView imageViewPost, imgFavoritos;
        private View viewHolder;
        private ImageButton btnCerrar;
        private RatingBar mediaUsuario;

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

    /**
     * Muestra un Dialog de aviso al usuario.
     * <p>
     * Si se desea borrar la oferta se eliminará de la base de datos
     *
     * @param idPublicacion id de la publicación en caso de querer borrar la oferta
     * @return void
     */
    public void mostrarAlertBorrarPublicacion(String idPublicacion) {
        // Crear un Dialog personalizado con un tema translúcido
        customDialog = new Dialog(context, R.style.Theme_Translucent);
        // Deshabilitar el título del Dialog por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Obligar al usuario a pulsar los botones para cerrarlo
        customDialog.setCancelable(false);
        // Establecer el contenido de nuestro Dialog mediante el layout alert_dialog_cerrar_sesion
        customDialog.setContentView(R.layout.alert_dialog_cerrar_sesion);

        // Obtener referencias a los elementos del layout
        TextView titulo = (TextView) customDialog.findViewById(R.id.titulo);
        TextView contenido = (TextView) customDialog.findViewById(R.id.contenido);

        // Establecer el texto del título y el contenido del cuadro de diálogo
        titulo.setText("Borrar Publicación");
        contenido.setText("Estás seguro que quieres borrar esta Publicación permanentemente");

        // Configurar el OnClickListener del botón "Aceptar"
        (customDialog.findViewById(R.id.aceptar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llamar al método borrarPublicacion para eliminar la publicación
                borrarPublicacion(idPublicacion);
                // Cerrar el cuadro de diálogo
                customDialog.dismiss();
            }
        });

        // Configurar el OnClickListener del botón "Cancelar"
        (customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cerrar el cuadro de diálogo sin realizar ninguna acción
                customDialog.dismiss();
            }
        });

        // Mostrar el cuadro de diálogo personalizado
        customDialog.show();
    }


}
