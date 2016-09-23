package applica.aj.runtime.javascriptcore;

import android.content.Context;
import android.util.Base64;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import applica.aj.runtime.Buffer;

/**
 * Created by bimbobruno on 02/09/16.
 */
public class BuffersManagerImpl extends JSObject implements BuffersManager {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public BuffersManagerImpl(Context context, JSContext jsContext) {
        super(jsContext, BuffersManager.class);
    }

    @Override
    public void create(final String base64, final JSFunction cb) {
        EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                    int id = Buffer.create(bytes);
                    cb.call(null, false, id);
                } catch (Exception ex) {
                    cb.call(null, true, ex.getMessage());
                }
            }
        });
    }

    @Override
    public void read(final int id, final JSFunction cb) {
        EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bytes = Buffer.get(id);
                    if (bytes != null) {
                        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                        cb.call(null, false, base64);
                    } else {
                        cb.call(null, false, "Buffer not found");
                    }

                } catch (Exception ex) {
                    cb.call(null, true, ex.getMessage());
                }
            }
        });
    }

    @Override
    public void destroy(final int id, final JSFunction cb) {
        EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Buffer.destroy(id);
                } catch (Exception ex) {
                    cb.call(null, true, ex.getMessage());
                }
            }
        });
    }
}
