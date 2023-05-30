package com.example.testmenu.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FileUtil {

    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public FileUtil() {

    }

    /**
     * Convierte una Uri en un archivo temporal en el dispositivo.
     *
     * @param context el contexto actual
     * @param uri     la Uri del archivo a convertir
     * @return el archivo temporal creado a partir de la Uri
     * @throws IOException si hay algún error al crear o escribir el archivo temporal
     */
    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    /**
     * Este método divide el nombre de archivo en dos partes: el nombre del archivo y su extensión.
     * <p>
     * Si el nombre de archivo no contiene una extensión, la segunda parte del arreglo devuelto será una cadena vacía.
     *
     * @param fileName el nombre de archivo a dividir
     * @return un arreglo de dos elementos que contiene el nombre del archivo y su extensión
     */
    public static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    /**
     * Método para obtener el nombre del archivo a partir de una Uri.
     *
     * @param context El contexto actual.
     * @param uri     La Uri del archivo.
     * @return El nombre del archivo.
     */
    @SuppressLint("Range")
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    // Obtener el nombre del archivo a través de la columna DISPLAY_NAME del cursor.
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            // Si no se puede obtener el nombre a través del cursor, se extrae del path de la Uri.
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    /**
     * Renombra un archivo con un nuevo nombre y devuelve el nuevo archivo renombrado.
     * Si ya existe un archivo con el mismo nombre que el nuevo nombre, elimina el archivo antiguo.
     *
     * @param file    archivo a renombrar.
     * @param newName nuevo nombre para el archivo.
     * @return el nuevo archivo renombrado.
     */
    public static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    /**
     * Copia los datos de entrada de un flujo de entrada a un flujo de salida, usando un búfer de tamaño por defecto.
     * Devuelve el número total de bytes copiados y lanza una IOException en caso de error.
     *
     * @param input  flujo de entrada a copiar.
     * @param output flujo de salida donde se copiarán los datos.
     * @return el número total de bytes copiados.
     * @throws IOException si ocurre un error al copiar los datos.
     */
    public static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
