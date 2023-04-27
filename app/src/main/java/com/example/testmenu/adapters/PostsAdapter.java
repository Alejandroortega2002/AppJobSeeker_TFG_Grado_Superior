package com.example.testmenu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Publicacion;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class PostsAdapter extends FirestoreRecyclerAdapter<Publicacion, PostsAdapter.ViewHolder> {

Context contexto;
public PostsAdapter(FirestoreRecyclerOptions<Publicacion>options, Context contexto){
    super(options);
    this.contexto = contexto;
}
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Publicacion publicacion) {
        holder.textViewTitulo.setText(publicacion.getTitulo());
        holder.textViewDescripcion.setText(publicacion.getDescripcion());
        if(publicacion.getImage1()!=null){
            if(!publicacion.getImage1().isEmpty()){
                Picasso.get().load(publicacion.getImage1()).into(holder.imageViewPost);

            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview_post,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitulo;
        TextView textViewDescripcion;
        ImageView imageViewPost;

        public ViewHolder(View view){
            super(view);
            textViewTitulo = view.findViewById(R.id.textViewTituloPostCard);
            textViewDescripcion = view.findViewById(R.id.textViewDescripcionPostCard);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
        }

    }
}
