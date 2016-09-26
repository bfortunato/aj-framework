package applica.framework.android.utils;

/**
 * Created by bimbobruno on 02/02/16.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import org.springframework.util.StringUtils;

import applica.framework.android.utils.decoders.BitmapDecoder;

/**
 * Created by applica1 on 26/10/15.
 */

public class BitmapUtils {

    public static int maxTextureSize = 0;
    public static Size maxBitmapSize = new Size(1024, 1024);


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    public static Bitmap loadBitmapResampledAndRotated(Context context, Uri uri) {
        Bitmap rotatedBitmap = null;

        try {
            Bitmap bitmap = loadDecodedBitmap(context, BitmapDecoder.fromUri(uri));

            String realPath = getRealPathFromURI(context, uri);
            int rotation = -1;
            if (StringUtils.hasLength(realPath)) {
                rotation = getRotationFromExif(context, realPath);
            }

            if (rotation == -1) {
                rotation = getRotationFromUri(context, uri);
            }

            switch (rotation) {
                case 90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    //bitmap.recycle();
                    break;
                case 180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    //bitmap.recycle();
                    break;
                case 270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    //bitmap.recycle();
                    break;
                default:
                    rotatedBitmap = bitmap;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rotatedBitmap;
    }

    public static int getRotationFromUri(Context context, Uri imageUri) {
        String[] orientationColumn = { MediaStore.Images.Media.ORIENTATION };
        Cursor cur = context.getContentResolver().query(imageUri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }

        return orientation;
    }

    public static int getRotationFromExif(Context context, String bitmapPath) {
        try {
            ExifInterface ei = new ExifInterface(bitmapPath);
            int exifOrientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                case ExifInterface.ORIENTATION_NORMAL:
                    return 0;
            }
        } catch (Exception e) {

        }

        return -1;
    }

    public static Bitmap loadDecodedBitmap(Context context, BitmapDecoder decoder) {
        return loadDecodedBitmap(context, decoder, maxBitmapSize);
    }

    public static Bitmap loadDecodedBitmap(Context context, BitmapDecoder decoder, Size size) {
        if (size == null) {
            size = maxBitmapSize;
        }

        Bitmap bitmap;
        try {
            android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            bitmap = decoder.decode(context, options);

            int sample = 1;
            //Scale image to screen resolution
            if (options.outHeight > maxBitmapSize.getHeight() || options.outWidth > maxBitmapSize.getWidth())
            {
                int heightRatio = Math.round((float) options.outHeight / (float) maxBitmapSize.getHeight());
                int widthRatio = Math.round((float) options.outWidth / (float) maxBitmapSize.getWidth());
                sample = Math.max(heightRatio, widthRatio);
            }

            //Scale image to stay within memory limitations
            while (BitmapUtils.calcBitmapSize(options.outWidth, options.outHeight, sample) > BitmapUtils.calcAvailableMemory())
            {
                sample *= 2;
            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = sample;

            bitmap = decoder.decode(context, options);

            if(BitmapUtils.maxTextureSize > 0) {
                if (Math.max(options.outWidth, options.outHeight) > BitmapUtils.maxTextureSize) {
                    float factor = Math.max(options.outWidth, options.outHeight) / BitmapUtils.maxTextureSize;
                    float width = options.outWidth / factor;
                    float height = options.outHeight / factor;
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                }
            }

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return image size
     * @param width
     * @param height
     * @param sample
     * @return image size in kb
     */
    private static long calcBitmapSize(int width, int height, int sample)
    {
        sample = sample == 0 ? 100 : sample;
        long value = ((width / sample) * (height / sample) * 4) / 1024;
        return value;

    }

    /**Returns max memory for app
     * @return current max memory size for app
     */
    private static long calcAvailableMemory()
    {
        long value = Runtime.getRuntime().maxMemory();
        //String type = "";
        if (Build.VERSION.SDK_INT >= 11)
        {
            value = (value / 1024) - (Runtime.getRuntime().totalMemory() / 1024);
            //type = "JAVA";
        }
        else
        {
            value = (value / 1024) - (Debug.getNativeHeapAllocatedSize() / 1024);
            //type = "NATIVE";
        }

        return value;
    }


    /**
     * Handles pre V19 uri's
     * @param context
     * @param contentUri
     * @return
     */
    private static String getPathForPreV19(Context context, Uri contentUri) {
        String res = null;

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }

        return res;
    }

    /**
     * Handles V19 and up uri's
     * @param context
     * @param contentUri
     * @return path
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getPathForV19AndUp(Context context, Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

    private static String getRealPathFromURI(Context context,
                                             Uri contentUri) {
        String uriString = String.valueOf(contentUri);
        boolean goForKitKat= uriString.contains("com.android.providers");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && goForKitKat) {
            return getPathForV19AndUp(context, contentUri);
        } else {

            return getPathForPreV19(context, contentUri);
        }
    }

}
