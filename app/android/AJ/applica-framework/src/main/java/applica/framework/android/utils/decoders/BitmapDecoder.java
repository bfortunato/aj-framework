package applica.framework.android.utils.decoders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.net.URL;

/**
 * Created by bimbobruno on 02/02/16.
 */
public abstract class BitmapDecoder {
    public abstract Bitmap decode(Context context, BitmapFactory.Options options);


    public static BitmapDecoder fromUri(Uri uri) {
        return new UriBitmapDecoder(uri);
    }
    
    public static BitmapDecoder fromURL(URL url) {
        return new URLBitmapDecoder(url);
    }
}
