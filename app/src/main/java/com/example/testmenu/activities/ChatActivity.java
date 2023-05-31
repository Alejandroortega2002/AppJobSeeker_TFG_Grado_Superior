package com.example.testmenu.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
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
    private String mExtraIdUser1, mExtraIdUser2, mExtraIdChat, username, myUsername, mUsernameChat, mImageReceiver = "", mImageSender = "";
    private ChatsFirebase mChatsFirebase;
    private MensajeFirebase mMensajeFirebase;
    private AutentificacioFirebase mAuthFirebase;
    private UsuariosBBDDFirebase mUsuarioFirebase;
    private NotificationFirebase mNotificationFirebase;
    private TokenFirebase mTokenFirebase;
    private EditText mEditTextMensaje;
    private ImageView mImageViewSendMensaje;
    private CircleImageView mCircleImageProfile;
    private TextView mTextViewUsername, mTextViewRelativeTime;
    private ImageView mImageViewBack;
    private RecyclerView mRecyclerViewMensaje;
    private MensajeAdapter mAdapter;
    private View mActionBarView;
    private LinearLayoutManager mLinearLayoutManager;
    private ListenerRegistration mListener;
    private long mIdNotificationChat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        // Inicia la escucha del adaptador si no es nulo.
        if (mAdapter != null) {
            mAdapter.startListening();
        }

        // Actualiza el estado en línea del usuario utilizando ViewedMensajeHelper.
        ViewedMensajeHelper.updateOnline(true, ChatActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Detiene la escucha del adaptador.
        mAdapter.stopListening();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Detiene la escucha del adaptador.
        mAdapter.stopListening();

        // Actualiza el estado fuera de línea del usuario utilizando ViewedMensajeHelper.
        ViewedMensajeHelper.updateOnline(false, ChatActivity.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Elimina el Listener si no es nulo.
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

    public void getMensajeChat() {
        // Obtiene la consulta de mensajes utilizando el ID del chat.
        Query query = mMensajeFirebase.getMensajeByChat(mExtraIdChat);

        // Configura las opciones del adaptador de FirestoreRecyclerOptions.
        FirestoreRecyclerOptions<Mensaje> options =
                new FirestoreRecyclerOptions.Builder<Mensaje>()
                        .setQuery(query, Mensaje.class)
                        .build();

        // Crea un nuevo adaptador de mensajes con las opciones y la actividad actual.
        mAdapter = new MensajeAdapter(options, ChatActivity.this);

        // Configura el adaptador en el RecyclerView.
        mRecyclerViewMensaje.setAdapter(mAdapter);

        // Inicia la escucha del adaptador.
        mAdapter.startListening();

        // Registra un observador de datos del adaptador para realizar acciones cuando se insertan elementos.
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

                // Actualiza el estado de visualización de los mensajes.
                updateViewed();

                // Obtiene el número total de mensajes.
                int numMensajes = mAdapter.getItemCount();

                // Obtiene la posición del último mensaje completamente visible en el LinearLayoutManager.
                int lastMensajePosicion = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                // Comprueba si el último mensaje visible es -1 o si el rango insertado es el último rango en el adaptador.
                if (lastMensajePosicion == -1 || (positionStart >= (numMensajes - 1) && lastMensajePosicion == (positionStart - 1))) {
                    // Desplaza el RecyclerView hasta la posición de inicio del rango insertado.
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
     *
     * @return void
     */

    public void sendMensaje() {
        // Obtiene el texto del mensaje del EditText.
        String textMensaje = mEditTextMensaje.getText().toString();

        // Verifica si el texto del mensaje no está vacío.
        if (!textMensaje.isEmpty()) {
            // Crea una nueva instancia de Mensaje.
            final Mensaje mensaje = new Mensaje();

            // Establece el ID del chat en el mensaje.
            mensaje.setIdChat(mExtraIdChat);

            // Verifica si el ID de autenticación actual coincide con el ID de User1 en el chat.
            if (mAuthFirebase.getUid().equals(mExtraIdUser1)) {
                mensaje.setIdSender(mExtraIdUser1);
                mensaje.setIdReceiver(mExtraIdUser2);
            } else {
                mensaje.setIdSender(mExtraIdUser2);
                mensaje.setIdReceiver(mExtraIdUser1);
            }

            // Establece la marca de tiempo actual en el mensaje.
            mensaje.setTimestamp(new Date().getTime());

            // Establece el estado de visualización en falso para el mensaje.
            mensaje.setViewed(false);

            // Establece el ID del chat nuevamente en el mensaje (redundante, se recomienda eliminar esta línea).
            mensaje.setIdChat(mExtraIdChat);

            // Establece el contenido del mensaje.
            mensaje.setMessage(textMensaje);

            // Crea el mensaje en Firebase Firestore.
            mMensajeFirebase.create(mensaje).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Limpia el texto del EditText después de enviar el mensaje.
                    mEditTextMensaje.setText("");

                    // Notifica al adaptador que se han producido cambios en los datos.
                    mAdapter.notifyDataSetChanged();

                    // Obtiene el token de notificación y realiza las acciones necesarias.
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
    public void showCustomToolbar(int resource) {
        // Obtener referencia a la Toolbar en el diseño de la actividad
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Configurar la Toolbar como la barra de acción de la actividad
        setSupportActionBar(toolbar);

        // Obtener una referencia al ActionBar
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
     *
     * @return void
     */
    public void getUserInfo() {
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
                    // Obtener el nombre de usuario y mostrarlo en la interfaz
                    if (documentSnapshot.contains("usuario")) {
                        mUsernameChat = documentSnapshot.getString("usuario");
                        mTextViewUsername.setText(mUsernameChat);
                    }
                    // Obtener el estado de conexión y mostrarlo en la interfaz
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
                    // Obtener la foto de perfil del usuario y mostrarla en la interfaz
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
     *
     * @return void
     */

    public void checkIfChatExist() {
        // Verificar si existe un chat entre los usuarios
        mChatsFirebase.getChatByUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Obtener el número de chats encontrados
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    // No se encontró un chat existente, crear un nuevo chat
                    createChat();
                } else {
                    // Se encontró un chat existente
                    // Obtener el ID del chat y las notificaciones relacionadas del primer documento del resultado de la consulta
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mIdNotificationChat = queryDocumentSnapshots.getDocuments().get(0).getLong("idNotification");
                    // Obtener los mensajes del chat y mostrarlos
                    getMensajeChat();
                    // Actualizar el estado de visualización de los mensajes
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

    public void updateViewed() {
        String idSender = "";
        if (mAuthFirebase.getUid().equals(mExtraIdUser1)) {
            idSender = mExtraIdUser2;
        } else {
            idSender = mExtraIdUser1;
        }
        // Obtener los mensajes del chat enviados por el otro usuario
        mMensajeFirebase.getMensajeByChatAndSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Recorrer cada documento de mensajes
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    // Actualizar el estado de visualización del mensaje a "visto"
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
    public void createChat() {
        // Crear un nuevo objeto Chat
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

        // Crear una lista de IDs de usuarios involucrados en el chat
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);

        // Guardar el chat en la base de datos
        mChatsFirebase.create(chat);

        // Obtener el ID del chat creado
        mExtraIdChat = chat.getId();

        // Obtener los mensajes del chat
        getMensajeChat();
    }


    /**
     * Obtiene el token de notificación del usuario receptor y realiza una acción con el mensaje y el token.
     * Determina el ID del usuario receptor en función del ID del usuario actual y el ID del usuario en la conversación.
     * Obtiene el token de notificación del usuario receptor en la base de datos.
     * Realiza una acción con el mensaje y el token obtenidos.
     *
     * @param mensaje El mensaje que se va a enviar.
     */
    public void getToken(final Mensaje mensaje) {
        String idUser = "";
        if (mAuthFirebase.getUid().equals(mExtraIdUser1)) {
            idUser = mExtraIdUser2;
        } else {
            idUser = mExtraIdUser1;
        }

        // Obtener el token de notificación del usuario receptor
        mTokenFirebase.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String usr = mAuthFirebase.getUid();
                        String token = documentSnapshot.getString("token");
                        // Realizar una acción con el mensaje y el token obtenidos
                        getLastThreeMessages(mensaje, token);
                    }
                }
            }
        });
    }


    /**
     * Obtiene los últimos tres mensajes del chat correspondientes al remitente actual y realiza una acción adicional.
     * <p>
     * Utiliza el ID del chat y el ID del remitente actual para obtener los mensajes.
     * Se comprube que la consulta fue un éxito y se pasa y convierte el documento a un objeto de tipo Mensajes
     * Se utiliza el método <b>Collections.reverse()<b> para mostrar el array de los mensajes en orden correcto en las notificaciones.
     * Luego, realiza una acción adicional con los mensajes obtenidos y el token de notificación en <b>sendNotification()<b>
     *
     * @param message El mensaje actual para el cual se obtendrán los últimos tres mensajes y se realizará la acción adicional.
     * @param token   El token de notificación del usuario correspondiente.
     * @return void
     */

    public void getLastThreeMessages(Mensaje message, final String token) {
        // Obtener los últimos tres mensajes de la conversación y el remitente actual
        mMensajeFirebase.getLastThreeMensajeByChatAndSender(mExtraIdChat, mAuthFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Mensaje> mensajeArrayList = new ArrayList<>();

                // Recorrer los documentos obtenidos y agregarlos a la lista de mensajes
                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                    if (d.exists()) {
                        Mensaje message = d.toObject(Mensaje.class);
                        mensajeArrayList.add(message);
                    }
                }

                // Si no se obtuvieron mensajes, se agrega el mensaje actual a la lista
                if (mensajeArrayList.size() == 0) {
                    mensajeArrayList.add(message);
                }

                // Invertir el orden de la lista de mensajes
                Collections.reverse(mensajeArrayList);

                // Convertir la lista de mensajes a formato JSON utilizando la biblioteca Gson
                Gson gson = new Gson();
                String mensajes = gson.toJson(mensajeArrayList);

                // Realizar una acción con el token de notificación, los mensajes obtenidos y el mensaje actual
                sendNotification(token, mensajes, message);
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

    public void sendNotification(final String token, String messages, Mensaje message) {
        // Crear un mapa de datos para la notificación
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

        // Verificar y asignar imágenes válidas para el remitente y el receptor
        if (mImageSender == null || mImageSender.equals("")) {
            mImageSender = "IMAGEN_NO_VALIDA";
        }
        if (mImageReceiver == null || mImageReceiver.equals("")) {
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

        // Obtener el último mensaje del remitente
        mMensajeFirebase.getLastMessageSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                String lastMessage = "";
                if (size > 0) {
                    lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                    data.put("lastMessage", lastMessage);
                }

                // Crear el cuerpo de la notificación con el token, prioridad y datos
                FCMBody body = new FCMBody(token, "high", "4500s", data);

                // Enviar la notificación utilizando la API de Firebase Cloud Messaging
                mNotificationFirebase.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                            // Notificación enviada con éxito
                        } else {
                            Toast.makeText(ChatActivity.this, "ERROR: No se pudo enviar la notificación", Toast.LENGTH_SHORT).show();
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
     *
     * @return void
     */

    public void getMyInfoUser() {
        // Obtener la información del usuario actual
        mUsuarioFirebase.getUsuarios(mAuthFirebase.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Verificar si el documento contiene el campo "usuario"
                    if (documentSnapshot.contains("usuario")) {
                        // Obtener el nombre de usuario del documento
                        myUsername = documentSnapshot.getString("usuario");
                    }
                    // Verificar si el documento contiene el campo "fotoPerfil"
                    if (documentSnapshot.contains("fotoPerfil")) {
                        // Obtener la imagen de perfil del documento
                        mImageSender = documentSnapshot.getString("fotoPerfil");
                    }
                }
            }
        });
    }

}