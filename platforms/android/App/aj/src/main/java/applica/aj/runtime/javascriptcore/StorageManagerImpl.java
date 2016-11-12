package applica.aj.runtime.javascriptcore;

import android.content.Context;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import applica.aj.Async;
import applica.aj.runtime.Buffer;

/**
 * Created by bimbobruno on 14/03/16.
 */
public class StorageManagerImpl extends JSObject implements StorageManager {

    private final JSContext jsContext;
    private Context context;

    public StorageManagerImpl(Context context, JSContext jsContext) {
        super(jsContext, StorageManager.class);
        this.context = context;
        this.jsContext = jsContext;
    }

    @Override
    public void readText(final String path, final JSFunction cb) {
        Async.run("StorageManager.readText", new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(context.getFilesDir(), path);
                    if (file.exists()) {
                        FileInputStream in = new FileInputStream(file);
                        String content = IOUtils.toString(in);
                        IOUtils.closeQuietly(in);
                        callCb(cb, new JSValue(jsContext, false), new JSValue(jsContext, content));
                    } else {
                        throw new IOException("File not found: " + path);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    callCb(cb, new JSValue(jsContext, true), new JSValue(jsContext, "Cannot read text file: " + e.getMessage()));
                }
            }
        });
    }

    @Override
    public void read(final String path, final JSFunction cb) {
        Async.run("StorageManager.read", new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(context.getFilesDir(), path);
                    if (file.exists()) {
                        FileInputStream in = new FileInputStream(file);
                        byte[] content = IOUtils.toByteArray(in);
                        IOUtils.closeQuietly(in);
                        callCb(cb, new JSValue(jsContext, false), new JSValue(jsContext, Buffer.create(content)));
                    } else {
                        throw new IOException("File not found: " + path);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    callCb(cb, new JSValue(jsContext, true), new JSValue(jsContext, "Cannot read binary file: " + e.getMessage()));
                }
            }
        });
    }

    @Override
    public void write(final String path, final int buffer, final JSFunction cb) {
        Async.run("StorageManager.write", new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bytes = Buffer.get(buffer);
                    if (bytes == null) {
                        callCb(cb, new JSValue(jsContext, true), new JSValue(jsContext, "Buffer not found"));
                        return;
                    }
                    File file = new File(context.getFilesDir(), path);
                    FileOutputStream out = new FileOutputStream(file);
                    IOUtils.write(bytes, out);
                    IOUtils.closeQuietly(out);
                    callCb(cb, new JSValue(jsContext, false), new JSValue(jsContext, "OK"));
                } catch (IOException e) {
                    e.printStackTrace();
                    callCb(cb, new JSValue(jsContext, true), new JSValue(jsContext, "Cannot write binary file: " + e.getMessage()));
                }
            }
        });
    }

    @Override
    public void writeText(final String path, final String content, final JSFunction cb) {
        Async.run("StorageManager.writeText", new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(context.getFilesDir(), path);
                    FileOutputStream out = new FileOutputStream(file);
                    IOUtils.write(content, out);
                    IOUtils.closeQuietly(out);
                    callCb(cb, new JSValue(jsContext, false), new JSValue(jsContext, "OK"));
                } catch (IOException e) {
                    e.printStackTrace();
                    callCb(cb, new JSValue(jsContext, true), new JSValue(jsContext, "Cannot write text file: " + e.getMessage()));
                }
            }
        });
    }

    @Override
    public void delete(final String path, final JSFunction cb) {
        Async.run("StorageManager.delete", new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.forceDelete(new File(context.getFilesDir(), path));
                    callCb(cb, new JSValue(jsContext, false), new JSValue(jsContext, "OK"));
                } catch (IOException e) {
                    e.printStackTrace();
                    callCb(cb, new JSValue(jsContext, true), new JSValue(jsContext, "Cannot delete file: " + e.getMessage()));
                }
            }
        });
    }

    @Override
    public void exists(final String path, final JSFunction cb) {
        Async.run("StorageManager.exists", new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(context.getFilesDir(), path);
                    callCb(cb, new JSValue(jsContext, false), new JSValue(jsContext, file.exists()));
                } catch (Exception e) {
                    e.printStackTrace();
                    callCb(cb, new JSValue(jsContext, true), new JSValue(jsContext, "Cannot check file existence: " + e.getMessage()));
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
