package applica.aj;

import android.content.Context;

import junit.framework.Assert;

import java.net.URI;

import applica.aj.runtime.AJRuntime;
import applica.aj.runtime.javascriptcore.AJJavaScriptCoreRuntime;
import applica.aj.runtime.v8.AJV8Runtime;
import applica.aj.runtime.websocket.AJWebSocketRuntime;

/**
 * Created by bimbobruno on 21/04/16.
 */
public class AJApp {

    private static AJApp _current;

    public static AJApp current() {
        return _current;
    }

    public static AJRuntime runtime() {
        return current().getRuntime();
    }

    private Context context;
    private AJRuntime runtime;
    private boolean debug = false;
    private String socketUrl = "http://localhost:3000";

    public AJApp(Context context) {
        Assert.assertNull("AJApp already instantiated. It's possible to have only one m_instance of AJApp", _current);

        _current = this;

        this.context = context;
    }

    public void init() {
        if (debug) {
            runtime = new AJWebSocketRuntime(context, URI.create(socketUrl));
        } else {
            runtime = new AJV8Runtime(context);
        }
    }

    public void reload() {
        if (runtime != null) {
            runtime.destroy();
        }

        init();
    }

    public AJRuntime getRuntime() {
        return runtime;
    }

    //alias
    public AJRuntime rt() {
        return getRuntime();
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getSocketUrl() {
        return socketUrl;
    }

    public void setSocketUrl(String socketUrl) {
        this.socketUrl = socketUrl;
    }
}
