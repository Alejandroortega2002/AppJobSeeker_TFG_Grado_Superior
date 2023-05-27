package com.example.testmenu.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmenu.R;
import com.example.testmenu.adapters.MensajeAdapter;
import com.example.testmenu.entidades.Chat;
import com.example.testmenu.entidades.FCMBody;
import com.example.testmenu.entidades.FCMResponse;
import com.example.testmenu.entidades.Mensaje;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.ChatsFirebase;
import com.example.testmenu.firebase.MensajeFirebase;
import com.example.testmenu.firebase.NotificationFirebase;
import com.example.testmenu.firebase.TokenFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.utils.RelativeTime;
import com.example.testmenu.utils.ViewedMensajeHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;
    ChatsFirebase mChatsFirebase;
    MensajeFirebase mMensajeFirebase;
    AutentificacioFirebase mAuthFirebase;
    UsuariosBBDDFirebase mUsuarioFirebase;
    NotificationFirebase mNotificationFirebase;

    TokenFirebase mTokenFirebase;

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
    String username;

    long mIdNotificationChat;

    String myUsername;
    String mUsernameChat;

    String mImageReceiver = "";
    String mImageSender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatsFirebase = new ChatsFirebase();
        mMensajeFirebase = new MensajeFirebase();
        mAuthFirebase = new AutentificacioFirebase();
        mUsuarioFirebase = new UsuariosBBDDFirebase();
        mNotificationFirebase = new NotificationFirebase();
        mTokenFirebase = new TokenFirebase();

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

        getMyInfoUser();


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
        if (mAdapter != null) {
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
        if (mListener != null) {
            mListener.remove();
        }
    }

    /**
     * Obtiene los mensajes de un chat específico y los muestra en el RecyclerView.
     * <p>
     *
     * @return void
     */

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
            /**
             * Se invoca cuando se inserta un rango de elementos en el adaptador.
             *
             * @param positionStart La posición de inicio del rango insertado.
             * @param itemCount     El número de elementos insertados.
             */
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int numMensajes = mAdapter.getItemCount();
                int lastMensajePosicion = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastMensajePosicion == -1 || (positionStart >= (numMensajes - 1) && lastMensajePosicion == (positionStart - 1))) {
                    mRecyclerViewMensaje.scrollToPosition(positionStart);
                }
            }
        });
    }


    /**
     * Almacena el mensaje y lo envia al usuario destinatario.
     * <p>
     * El mensaje se alamcena en una variable y se comprueba si esta tiene algun valor.
     * Se crea un objeto de tipo <b>Mensaje</b> y se le asigna los valores requeridos.
     * Dependiendo de quien envió el mensaje, será asignado en <b>mensaje.setIdSender(mExtraIdUser1)</b>.
     * Se crea el objeto en la documentación de Firebase y se realiza un listener de la tarea.
     * Si termina con éxito, se notifica al adaptador de los datos modificados y se llama al método <b>getToken()</b>
     * <p>
     * @return void
     */

    private void sendMensaje() {
        String textMensaje = mEditTextMensaje.getText().toString();
        if (!textMensaje.isEmpty()) {
            final Mensaje mensaje = new Mensaje();
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
            mensaje.setIdChat(mExtraIdChat);
            mensaje.setMessage(textMensaje);


            //Crea el mensaje en Firebase Firestore.

            mMensajeFirebase.create(mensaje).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mEditTextMensaje.setText("");
                    mAdapter.notifyDataSetChanged();
                    getToken(mensaje);
                } else {
                    Toast.makeText(ChatActivity.this, "El mensaje no se pudo crear", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * Muestra una barra de herramientas personalizada con los elementos proporcionados.
     *
     * @param resource El recurso de diseño para la barra de herramientas personalizada.
     * @return void
     */
    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();

        // Configuración de la barra de herramientas
        actionbar.setTitle("");
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);

        // Inflar el diseño personalizado de la barra de herramientas
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionbar.setCustomView(mActionBarView);

        // Obtener referencias a los elementos de la barra de herramientas personalizada
        mCircleImageProfile = mActionBarView.findViewById(R.id.circleImageProfile);
        mTextViewUsername = mActionBarView.findViewById(R.id.textViewUsername);
        mTextViewRelativeTime = mActionBarView.findViewById(R.id.textViewRelativeTime);
        mImageViewBack = mActionBarView.findViewById(R.id.imageViewBack);

        // Configurar el evento click del botón de retroceso
        mImageViewBack.setOnClickListener(v -> finish());

        // Obtener información del usuario
        getUserInfo();
    }


    /**
     * Obtiene la información del usuario relacionado al chat y actualiza la interfaz de usuario correspondiente.
     * <p>
     * @return void
     */
    private void getUserInfo() {
        String idUserInfo = "";

        // Determinar el ID del usuario relacionado al chat
        if (mAuthFirebase.getUid().equals(mExtraIdUser1)) {
            idUserInfo = mExtraIdUser2;
        } else {
            idUserInfo = mExtraIdUser1;
        }

        // Obtener información del usuario en tiempo real
        mListener = mUsuarioFirebase.getUsuariosRealTime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        mUsernameChat = documentSnapshot.getString("usuario");
                        mTextViewUsername.setText(mUsernameChat);
                    }
                    if (documentSnapshot.contains("online")) {
                        boolean online = Boolean.TRUE.equals(documentSnapshot.getBoolean("online"));
                        if (online) {
                            mTextViewRelativeTime.setText("En línea");
                        } else if (documentSnapshot.contains("lastConnect")) {
                            long lastConnect = documentSnapshot.getLong("lastConnect");
                            String relativeTime = RelativeTime.getTimeAgo(lastConnect, ChatActivity.this);
                            mTextViewRelativeTime.setText(relativeTime);
                        }
                    }
                    if (documentSnapshot.contains("fotoPerfil")) {
                        mImageReceiver = documentSnapshot.getString("fotoPerfil");
                        if (mImageReceiver != null && !mImageReceiver.equals("")) {
                            Picasso.get().load(mImageReceiver).into(mCircleImageProfile);
                        }
                    }
                }
            }
        });
    }


    /**
     * Comprobar si el chat ya existe entre usuarios, en caso contrario se crea uno nuevo.
     * <p>
     * @return void
     */

    private void checkIfChatExist() {
        mChatsFirebase.getChatByUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    createChat();
                } else {
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mIdNotificationChat = queryDocumentSnapshots.getDocuments().get(0).getLong("idNotification");
                    getMensajeChat();
                    updateViewed();
                }
            }
        });
    }

    /**
     * Actualiza el estado de visualización de los mensajes en el chat.
     * Comprueba el remitente de los mensajes y marca los mensajes correspondientes como vistos.
     * <p>
     * Se comprueba que usuario es el autenticado, dependiendo de ello, la vista es diferente.
     * Se obtiene los mensajes del chat para el remitente específico.
     * Recorre los documentos de los mensajes y marca cada uno como visto
     *
     * @return void
     */

    private void updateViewed() {
        String idSender = "";
        if (mAuthFirebase.getUid().equals(mExtraIdUser1)) {
            idSender = mExtraIdUser2;
        } else {
            idSender = mExtraIdUser1;
        }
        mMensajeFirebase.getMensajeByChatAndSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    mMensajeFirebase.updateviewed(document.getId(), true);
                }
            }
        });
    }

    /**
     * Crea un nuevo chat entre los usuarios especificados y establece los valores iniciales.
     *
     * @return void
     */
    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimmestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);
        Random random = new Random();
        int n = random.nextInt(1000000);
        chat.setIdNotification(n);
        mIdNotificationChat = n;

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatsFirebase.create(chat);
        mExtraIdChat = chat.getId();
        getMensajeChat();
    }


    /**
     * Obtiene el token de notificación del usuario correspondiente al mensaje y realiza una acción adicional.
     * <p>
     * Comprueba el ID del usuario actual y obtiene el token del otro usuario del chat.
     * Luego, utiliza el token para realizar una acción adicional con el método <b>getLastThreeMessages()<b>
     *
     * @param mensaje Objeto de clase Mensaje, se extraen los mensajes enviados.
     * @return void
     *
     */

    private void getToken(final Mensaje mensaje) {
        String idUser = "";
        if (mAuthFirebase.getUid().equals(mExtraIdUser1)) {
            idUser = mExtraIdUser2;
        } else {
            idUser = mExtraIdUser1;
        }

        mTokenFirebase.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String usr = mAuthFirebase.getUid();
                        String token = documentSnapshot.getString("token");
                        getLastThreeMessages(mensaje, token);
                    }
                }
            }
        });
    }

    /**
     * Obtiene los últimos tres mensajes del chat correspondientes al remitente actual y realiza una acción adicional.
     *<p>
     * Utiliza el ID del chat y el ID del remitente actual para obtener los mensajes.
     * Se comprube que la consulta fue un éxito y se pasa y convierte el documento a un objeto de tipo Mensajes
     * Se utiliza el método <b>Collections.reverse()<b> para mostrar el array de los mensajes en orden correcto en las notificaciones.
     * Luego, realiza una acción adicional con los mensajes obtenidos y el token de notificación en <b>sendNotification()<b>
     *
     * @param message El mensaje actual para el cual se obtendrán los últimos tres mensajes y se realizará la acción adicional.
     * @param token   El token de notificación del usuario correspondiente.
     * @return void
     */

    private void getLastThreeMessages(Mensaje message, final String token) {
        mMensajeFirebase.getLastThreeMensajeByChatAndSender(mExtraIdChat, mAuthFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Mensaje> mensajeArrayList = new ArrayList<>();

                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                    if (d.exists()) {
                        Mensaje message = d.toObject(Mensaje.class);
                        mensajeArrayList.add(message);
                    }
                }

                if (mensajeArrayList.size() == 0) {
                    mensajeArrayList.add(message);
                }

                Collections.reverse(mensajeArrayList);
                Gson gson = new Gson();
                String mensajes = gson.toJson(mensajeArrayList);
                sendNotificaction(token, mensajes, message);
            }
        });
    }


    /**
     * Envía una notificación utilizando el token de notificación y otros datos relacionados con el mensaje.
     * <p>
     * Crea un mapa de datos que contiene información como el título, cuerpo del mensaje, nombres de usuario, ID del chat, etc.
     * Luego, utiliza el mapa de datos para enviar una notificación mediante el objeto <b>mNotificationFirebase<b>.
     *
     * @param token    El token de notificación del destinatario.
     * @param messages Los mensajes en formato JSON.
     * @param message  El mensaje actual para el cual se enviará la notificación.
     * @return void
     */

    private void sendNotificaction(final String token, String messages, Mensaje message) {
        final Map<String, String> data = new HashMap<>();
        data.put("title", "MENSAJE");
        data.put("body", message.getMessage());
        data.put("idNotification", String.valueOf(mIdNotificationChat));
        data.put("messages", messages);
        data.put("usernameSender", myUsername.toUpperCase());
        data.put("usernameReceiver", mUsernameChat.toUpperCase());
        data.put("idSender", message.getIdSender());
        data.put("idReceiver", message.getIdReceiver());
        data.put("idChat", message.getIdChat());

        if (mImageSender.equals("")) {
            mImageSender = "IMAGEN_NO_VALIDA";
        }
        if (mImageReceiver.equals("")) {
            mImageReceiver = "IMAGEN_NO_VALIDA";
        }
        data.put("imageSender", mImageSender);
        data.put("imageReceiver", mImageReceiver);

        String idSender = "";
        if (mAuthFirebase.getUid().equals(mExtraIdUser1)) {
            idSender = mExtraIdUser2;
        } else {
            idSender = mExtraIdUser1;
        }
        mMensajeFirebase.getLastMessageSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                String lastMessage = "";
                if (size > 0) {
                    lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                    data.put("lastMessage", lastMessage);
                }
                FCMBody body = new FCMBody(token, "high", "4500s", data);
                mNotificationFirebase.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getSuccess() == 1) {
                                // Notificación enviada con éxito
                            } else {
                                Toast.makeText(ChatActivity.this, "ERROR: No se pudo enviar la notificación", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "Mensaje NO enviado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                        // Error al enviar la notificación
                    }
                });
            }
        });
    }

    /**
     * Se  obtiene la información necesaria del usuario que está autenticado en la app.
     * <p>
     * @return void
     */

    private void getMyInfoUser() {
        mUsuarioFirebase.getUsuarios(mAuthFirebase.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        myUsername = documentSnapshot.getString("usuario");
                    }
                    if (documentSnapshot.contains("fotoPerfil")) {
                        mImageSender = documentSnapshot.getString("fotoPerfil");
                    }
                }
            }
        });
    }

}