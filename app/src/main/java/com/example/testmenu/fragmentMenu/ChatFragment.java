package com.example.testmenu.fragmentMenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmenu.R;
import com.example.testmenu.adapters.ChatsAdapter;
import com.example.testmenu.databinding.FragmentChatBinding;
import com.example.testmenu.entidades.Chat;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.ChatsFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

    private ChatsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private AutentificacioFirebase mAuthFirebase;
    private View mView;
    private ChatsFirebase mChatsFirebase;

    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerView = mView.findViewById(R.id.recyclerViewChats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mChatsFirebase = new ChatsFirebase();
        mAuthFirebase = new AutentificacioFirebase();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart(); // Llamada al método onStart() de la clase base

        // Obtener todos los chats del usuario actual
        Query query = mChatsFirebase.getAll(mAuthFirebase.getUid());

        // Configurar las opciones del adaptador de recycler view utilizando FirestoreRecyclerOptions
        FirestoreRecyclerOptions<Chat> options =
                new FirestoreRecyclerOptions.Builder<Chat>()
                        .setQuery(query, Chat.class) // Establecer la consulta y el modelo de datos
                        .build();

        // Crear una instancia del adaptador de chats con las opciones y el contexto actual
        mAdapter = new ChatsAdapter(options, getContext());

        // Establecer el adaptador en el recycler view
        mRecyclerView.setAdapter(mAdapter);

        // Iniciar la escucha de cambios en el adaptador
        mAdapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter.getListener() != null) {
            mAdapter.getListener().remove();
        }

        if (mAdapter.getmListenerLastMessage() != null) {
            mAdapter.getmListenerLastMessage().remove();
        }
    }
}