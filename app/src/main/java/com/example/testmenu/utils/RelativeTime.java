package com.example.testmenu.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RelativeTime extends Application {

    static int SECOND_MILLIS = 1000;
    static int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    static int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    static int DAY_MILLIS = 24 * HOUR_MILLIS;

    /**
     * Retorna una descripción del tiempo transcurrido desde un momento dado hasta el momento actual.
     *
     * @param time el tiempo proporcionado en milisegundos o segundos si es menor a 1 billón
     * @param ctx  el contexto de la aplicación
     * @return una cadena que describe el tiempo transcurrido
     */
    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // Si el timestamp se proporciona en segundos, se convierte a milisegundos
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "Hace un momento";
        }

        // Se calcula la diferencia de tiempo entre el momento actual y el tiempo proporcionado
        long diff = now - time;

        // Se verifica la diferencia de tiempo y se devuelve la descripción correspondiente
        if (diff < MINUTE_MILLIS) {
            return "Hace un momento";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Hace un minuto";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "Hace " + diff / MINUTE_MILLIS + " minutos";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Hace una hora";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "Hace " + diff / HOUR_MILLIS + " horas";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Ayer";
        } else {
            return "Hace " + diff / DAY_MILLIS + " días";
        }
    }


    /**
     * Formatea el tiempo proporcionado en milisegundos o segundos si es menor a 1 billón
     * en el formato de hora AM/PM.
     *
     * @param time el tiempo proporcionado en milisegundos o segundos si es menor a 1 billón
     * @param ctx  el contexto de la aplicación
     * @return una cadena que representa el tiempo formateado en el formato de hora AM/PM,
     * "Ayer" si el tiempo está dentro de las últimas 48 horas, o
     * "Hace x días" si el tiempo está más atrás en el pasado
     */
    public static String timeFormatAMPM(long time, Context ctx) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        if (time < 1000000000000L) {
            // Si el timestamp se proporciona en segundos, se convierte a milisegundos
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            // Si el tiempo es mayor que el tiempo actual o es menor o igual a cero,
            // se formatea el tiempo proporcionado utilizando el formato de hora AM/PM
            String dateString = formatter.format(new Date(time));
            return dateString;
        }

        // Se calcula la diferencia de tiempo entre el momento actual y el tiempo proporcionado
        long diff = now - time;

        // Se realizan comparaciones en función de la diferencia de tiempo y se retorna la descripción adecuada
        if (diff < 24 * HOUR_MILLIS) {
            // Si la diferencia es menor a 24 horas, se formatea el tiempo proporcionado utilizando el formato de hora AM/PM
            String dateString = formatter.format(new Date(time));
            return dateString;
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Ayer";
        } else {
            return "Hace " + diff / DAY_MILLIS + " días";
        }
    }

}