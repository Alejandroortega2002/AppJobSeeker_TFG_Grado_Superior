package com.example.testmenu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmenu.R;
import com.example.testmenu.activities.ChatActivity;
import com.example.testmenu.entidades.Chat;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.ChatsFirebase;
import com.example.testmenu.firebase.MensajeFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

   private Context context;
   private UsuariosBBDDFirebase mUsersProvider;
   private AutentificacioFirebase mAuthProvider;
   private ChatsFirebase mChatsFirebase;

   private MensajeFirebase mMensajeFirebase;

   private ListenerRegistration mListener;
   private ListenerRegistration mListenerLastMessage;

    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsuariosBBDDFirebase();
        mAuthProvider = new AutentificacioFirebase();
        mChatsFirebase = new ChatsFirebase();
        mMensajeFirebase = new MensajeFirebase();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();
        if(mAuthProvider.getUid().equals(chat.getIdUser1())){
            getUserInfo(chat.getIdUser2(),holder);
        }else{
            getUserInfo(chat.getIdUser1(),holder);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    goToChatActivity(chatId, chat.getIdUser1(), chat.getIdUser2());
            }
        });
        getLastMessage(chatId,holder.textViewLastMessage);

        String idSender = "";
        if (mAuthProvider.getUid().equals(chat.getIdUser1())){
            idSender= chat.getIdUser2();
        } else {
            idSender = chat.getIdUser1();
        }
        getMessageNotRead(chatId,idSender, holder.mensajesNoLeidos,holder.frameLayoutMessageNotRead);
    }

    /**
     * Muestra el numero de mensajes sin leer por el usuario.
     * <p>
     * Se consulta a la base de datos el obtener los mensajes nuevos del chat, filtrados por <b>chatId</b> e <b>idSender</b>.
     * La consulta se hace a tiempo real. Se comprueba si el valor de la consulta está vacio o no.
     * En caso de haber mensajes sin leer, se muestra un aviso de nuevos mensajes, este desaparece si no hay mensajes nuevos, o si ya han sido enviados.
     *
     * @param chatId id del chat entre los usuarios
     * @param idSender id del usuario que envia los mensajes
     * @param mensajesNoLeidos donde se muestra el numero de mensajes no leidos
     * @param frameLayoutMessageNotRead layout que se aparecerá si hay mensajes.
     * @return void
     */
    public void getMessageNotRead(String chatId, String idSender, TextView mensajesNoLeidos, FrameLayout frameLayoutMessageNotRead) {
        mListener= mMensajeFirebase.getMensajeByChatAndSender(chatId,idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    int size = value.size();
                    if (size>0){
                        frameLayoutMessageNotRead.setVisibility(View.VISIBLE);
                        mensajesNoLeidos.setText(String.valueOf(size));
                    } else {
                        frameLayoutMessageNotRead.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public ListenerRegistration getListener(){
        return mListener;
    }
    public ListenerRegistration getmListenerLastMessage(){
        return mListenerLastMessage;
    }

    /**
     * Muestra el último mensaje enviado en el chat en un TextView.
     * <p>
     * Consulta la base de datos filtrado por el id del chat en tiempo real.
     * Si no está vacio, se obtiene el último mensaje del documento y se le asigna al <b>TextView</b> pasado como parámetro.
     *
     * @param chatId id del chat del que se desea extraer el mensaje
     * @param textViewLastMessage textView en donde se monstrará el último mensaje.
     * @return void
     */
    public void getLastMessage(String chatId, TextView textViewLastMessage) {
        mListenerLastMessage= mMensajeFirebase.getLastMessage(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null){
                    int size = value.size();
                    if (size>0){
                        String lastMessage = value.getDocuments().get(0).getString("message");
                        textViewLastMessage.setText(lastMessage);
                    }
                }
            }
        });
    }

    public void goToChatActivity(String chatId,String idUser1,String idUser2) {

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat", chatId);
        intent.putExtra("idUser1", idUser1);
        intent.putExtra("idUser2", idUser2);
        context.startActivity(intent);
    }

    /**
     * Obtiene la info de el usuario del que se quiere
     * @param idUser el id del usuario al que mostrar su info
     * @param holder el viewholder en donde mostrar la info
     * @return void
     */
    public void getUserInfo(String idUser, final ViewHolder holder) {
        mUsersProvider.getUsuarios(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        String username = documentSnapshot.getString("usuario");
                        holder.textViewUsername.setText(username.toUpperCase());
                    }
                    if (documentSnapshot.contains("fotoPerfil")) {
                        String imageProfile = documentSnapshot.getString("fotoPerfil");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.get().load(imageProfile).into(holder.circleImageChat);
                            }
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview_chat, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewUsername;
        private TextView textViewLastMessage;
        private FrameLayout frameLayoutMessageNotRead;
        private TextView mensajesNoLeidos;
        private CircleImageView circleImageChat;
        private View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.nombreUsuarioChat);
            textViewLastMessage = view.findViewById(R.id.textViewUltimoMensajeChat);
            mensajesNoLeidos = view.findViewById(R.id.txtNotRead);
            circleImageChat = view.findViewById(R.id.circleImageChat);
            frameLayoutMessageNotRead = view.findViewById(R.id.frameLayoutMessageNotRead);
            viewHolder = view;
        }
    }

}
