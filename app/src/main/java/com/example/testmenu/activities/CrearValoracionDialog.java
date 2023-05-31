package com.example.testmenu.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.testmenu.R;
import com.example.testmenu.entidades.FCMBody;
import com.example.testmenu.entidades.FCMResponse;
import com.example.testmenu.entidades.Valoraciones;
import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.NotificationFirebase;
import com.example.testmenu.firebase.TokenFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;
import com.example.testmenu.firebase.ValoracionFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearValoracionDialog extends DialogFragment {
    private String idUser;
    private CircleImageView fotoPerfil;
    private TextView nombreUser;
    private RatingBar estrellasCrear;
    private EditText escribirValoracion;
    private Button enviar, cancelar;
    private String valoracion;
    private String nUsuarioActivity;
    private AutentificacioFirebase autentificacioFirebase;
    private UsuariosBBDDFirebase usuariosBBDDFirebase;
    private ValoracionFirebase valoracionFirebase;
    private NotificationFirebase mNotificationFirebase;
    private TokenFirebase mTokenFirebase;
    private Context context;
    /**
     * Constructor vacío requerido para DialogFragment.
     */
    public CrearValoracionDialog() {
    }


    /**
     * Constructor de la clase CrearValoracionDialog.
     *
     * @param idUser El ID del usuario.
     */
    public CrearValoracionDialog(String idUser,Context context) {
        this.idUser = idUser;
        this.context = context;
    }


    /**
     * Método llamado para crear el diálogo.
     *
     * @param savedInstanceState Los datos guardados del fragmento.
     * @return El diálogo personalizado.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog customDialog = new Dialog(getActivity());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.alert_dialog_crear_valoracion);

        fotoPerfil = customDialog.findViewById(R.id.fotoUsuarioCrearValoracion);
        nombreUser = customDialog.findViewById(R.id.nombreUsuarioCrearValoracion);
        estrellasCrear = customDialog.findViewById(R.id.crearEstrellas);
        escribirValoracion = customDialog.findViewById(R.id.editEscribirValoracion);
        enviar = customDialog.findViewById(R.id.btnEnviarValoracion);
        cancelar = customDialog.findViewById(R.id.btnCancelarValoracion);

        autentificacioFirebase = new AutentificacioFirebase();  // Inicialización de AutentificacioFirebase
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();  // Inicialización de UsuariosBBDDFirebase
        valoracionFirebase = new ValoracionFirebase();  // Inicialización de ValoracionFirebase
        mNotificationFirebase = new NotificationFirebase();  // Inicialización de NotificationFirebase
        mTokenFirebase = new TokenFirebase();

        String userId = autentificacioFirebase.getUid();
        cargarDetallesUsuario(userId);

        cancelar.setOnClickListener(v -> {
            dismiss();
        });

        enviar.setOnClickListener(v -> {
            validarValoracion();
        });

        return customDialog;
    }



    /**
     * Se instancia el objeto Valoracion en el que se le asigna los datos del usuario con la sesión actual iniciada
     *
     * @param idUser id del usuario con la sesión iniciada
     * @return void
     */
    public void meterDatos(String idUser) {
        // Obtener el valor de las calificaciones del objeto estrellasCrear y convertirlo a String
        String ratings = String.valueOf(estrellasCrear.getRating());

        // Obtener el texto de la valoración del objeto escribirValoracion y eliminar los espacios en blanco al principio y al final
        valoracion = escribirValoracion.getText().toString().trim();

        // Obtener la marca de tiempo actual y convertirla a String
        String timestamp = String.valueOf(System.currentTimeMillis());

        // Crear un objeto Valoraciones
        Valoraciones v = new Valoraciones();

        // Establecer el valor de las calificaciones en el objeto Valoraciones
        v.setNota(ratings);

        // Establecer el valor de la valoración en el objeto Valoraciones
        v.setValoracion(valoracion);

        // Establecer el ID de usuario en el objeto Valoraciones
        v.setUserId(idUser);

        // Establecer el ID del usuario que realiza la publicación en el objeto Valoraciones
        v.setUserPostId(autentificacioFirebase.getUid());

        // Establecer la marca de tiempo en el objeto Valoraciones
        v.setTimeStamp(timestamp);

        // Crear la valoración en Firebase Firestore y agregar un listener para recibir el resultado
        valoracionFirebase.create(v).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Si la tarea se completa con éxito, mostrar un mensaje de éxito
                    Toast.makeText(context, "El comentario se creó correctamente", Toast.LENGTH_SHORT).show();

                    // Enviar una notificación con la valoración
                    sendNotification(valoracion);
                } else {
                    // Si la tarea no se completa con éxito, mostrar un mensaje de error
                    Toast.makeText(context, "No se pudo crear el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Carga los datos del usuario que está logeado y lo muestra en un Dialog customizado.
     * <p>
     * Se realiza una consulta filtrada por el id del usuario para obtener su información en un documento.
     * Si existe, se rellenan los datos pedidos para mostrarlos en el dialog
     *
     * @param userId id del usuario que está actualmente logeado
     * @return void
     */
    public void cargarDetallesUsuario(String userId) {
        // Verificar si el userId no es nulo
        if (userId != null) {
            // Obtener los detalles del usuario desde Firebase Firestore
            usuariosBBDDFirebase.getUsuarios(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    // Verificar si el documento existe
                    if (documentSnapshot.exists()) {
                        // Obtener el nombre de usuario del documento
                        if (documentSnapshot.contains("usuario")) {
                            nUsuarioActivity = documentSnapshot.getString("usuario");
                            nombreUser.setText("@" + nUsuarioActivity.toUpperCase());
                        }

                        // Obtener la URL de la foto de perfil del documento
                        if (documentSnapshot.contains("fotoPerfil")) {
                            String fotoPerfilActivity = documentSnapshot.getString("fotoPerfil");
                            // Verificar si la URL de la foto de perfil no es nula
                            if (fotoPerfilActivity != null) {
                                // Cargar la foto de perfil utilizando Picasso
                                Picasso.get().load(fotoPerfilActivity).into(fotoPerfil);
                            }
                        }
                    }
                }
            });
        }
    }



    /**
     * Adquiere el token del usuario al que se desea mandar la notificación y si existe se crea la notificación con su body creado.
     * <p>
     * Se realiza una consulta filtrada con el id del usuario logeado. Si encuentra su documento, se comprueba que los campos requeridos existan.
     * Se crea un mapa de datos que contiene información como el título, cuerpo del mensaje, nombres de usuario, ID del chat, etc.
     * Luego, utiliza el mapa de datos para enviar una notificación mediante el objeto <b>mNotificationFirebase<b>.
     *
     * @param comentario la valoracion que el usuario ha escrito
     * @return void
     */
    public void sendNotification(String comentario) {
        // Verificar si el ID de usuario es nulo
        if (idUser == null) {
            return;
        }

        // Obtener el token de notificación del usuario desde Firebase Firestore
        mTokenFirebase.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Verificar si el documento existe
                if (documentSnapshot.exists()) {
                    // Verificar si el documento contiene el token de notificación
                    if (documentSnapshot.contains("token")) {
                        String usr = autentificacioFirebase.getUid();
                        String token = documentSnapshot.getString("token");

                        // Crear un mapa de datos para la notificación
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "NUEVO COMENTARIO DE " + nUsuarioActivity);
                        data.put("body", valoracion);

                        // Crear el objeto FCMBody con el token y los datos de la notificación
                        FCMBody body = new FCMBody(token, "high", "4500s", data);

                        // Enviar la notificación utilizando la instancia de mNotificationFirebase
                        mNotificationFirebase.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null) {
                                    // Verificar si la respuesta indica que la notificación se envió con éxito
                                    if (response.body().getSuccess() == 1) {
                                        Toast.makeText(context, "La notificación se ha enviado", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "ERROR no se envió", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "La notificación NO se ha enviado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {
                                // Error al enviar la notificación
                            }
                        });
                    }
                }
            }
        });
    }


    /**
     * Valida la valoración ingresada por el usuario y la calificación seleccionada.
     *<p>
     * Verifica si los campos están vacíos y si se ha seleccionado una calificación válida.
     * Si los requisitos se cumplen, llama al método <b>meterDatos()<b> y luego cierra el diálogo.
     * Si los requisitos no se cumplen, muestra un mensaje de error correspondiente y no realiza ninguna acción adicional.
     *
     *@return void
     */
    public void validarValoracion() {
        String ratings = String.valueOf(estrellasCrear.getRating());
        valoracion = escribirValoracion.getText().toString().trim();

        // Verificar si los campos están vacíos
        if (valoracion.isEmpty()) {
            Toast.makeText(context, "Debe ingresar una valoración", Toast.LENGTH_SHORT).show();
            return; // No hace nada si no se cumple el requisito
        }

        // Verificar si el rating es cero
        if (ratings.equals("0.0")) {
            Toast.makeText(context, "Debe seleccionar una calificación", Toast.LENGTH_SHORT).show();
            return; // No hace nada si no se cumple el requisito
        }
        meterDatos(idUser);
        dismiss();
    }


}
