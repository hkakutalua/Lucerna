package com.bitpapr.lucerna.utilities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by henrick on 12/14/17.
 */

/**
 * Utility class to handle Bitmap in our app
 */
public class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    /**
     * Loads a bitmap from a local Uri
     * @param localUri the local uri
     * @param context the app context
     * @return a bitmap loaded from a local uri
     */
    public static Bitmap loadBitmapFromUri(Uri localUri, Context context) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(localUri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException exception) {
            Log.e(TAG, exception.getMessage());
            return null;
        }
    }

    /**
     * Loads a bitmap from a local Uri and then compress it to
     * reduce it's size
     * @param localUri the local uri
     * @param context the app context
     * @return a compressed bitmap
     */
    public static Bitmap loadCompressedBitmapFromUri(Uri localUri, Context context) {
        Bitmap compressedBitmap = loadBitmapFromUri(localUri, context);

        if (compressedBitmap != null) {
            ByteArrayOutputStream bitmapOutputStream = new ByteArrayOutputStream();

            boolean success = compressedBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    20,
                    bitmapOutputStream);

            compressedBitmap = BitmapFactory.decodeStream(
                    new ByteArrayInputStream(bitmapOutputStream.toByteArray()));

            if (!success) return null;
        }

        return compressedBitmap;
    }

    /**
     * Put the bitmap in a byte array
     * @param bitmap the bitmap
     * @return a byte array containing the bitmap content
     */
    public static byte[] getBitmapByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } else {
            return null;
        }
    }
}
