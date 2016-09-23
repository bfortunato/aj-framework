package applica.framework.android.utils.decoders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by bimbobruno on 02/02/16.
 */
public class URLBitmapDecoder extends BitmapDecoder {

    private final URL url;

    public URLBitmapDecoder(URL url) {
        this.url = url;
    }

    @Override
    public Bitmap decode(Context context, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        try {
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(in, null, options);
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public URL getUrl() {
        return url;
    }

}
