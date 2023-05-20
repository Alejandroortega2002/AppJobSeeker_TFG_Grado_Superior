package com.example.testmenu.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.adapters.MensajeAdapter;
import com.example.testmenu.entidades.Chat;
import com.example.testmenu.entidades.Mensaje;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.ChatsFirebase;
import com.example.testmenu.firebase.MensajeFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.utils.RelativeTime;
import com.example.testmenu.utils.ViewedMensajeHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;
    ChatsFirebase mChatsFirebase;
    MensajeFirebase mMensajeFirebase;
    AutentificacioFirebase mAuthFirebase;
    UsuariosBBDDFirebase mUsuarioFirebase;

    EditText mEditTextMensaje;
    ImageView mImageViewSendMensaje;

    CircleImageView mCircleImageProfile;
    TextView mTextViewUsername;
    TextView mTextViewRelativeTime;
    ImageView mImageViewBack;
    RecyclerView mRecyclerViewMensaje;
    MensajeAdapter mAdapter;
    View mActionBarView;

    LinearLayoutManager mLinearLayoutManager;
    ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatsFirebase = new ChatsFirebase();
        mMensajeFirebase = new MensajeFirebase();
        mAuthFirebase = new AutentificacioFirebase();
        mUsuarioFirebase = new UsuariosBBDDFirebase();

        mEditTextMensaje = findViewById(R.id.editTextMensaje);
        mImageViewSendMensaje = findViewById(R.id.imageViewSendMensaje);
        mRecyclerViewMensaje = findViewById(R.id.recyclerViewMessage);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMensaje.setLayoutManager(mLinearLayoutManager);

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        showCustomToolbar(R.layout.custom_chat_toolbar);


        mImageViewSendMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMensaje();
            }
        });


        checkIfChatExist();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter !=null){
            mAdapter.startListening();
        }
        ViewedMensajeHelper.updateOnline(true, ChatActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.stopListening();
        ViewedMensajeHelper.updateOnline(false, ChatActivity.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener !=null){
            mListener.remove();
        }
    }

    private void getMensajeChat() {
        Query query = mMensajeFirebase.getMensajeByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Mensaje> options =
                new FirestoreRecyclerOptions.Builder<Mensaje>()
                        .setQuery(query, Mensaje.class)
                        .build();
        mAdapter = new MensajeAdapter(options, ChatActivity.this);
        mRecyclerViewMensaje.setAdapter(mAdapter);
        mAdapter.startListening();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int numMensajes = mAdapter.getItemCount();
                int lastMensajePosicion = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastMensajePosicion == -1 || (positionStart >= (numMensajes - 1) && lastMensajePosicion == (positionStart - 1))) {
                    mRecyclerViewMensaje.scrollToPosition(positionStart);
                }
            }
        });
    }


    private void sendMensaje() {
        String textMensaje = mEditTextMensaje.getText().toString();
        if (!textMensaje.isEmpty()) {
            Mensaje mensaje = new Mensaje();
            mensaje.setIdChat(mExtraIdChat);
            if (mAuthFirebase.getUid().equals(mExtraIdUser1)) {
                mensaje.setIdSender(mExtraIdUser1);
                mensaje.setIdReceiver(mExtraIdUser2);
            } else {
                mensaje.setIdSender(mExtraIdUser2);
                mensaje.setIdReceiver(mExtraIdUser1);
            }
            mensaje.setTimestamp(new Date().getTime());
            mensaje.setViewed(false);
            mensaje.setMessage(textMensaje);

            MensajeFirebase mensajeFirebase = new MensajeFirebase();
            mensajeFirebase.create(mensaje)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mEditTextMensaje.setText("");
                            int position = mAdapter.getItemCount(); // Obtener la posición del nuevo elemento
                            mAdapter.notifyDataSetChanged(); // Actualizar la lista de mensajes en la interfaz de usuario
                        } else {
                            Toast.makeText(ChatActivity.this, "El mensaje no se pudo crear", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("");
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionbar.setCustomView(mActionBarView);

        mCircleImageProfile = mActionBarView.findViewById(R.id.circleImageProfile);
        mTextViewUsername = mActionBarView.findViewById(R.id.textViewUsername);
        mTextViewRelativeTime = mActionBarView.findViewById(R.id.textViewRelativeTime);
        mImageViewBack = mActionBarView.findViewById(R.id.imageViewBack);

        mImageViewBack.setOnClickListener(v -> finish());

        getUserInfo();


    }

    private void getUserInfo() {
        String idUserInfo = "";
        if (mAuthFirebase.getUid().equals(mExtraIdUser1)) {
            idUserInfo = mExtraIdUser2;
        } else {
            idUserInfo = mExtraIdUser1;
        }
       mListener = mUsuarioFirebase.getUsuariosRealTime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        String username = documentSnapshot.getString("usuario");
                        mTextViewUsername.setText(username);
                    }
                    if (documentSnapshot.contains("online")) {
                        boolean online = Boolean.TRUE.equals(documentSnapshot.getBoolean("online"));
                        if (online) {
                            mTextViewRelativeTime.setText("En linea");
                        } else if (documentSnapshot.contains("lastConnect")) {
                            long lastConnect = documentSnapshot.getLong("lastConnect");
                            String relativeTime = RelativeTime.getTimeAgo(lastConnect, ChatActivity.this);
                            mTextViewRelativeTime.setText(relativeTime);
                        }
                    }
                    if (documentSnapshot.contains("fotoPerfil")) {
                        String imageProfile = documentSnapshot.getString("fotoPerfil");
                        if (imageProfile != null) {
                            if (!imageProfile.equals("")) {
                                Picasso.get().load(imageProfile).into(mCircleImageProfile);

                            }
                        }
                    }

                }
            }
        });
    }


    private void checkIfChatExist() {
        mChatsFirebase.getChatByUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    createChat();
                }
                else {
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    getMensajeChat();
                    updateViewed();
                }
            }
        });
    }

    private void updateViewed() {
        String idSender = "";
        if(mAuthFirebase.getUid().equals(mExtraIdUser1)){
            idSender = mExtraIdUser2;
        }else{
            idSender = mExtraIdUser1;
        }
        mMensajeFirebase.getMensajeByChatAndSender(mExtraIdChat,idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                    mMensajeFirebase.updateviewed(document.getId(), true);
                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimmestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatsFirebase.create(chat);
        mExtraIdChat = chat.getId();
        getMensajeChat();

    }
}