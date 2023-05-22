

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

    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    private void busquedasDiferentes() {
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

    private void buscar(String query) {
        Query searchQuery = mPublicacionfirebase.getPostByTitulo(query);
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(searchQuery, Publicacion.class)
                .build();

        mPostsAdapterBuscar = new PostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mPostsAdapterBuscar);
        mPostsAdapterBuscar.startListening();
    }
}