package com.example.testmenu.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.example.testmenu.firebase.AutentificacioFirebase;
import com.example.testmenu.firebase.UsuariosBBDDFirebase;

import java.util.List;

public class ViewedMensajeHelper {
    public static void updateOnline(boolean status, final Context context) {
        UsuariosBBDDFirebase mUsuariosBBDDFirebase = new UsuariosBBDDFirebase();
        AutentificacioFirebase mAutentificacioFirebase = new AutentificacioFirebase();
        if (mAutentificacioFirebase.getUid() != null) {
            if (isApplicationSentToBackground(context)) {
                mUsuariosBBDDFirebase.updateOnline(mAutentificacioFirebase.getUid(), status);
            } else if (status) {
                mUsuariosBBDDFirebase.updateOnline(mAutentificacioFirebase.getUid(), status);

            }
        }
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
