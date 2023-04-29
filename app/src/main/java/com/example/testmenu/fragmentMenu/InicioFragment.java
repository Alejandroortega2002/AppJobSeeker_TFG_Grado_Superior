package com.example.testmenu.fragmentMenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.testmenu.R;
import com.example.testmenu.activities.LoginActivity;
import com.example.testmenu.activities.MainActivity;
import com.example.testmenu.activities.PostActivity;
import com.example.testmenu.adapters.PostsAdapter;
import com.example.testmenu.entidades.Publicacion;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.PublicacionFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;


public class InicioFragment extends Fragment {

    View mView;
    FloatingActionButton mFab;
    Toolbar mToolbar;
    AutentificacioFirebase mAutentificacionFirebase;
    RecyclerView mRecyclerView;
    PublicacionFirebase mPublicacionfirebase;
    PostsAdapter mPostsAdapter;

    public InicioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_inicio, container, false);
        mFab = mView.findViewById(R.id.fab);
        mToolbar = mView.findViewById(R.id.toolbar);
        mRecyclerView = mView.findViewById(R.id.recyclerViewInicio);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Publicaciones");
        setHasOptionsMenu(true);
        mAutentificacionFirebase = new AutentificacioFirebase();
        mPublicacionfirebase = new PublicacionFirebase();

        mFab.setOnClickListener(view -> goToPost());
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
    }


    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemLogout) {
            logout();
        }

        return true;
    }

    private void logout() {
        mAutentificacionFirebase.logout();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
