

package com.example.testmenu.fragmentMenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.testmenu.R;
import com.example.testmenu.activities.PostActivity;
import com.example.testmenu.adapters.PostsAdapter;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

public class InicioFragment extends Fragment {

    private View mView;
    private FloatingActionButton mFab;
    private AutentificacioFirebase mAutentificacionFirebase;
    private RecyclerView mRecyclerView;
    private PublicacionFirebase mPublicacionfirebase;

    private SearchView mSearchView;
    private PostsAdapter mPostsAdapter;
    private PostsAdapter mPostsAdapterBuscar;

    public InicioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_inicio, container, false);
        mFab = mView.findViewById(R.id.fab);
        mRecyclerView = mView.findViewById(R.id.recyclerViewInicio);
        mSearchView = mView.findViewById(R.id.buscarInicio);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAutentificacionFirebase = new AutentificacioFirebase();
        mPublicacionfirebase = new PublicacionFirebase();

        mFab.setOnClickListener(view -> goToPost());

        busquedasDiferentes();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPublicacionfirebase.getAll();
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query, Publicacion.class)
                .build();

        mPostsAdapter = new PostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
        if (mPostsAdapterBuscar != null) {
            mPostsAdapterBuscar.stopListening();
        }
    }

    public void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    /**
     * Configura un listener de texto de búsqueda para el objeto SearchView.
     * <p>
     * Establece el comportamiento para cuando se envía una consulta de búsqueda (onQueryTextSubmit)
     * y cuando el texto de búsqueda cambia (onQueryTextChange).
     *
     * @return void
     */
    public void busquedasDiferentes() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscar(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscar(newText);// Handle search text change if needed
                return false;
            }
        });
    }

    /**
     * Realiza una búsqueda utilizando el texto de consulta proporcionado.
     * <p>
     * Se obtiene una consulta de búsqueda utilizando el método <b>getPostByTitulo()</b> de mPublicacionfirebase.
     * Se construyen las opciones de FirestoreRecyclerOptions con la consulta de búsqueda y la clase Publicacion.
     * Se crea un nuevo PostsAdapter con las opciones y el contexto actual.
     * Se establece el adaptador en el RecyclerView y se inicia la escucha del adaptador.
     *
     * @param query El texto de búsqueda utilizado para buscar publicaciones.
     */
    public void buscar(String query) {
        Query searchQuery = mPublicacionfirebase.getPostByTitulo(query);
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(searchQuery, Publicacion.class)
                .build();

        mPostsAdapterBuscar = new PostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mPostsAdapterBuscar);
        mPostsAdapterBuscar.startListening();
    }
}