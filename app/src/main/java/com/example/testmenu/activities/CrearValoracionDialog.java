package com.example.testmenu.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

    String valoracion;
    String nUsuarioActivity;
    AutentificacioFirebase autentificacioFirebase;
    UsuariosBBDDFirebase usuariosBBDDFirebase;
    ValoracionFirebase valoracionFirebase;

    NotificationFirebase mNotificationFirebase;
    TokenFirebase mTokenFirebase;

    private Context context;

    // Constructor vacío requerido para DialogFragment
    public CrearValoracionDialog() {
    }

    public CrearValoracionDialog(String idUser) {
        this.idUser = idUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        autentificacioFirebase = new AutentificacioFirebase();
        usuariosBBDDFirebase = new UsuariosBBDDFirebase();
        mNotificationFirebase = new NotificationFirebase();
        mTokenFirebase = new TokenFirebase();
        valoracionFirebase = new ValoracionFirebase();
        context = getContext();
    }

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


    public void meterDatos(String idUser) {
        String ratings = String.valueOf(estrellasCrear.getRating());
        valoracion = escribirValoracion.getText().toString().trim();

        String timestamp = String.valueOf(System.currentTimeMillis());

        Valoraciones v = new Valoraciones();
        v.setNota(ratings);
        v.setValoracion(valoracion);
        v.setUserId(idUser);
        v.setUserPostId(autentificacioFirebase.getUid());
        v.setTimeStamp(timestamp);

        valoracionFirebase.create(v).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "El comentario se creó correctamente", Toast.LENGTH_SHORT).show();
                    sendNotification(valoracion);
                } else {
                    Toast.makeText(context, "No se pudo crear el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cargarDetallesUsuario(String userId) {
        if (userId != null) {
            usuariosBBDDFirebase.getUsuarios(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("usuario")) {
                            nUsuarioActivity = documentSnapshot.getString("usuario");
                            nombreUser.setText("@" + nUsuarioActivity.toUpperCase());
                        }
                        if (documentSnapshot.contains("fotoPerfil")) {
                            String fotoPerfilActivity = documentSnapshot.getString("fotoPerfil");
                            if (fotoPerfilActivity != null) {
                                Picasso.get().load(fotoPerfilActivity).into(fotoPerfil);
                            }
                        }
                    }
                }
            });
        }
    }

    private void sendNotification(String comentario) {
        if (idUser == null) {
            return;
        }
        mTokenFirebase.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String usr = autentificacioFirebase.getUid();
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "NUEVO COMENTARIO DE " + nUsuarioActivity);
                        data.put("body", valoracion);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationFirebase.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null) {
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

                            }
                        });
                    }
                }
            }
        });
    }

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
