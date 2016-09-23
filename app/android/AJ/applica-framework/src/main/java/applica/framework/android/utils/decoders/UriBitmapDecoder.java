package applica.framework.android.utils.decoders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;

/**
 * Created by bimbobruno on 02/02/16.
 */
public class UriBitmapDecoder extends BitmapDecoder {

    private final Uri uri;

    public UriBitmapDecoder(Uri uri) {
        this.uri = uri;
    }

    @Override
    public Bitmap decode(Context context, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Uri getUri() {
        return uri;
    }
}
