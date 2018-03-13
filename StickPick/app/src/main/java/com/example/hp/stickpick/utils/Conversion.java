package com.example.hp.stickpick.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Shadab Azam Farooqui on 6/13/2017.
 */

public class Conversion {

    public static String stringFromBitmap(Bitmap bitmap) {
        if (bitmap!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String str = Base64.encodeToString(b, Base64.DEFAULT);
            return str;
        }
        else {
            return "";
        }

    }

    public Bitmap bitmapFromString(String encodedString) {
        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;
    }

    public static String stringFromByteArray(byte[] gzip) throws IOException {
        java.util.zip.Inflater inf = new java.util.zip.Inflater();
        java.io.ByteArrayInputStream bytein = new java.io.ByteArrayInputStream(gzip);
        java.util.zip.GZIPInputStream gzin = new java.util.zip.GZIPInputStream(bytein);
        java.io.ByteArrayOutputStream byteout = new java.io.ByteArrayOutputStream();
        int res = 0;
        byte buf[] = new byte[1024];
        while (res >= 0) {
            res = gzin.read(buf, 0, buf.length);
            if (res > 0) {
                byteout.write(buf, 0, res);
            }
        }
        byte uncompressed[] = byteout.toByteArray();
        return (uncompressed.toString());
    }

    public static Bitmap bitmapFromByteArray(byte[] imgbytes) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length);
        //imgbytes = Base64.decode(imgbytes, Base64.DEFAULT);
        // Bitmap bitmap = BitmapFactory.decodeByteArray(imgbytes, 0,imgbytes.length);
        return bitmap;
    }

    public static byte[] byteArrayFromBitmap(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Bitmap BitMapfromString(String encodedString) {

        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;

    }

}
