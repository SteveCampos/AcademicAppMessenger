package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.MediaFile;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageCompression extends AsyncTask<Uri, Void, MediaFile> {

    private static final String TAG = ImageCompression.class.getSimpleName();
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;

    private File cacheDir;
    private ContentResolver resolver;

    public ImageCompression(File cacheDir, ContentResolver resolver) {
        this.cacheDir = cacheDir;
        this.resolver = resolver;
    }

    @Override
    protected MediaFile doInBackground(Uri... uris) {
        if (uris[0] == null || uris[0].equals(Uri.EMPTY))
            return null;

        return compressImage(uris[0]);
    }
/*

    protected void onPostExecute(Uri imagePathCompressed) {
        // imagePath is path of new compressed image.
    }
*/

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                resolver.openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private MediaFile compressImage(Uri uri) {

        String imagePath = uri.getPath();
        Log.d(TAG, "uri.getPath(): " + uri.getPath());
        Log.d(TAG, "uri.getLastPathSegment(): " + uri.getLastPathSegment());
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmp = null;
        ParcelFileDescriptor parcelFileDescriptor =
                null;
        try {
            parcelFileDescriptor = resolver.openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        //Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        try {
            bmp = getBitmapFromUri(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        Log.d(TAG, "actualHeight: " + actualHeight);
        Log.d(TAG, "actualWidth: " + actualWidth);

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            //bmp = BitmapFactory.decodeFile(imagePath, options);
            bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            //printBitmapSize(bmp);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if (bmp != null) {
            bmp.recycle();
            bmp = null;
        }

        int rotate = 0;
        int orientation = 0;
        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d(TAG, "orientation: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotate = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                rotate = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotate = 270;
            }
            if (rotate != 0) {
                matrix.postRotate(rotate);
            }

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        Uri compressedUri = getCompressedUri(uri.getLastPathSegment());
        try {
            out = new FileOutputStream(compressedUri.getPath());

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        printBitmapSize(scaledBitmap);

        return new MediaFile(compressedUri, compressedUri.toString(), "image/jpeg", scaledBitmap.getHeight(), scaledBitmap.getWidth(), rotate, orientation, compressedUri.getLastPathSegment(), byteSizeOf(scaledBitmap));
    }

    private void printBitmapSize(Bitmap scaledBitmap) {
        long bytesize = byteSizeOf(scaledBitmap);
        int kb = (int) (bytesize / 1024);
        Log.d(TAG, "size bitmap : " + kb + " kb");
        Log.d(TAG, "getHeight bitmap: " + scaledBitmap.getHeight());
        Log.d(TAG, "getWidth bitmap: " + scaledBitmap.getWidth());
    }

    /**
     * returns the bytesize of the give bitmap
     */
    public static long byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private Uri getCompressedUri(String name) {
        Uri uri = null;
        try {
            uri = Uri.fromFile(File.createTempFile("IMG_" + name, ".jpg", cacheDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;

    }

}