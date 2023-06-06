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
        // Se abre un InputStream para leer los datos del Uri especificado
        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        // Se obtiene el nombre del archivo utilizando el método getFileName()
        String fileName = getFileName(context, uri);

        // Se divide el nombre del archivo en dos partes: nombre y extensión
        String[] splitName = splitFileName(fileName);

        // Se crea un archivo temporal con el prefijo y sufijo obtenidos del nombre del archivo
        File tempFile = File.createTempFile(splitName[0], splitName[1]);

        // Se renombra el archivo temporal con el nombre original del archivo
        tempFile = rename(tempFile, fileName);

        // Se indica que el archivo se borrará automáticamente cuando el programa termine
        tempFile.deleteOnExit();

        FileOutputStream out = null;
        try {
            // Se crea un FileOutputStream para escribir en el archivo temporal
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Se copian los datos del InputStream al FileOutputStream
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }

        // Se retorna el archivo temporal
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
            // Si se encuentra un punto en el nombre del archivo, se divide en nombre y extensión
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        // Se retorna un arreglo de cadenas con el nombre y la extensión del archivo
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
            // Si el esquema de la Uri es "content", se realiza una consulta a través del ContentResolver
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    // Se obtiene el nombre del archivo a través de la columna DISPLAY_NAME del cursor
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
            // Si no se puede obtener el nombre a través del cursor, se extrae del path de la Uri
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                // Se obtiene el nombre del archivo a partir del último separador de directorio en el path de la Uri
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
        // Se crea un nuevo objeto File con el directorio padre y el nuevo nombre
        File newFile = new File(file.getParent(), newName);

        // Se verifica si el nuevo archivo es diferente al archivo original
        if (!newFile.equals(file)) {
            // Si el nuevo archivo ya existe, se elimina el archivo existente
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old " + newName + " file");
            }

            // Se intenta cambiar el nombre del archivo original al nuevo nombre
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }

        // Se retorna el nuevo archivo
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
        long count = 0; // Variable para contar el número de bytes copiados
        int n; // Variable para almacenar el número de bytes leídos en cada iteración
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE]; // Buffer para almacenar los bytes leídos

        // Se realiza la copia de los bytes del InputStream al OutputStream
        while (EOF != (n = input.read(buffer))) {
            // Se escribe el contenido del buffer en el OutputStream
            output.write(buffer, 0, n);
            // Se actualiza el contador de bytes copiados
            count += n;
        }

        // Se retorna el total de bytes copiados
        return count;
    }
}
