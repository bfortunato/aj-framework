package applica.framework.android.utils.decoders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Created by bimbobruno on 02/02/16.
 */
public class InputStreamBitmapDecoder extends BitmapDecoder {

    private final InputStream inputStream;

    public InputStreamBitmapDecoder(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public Bitmap decode(Context context, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
