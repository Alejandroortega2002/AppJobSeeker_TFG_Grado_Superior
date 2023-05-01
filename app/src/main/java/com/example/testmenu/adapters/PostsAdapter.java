package com.example.testmenu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmenu.R;
import com.example.testmenu.activities.PostDetailActivity;
import com.example.testmenu.entidades.Publicacion;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class PostsAdapter extends FirestoreRecyclerAdapter<Publicacion, PostsAdapter.ViewHolder> {

Context context;
public PostsAdapter(FirestoreRecyclerOptions<Publicacion>options, Context contexto){
    super(options);
    this.context = contexto;
}
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Publicacion publicacion) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        holder.textViewTitulo.setText(publicacion.getTitulo());
        holder.textViewDescripcion.setText(publicacion.getDescripcion());
        if(publicacion.getImage1()!=null){
            if(!publicacion.getImage1().isEmpty()){
                Picasso.get().load(publicacion.getImage1()).into(holder.imageViewPost);

            }
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(context, PostDetailActivity.class);
                intent.putExtra("id",postId);
                context.startActivity(intent);
            }
        });
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

        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewTitulo = view.findViewById(R.id.textViewTituloPostCard);
            textViewDescripcion = view.findViewById(R.id.textViewDescripcionPostCard);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            viewHolder = view;
        }

    }
}
