package com.example.testmenu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Valoraciones;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ValoracionesAdapter extends FirestoreRecyclerAdapter<Valoraciones, ValoracionesAdapter.HolderValoraciones> {

    private Context context;
    private List<Valoraciones> mValoraciones;
    private UsuariosBBDDFirebase usuariosBBDDFirebase;

    private AutentificacioFirebase autentificacioFirebase;

    public ValoracionesAdapter(FirestoreRecyclerOptions<Valoraciones> options, Context context) {
        super(options);
        this.context = context;
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        autentificacioFirebase = new AutentificacioFirebase();
    }


    @Override
    public HolderValoraciones onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout carview_valoracion en una nueva vista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview_valoracion, parent, false);
        // Devolver una nueva instancia de HolderValoraciones y asignar la vista inflada
        return new HolderValoraciones(view);
    }


    @Override
    public void onBindViewHolder(@NonNull HolderValoraciones holder, int position, @NonNull Valoraciones valoraciones) {
        // Obtener el documento en la posición especificada
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        // Obtener el contenido de la valoración y asignarlo al TextView en el HolderValoraciones
        holder.contenido.setText(valoraciones.getValoracion());
        // Obtener la fecha de la valoración y formatearla
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(valoraciones.getTimeStamp()));
        String dateFormat = DateFormat.getDateInstance().format(calendar.getTime());
        // Obtener el userId del documento
        String userId = document.getString("userPostId");
        // Asignar la calificación y la fecha al HolderValoraciones
        holder.estrellas.setRating(Float.parseFloat(valoraciones.getNota()));
        holder.fecha.setText(dateFormat);
        // Cargar los detalles del usuario correspondiente al userId en el HolderValoraciones
        cargarDetallesUsuario(userId, holder);
    }

    /**
     * Carga el contenido de los usuarios y sus valoraciones desde Firebase
     * <p>
     * @param userId id del user al que se desea sacar la info
     * @param holder el viewholder en donde mostrar los datos de la base de datos
     * @return void
     */
    public void cargarDetallesUsuario(String userId, final HolderValoraciones holder) {
        if (userId != null) {
            usuariosBBDDFirebase.getUsuarios(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Obtener el nombre de usuario del documento y asignarlo al TextView en el HolderValoraciones
                        String nombreUsuario = documentSnapshot.getString("usuario");
                        if (nombreUsuario != null) {
                            holder.nombreUsuario.setText("@" + nombreUsuario.toUpperCase());
                        }
                        // Obtener la URL de la foto de perfil del documento y cargarla en el ImageView en el HolderValoraciones
                        String fotoPerfil = documentSnapshot.getString("fotoPerfil");
                        if (fotoPerfil != null) {
                            // Cargar la imagen de perfil del usuario usando la biblioteca Picasso.
                            Picasso.get().load(fotoPerfil).into(holder.fotoUsuario);
                        }
                    }
                }
            });
        }
    }



    public static class HolderValoraciones extends RecyclerView.ViewHolder {
        private CircleImageView fotoUsuario;
        private TextView nombreUsuario, fecha, contenido;
        private RatingBar estrellas;

        public HolderValoraciones(View itemView) {
            super(itemView);
            fotoUsuario = itemView.findViewById(R.id.fotoUsuarioCard);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuarioCard);
            fecha = itemView.findViewById(R.id.fechaCard);
            contenido = itemView.findViewById(R.id.contenidoValoracion);
            estrellas = itemView.findViewById(R.id.estrellasCard);
        }
    }
}
