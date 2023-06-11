package com.example.testmenu.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmenu.R;
import com.example.testmenu.activities.ChatActivity;
import com.example.testmenu.entidades.Mensaje;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajeAdapter extends FirestoreRecyclerAdapter<Mensaje, MensajeAdapter.ViewHolder> {

    private Context context;
    private UsuariosBBDDFirebase mUsersProvider;
    private AutentificacioFirebase mAuthProvider;

    public MensajeAdapter(FirestoreRecyclerOptions<Mensaje> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsuariosBBDDFirebase();
        mAuthProvider = new AutentificacioFirebase();
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Mensaje mensaje) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String mensajeId = document.getId();

        // Se establece el texto del mensaje en el TextView correspondiente del ViewHolder
        holder.textViewMensaje.setText(mensaje.getMessage());

        // Se formatea y se establece la fecha relativa del mensaje en el TextView correspondiente del ViewHolder
        String relativeTime = RelativeTime.timeFormatAMPM(mensaje.getTimestamp(), context);
        holder.textViewDate.setText(relativeTime);

        // Se verifica si el mensaje fue enviado por el usuario actual o por otro usuario
        if (mensaje.getIdSender().equals(mAuthProvider.getUid())) {
            // El mensaje fue enviado por el usuario actual

            // Se establecen los parámetros de diseño para alinear el mensaje a la derecha
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150, 0, 0, 0);
            holder.linearLayoutMensaje.setLayoutParams(params);

            // Se establece el padding y el fondo del LinearLayout correspondiente del ViewHolder
            holder.linearLayoutMensaje.setPadding(30, 20, 0, 20);
            holder.linearLayoutMensaje.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout));

            // Se muestra el icono de visto en el mensaje
            holder.imageViewViewed.setVisibility(View.VISIBLE);

            // Se establecen los colores del texto del mensaje y la fecha
            holder.textViewMensaje.setTextColor(Color.WHITE);
            holder.textViewDate.setTextColor(Color.LTGRAY);
        } else {
            // El mensaje fue enviado por otro usuario

            // Se establecen los parámetros de diseño para alinear el mensaje a la izquierda
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0, 0, 150, 0);
            holder.linearLayoutMensaje.setLayoutParams(params);

            // Se establece el padding y el fondo del LinearLayout correspondiente del ViewHolder
            holder.linearLayoutMensaje.setPadding(30, 20, 30, 20);
            holder.linearLayoutMensaje.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout_gray));

            // Se oculta el icono de visto en el mensaje
            holder.imageViewViewed.setVisibility(View.GONE);

            // Se establecen los colores del texto del mensaje y la fecha
            holder.textViewMensaje.setTextColor(Color.DKGRAY);
            holder.textViewDate.setTextColor(Color.LTGRAY);
        }

        // Se verifica si el mensaje ha sido visto o no, y se establece el ícono correspondiente
        if (mensaje.isViewed()) {
            holder.imageViewViewed.setImageResource(R.drawable.icon_check_blue);
        } else {
            holder.imageViewViewed.setImageResource(R.drawable.icon_check_gray);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mensaje, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMensaje;
        private TextView textViewDate;
        private ImageView imageViewViewed;
        private LinearLayout linearLayoutMensaje;
        private View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewMensaje = view.findViewById(R.id.textViewMensaje);
            textViewDate = view.findViewById(R.id.textViewDateMensaje);
            linearLayoutMensaje = view.findViewById(R.id.linearLayoutMensaje);
            imageViewViewed = view.findViewById(R.id.imageViewedMensaje);

            viewHolder = view;
        }
    }

}
