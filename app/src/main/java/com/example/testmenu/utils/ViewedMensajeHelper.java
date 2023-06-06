package com.example.testmenu.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;

import java.util.List;

public class ViewedMensajeHelper {

    /**
     * Actualiza el estado en línea del usuario en Firebase.
     *
     * @param status el nuevo estado en línea del usuario
     * @param context el contexto de la aplicación
     */
    public static void updateOnline(boolean status, final Context context) {
        UsuariosBBDDFirebase mUsuariosBBDDFirebase = new UsuariosBBDDFirebase();
        AutentificacioFirebase mAutentificacioFirebase = new AutentificacioFirebase();

        // Verifica si el ID de usuario actual no es nulo
        if (mAutentificacioFirebase.getUid() != null) {
            // Verifica si la aplicación está en segundo plano
            if (isApplicationSentToBackground(context)) {
                // Actualiza el estado en línea del usuario en Firebase
                mUsuariosBBDDFirebase.updateOnline(mAutentificacioFirebase.getUid(), status);
            } else if (status) {
                // Si el estado es verdadero y la aplicación no está en segundo plano,
                // actualiza el estado en línea del usuario en Firebase
                mUsuariosBBDDFirebase.updateOnline(mAutentificacioFirebase.getUid(), status);
            }
        }
    }


    /**
     * Verifica si la aplicación se encuentra en segundo plano.
     *
     * @param context el contexto de la aplicación
     * @return true si la aplicación está en segundo plano, false en caso contrario
     */
    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);

        // Verifica si hay tareas en ejecución
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;

            // Compara el paquete de la actividad superior con el paquete de la aplicación
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

}
