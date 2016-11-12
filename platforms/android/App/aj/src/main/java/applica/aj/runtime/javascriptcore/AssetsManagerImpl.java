package applica.aj.runtime.javascriptcore;

import android.content.Context;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.IOException;
import java.io.InputStream;

import applica.aj.Async;
import applica.aj.runtime.Buffer;

/**
 * Created by bimbobruno on 14/03/16.
 */
public class AssetsManagerImpl extends JSObject implements AssetsManager {

    private final JSContext jsContext;
    private Context context;

    public AssetsManagerImpl(Context context, JSContext jsContext) {
        super(jsContext, AssetsManager.class);
        this.context = context;
        this.jsContext = jsContext;
    }

    @Override
    public void load(final String path, final JSFunction cb) {
        Async.run("AssetsManager.load", new Runnable() {
            @Override
            public void run() {
                try {
                    String appDir = "hybrid/";
                    String finalPath = FilenameUtils.normalize(FilenameUtils.concat(appDir, path));
                    InputStream in = context.getAssets().open(finalPath);
                    byte[] bytes = IOUtils.toByteArray(in);
                    IOUtils.closeQuietly(in);
                    callCb(cb, new JSValue(jsContext, false), new JSValue(jsContext, Buffer.create(bytes)));
                } catch (IOException e) {
                    e.printStackTrace();
                    callCb(cb, new JSValue(jsContext, true), new JSValue(jsContext, "error.load.data"));
                }
            }
        });
    }

    @Override
    public void exists(final String path, final JSFunction cb) {
        Async.run("AssetsManager.exists", new Runnable() {
            @Override
            public void run() {
                try {
                    String appDir = "hybrid/";
                    String finalPath = FilenameUtils.normalize(FilenameUtils.concat(appDir, path));
                    InputStream in = context.getAssets().open(finalPath);
                    callCb(cb, new JSValue(jsContext, false), new JSValue(jsContext, true));
                    IOUtils.closeQuietly(in);
                } catch (IOException e) {
                    e.printStackTrace();
                    callCb(cb, new JSValue(jsContext, false), new JSValue(jsContext, false));
                }
            }
        });
    }

    private void callCb(final JSFunction cb, final JSValue error, final JSValue value) {
        Async.run("StorageManager.callCb", new Runnable() {
            @Override
            public void run() {
                cb.call(null, error, value);
            }
        });
    }
}
