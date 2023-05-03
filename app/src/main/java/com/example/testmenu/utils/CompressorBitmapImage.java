package com.example.testmenu.utils;


import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;


public class CompressorBitmapImage {

    /*
     * Metodo que permite comprimir imagenes y transformarlas a bitmap
     */

    /**
     * Obtiene una imagen comprimida a partir de una ruta de archivo, con un ancho y alto máximos especificados.
     *
     * @param ctx    Contexto de la aplicación.
     * @param path   Ruta de archivo de la imagen a comprimir.
     * @param width  Ancho máximo deseado para la imagen comprimida.
     * @param height Alto máximo deseado para la imagen comprimida.
     * @return Un arreglo de bytes que representa la imagen comprimida.
     */
    public static byte[] getImage(Context ctx, String path, int width, int height) {
        // Se obtiene el archivo de la ruta especificada.
        final File file_thumb_path = new File(path);
        Bitmap thumb_bitmap = null;

        // Se comprime la imagen a los parámetros especificados.
        try {
            thumb_bitmap = new Compressor(ctx)
                    .setMaxWidth(width)
                    .setMaxHeight(height)
                    .setQuality(75)
                    .compressToBitmap(file_thumb_path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Se comprime aún más la imagen a un formato JPEG y se convierte a un arreglo de bytes.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assert thumb_bitmap != null;
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }
}

