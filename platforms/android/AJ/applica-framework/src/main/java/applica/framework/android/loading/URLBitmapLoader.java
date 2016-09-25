package applica.framework.android.loading;

import android.content.Context;
import android.graphics.Bitmap;

import java.net.URL;

import applica.framework.android.utils.BitmapUtils;
import applica.framework.android.utils.Listener;
import applica.framework.android.utils.Size;
import applica.framework.android.utils.decoders.BitmapDecoder;

/**
 * Created by bimbobruno on 02/02/16.
 */
public class URLBitmapLoader {

    private final URL url;
    private Listener<Bitmap> listener;
    private Size size;
    private Bitmap bitmap;

    public URLBitmapLoader(URL url, Listener<Bitmap> listener) {
        this.url = url;
        this.listener = listener;
    }

    public void load(final Context context) {
        if (bitmap != null) {
            listener.onSuccess(bitmap);
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = BitmapUtils.loadDecodedBitmap(context, BitmapDecoder.fromURL(url), size);
                    if (bitmap != null) {
                        listener.onSuccess(bitmap);
                    } else {
                        listener.onError("bitmap not loaded");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }
            }
        });
        thread.start();

    }

    public URL getUrl() {
        return url;
    }

    public Listener<Bitmap> getListener() {
        return listener;
    }

    public void setListener(Listener<Bitmap> listener) {
        this.listener = listener;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
