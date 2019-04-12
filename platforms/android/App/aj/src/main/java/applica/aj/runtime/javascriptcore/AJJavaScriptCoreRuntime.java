package applica.aj.runtime.javascriptcore;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSException;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.InputStream;

import applica.aj.AJObject;
import applica.aj.Async;
import applica.aj.Semaphore;
import applica.aj.runtime.AJRuntime;
import applica.aj.runtime.Plugin;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class AJJavaScriptCoreRuntime extends AJRuntime {

    public interface NativeGlobals {

        JSValue __requireInternal(String path);

        void async(JSValue action);

        void trigger(String type, JSValue data);

        void exec(String plugin, String fn, JSValue data, JSFunction callback);
    }

    public class NativeGlobalsImpl extends JSObject implements NativeGlobals {

        public NativeGlobalsImpl(JSContext jsContext) {
            super(jsContext, NativeGlobals.class);
        }

        @Override
        public JSValue __requireInternal(String path) {
            return require.__requireInternal(path);
        }

        @Override
        public void async(final JSValue action) {
            Async.run("js.async", new Runnable() {
                @Override
                public void run() {
                    action.toFunction().call(null, (Object[]) new JSValue[0]);
                }
            });
        }

        @Override
        public void trigger(final String type, final JSValue data) {
            AJObject argument = JS2AJObject.toAJ(data.toObject());
            AJJavaScriptCoreRuntime.this.trigger(type, argument);
        }

        @Override
        public void exec(final String plugin, final String fn, final JSValue data, final JSFunction callback) {
            AJObject argument = JS2AJObject.toAJ(data.toObject());
            AJJavaScriptCoreRuntime.this.exec(plugin, fn, argument, new Plugin.Callback() {
                @Override
                public void onSuccess(AJObject data) {
                    callback.call(null, false, data);
                }

                @Override
                public void onError(AJObject data) {
                    callback.call(null, true, data);
                }
            });
        }
    }

    private JSContext jsContext;
    private Require require;
    private JSObject jsRuntime;
    private JSFunction dispatchFunction;

    public AJJavaScriptCoreRuntime(final Context context) {
        super(context);

        jsContext = new JSContext();
        jsContext.setExceptionHandler(new JSContext.IJSExceptionHandler() {
            @Override
            public void handle(JSException e) {
                e.printStackTrace();
            }
        });

        require = new Require(context, jsContext);

        JSObject platform = new JSObject(jsContext);
        platform.property("engine", "native");
        platform.property("device", "Android");
        jsContext.property("platform", platform);

        jsContext.evaluateScript("var global = this;");

        NativeGlobals nativeGlobals = new NativeGlobalsImpl(jsContext);
        jsContext.property("native", nativeGlobals);
        jsContext.evaluateScript("global.__trigger = native.trigger;");
        jsContext.evaluateScript("global.async = native.async;");
        jsContext.evaluateScript("global.__exec = native.exec;");
        jsContext.evaluateScript("global.__requireInternal = native.__requireInternal;");

        Timers timers = new TimersImpl(jsContext);
        jsContext.property("native").toObject().property("timers", timers);
        jsContext.evaluateScript("global.setTimeout = native.timers.setTimeout;");
        jsContext.evaluateScript("global.setInterval = native.timers.setInterval;");
        jsContext.evaluateScript("global.clearTimeout = native.timers.clearTimeout;");
        jsContext.evaluateScript("global.clearInterval = native.timers.clearTimeout;");

        Logger logger = new LoggerImpl(jsContext);
        jsContext.property("logger", logger);
        jsContext.evaluateScript("logger.i = function() { logger.__i(Array.prototype.join.call(arguments, ' ')); }");
        jsContext.evaluateScript("logger.w = function() { logger.__w(Array.prototype.join.call(arguments, ' ')); }");
        jsContext.evaluateScript("logger.e = function() { logger.__e(Array.prototype.join.call(arguments, ' ')); }");

        jsContext.property("__httpClient", new HttpClientImpl(context, jsContext));
        jsContext.property("__assetsManager", new AssetsManagerImpl(context, jsContext));
        jsContext.property("__storageManager", new StorageManagerImpl(context, jsContext));
        jsContext.property("__buffersManager", new BuffersManagerImpl(context, jsContext));
        jsContext.property("device", new DeviceImpl(context, jsContext));

        jsContext.evaluateScript("var DEBUG = true;");
        jsContext.evaluateScript("var LOG_LEVEL_INFO = 3;");
        jsContext.evaluateScript("var LOG_LEVEL_WARNING = 2;");
        jsContext.evaluateScript("var LOG_LEVEL_ERROR = 1;");
        jsContext.evaluateScript("var LOG_LEVEL_DISABLED = 0;");
        jsContext.evaluateScript("var LOG_LEVEL = LOG_LEVEL_INFO;");

        try {
            InputStream inputStream = getContext().getAssets().open("js/app.js");
            String source = IOUtils.toString(inputStream);
            IOUtils.closeQuietly(inputStream);

            jsContext.evaluateScript(source);

            JSFunction main = jsContext.property("main").toFunction();
            jsRuntime = main.call().toObject();

            dispatchFunction = jsRuntime.property("run").toFunction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Semaphore run(final String action, final AJObject data) {
        Log.i("AJ", String.format("Dispatching action %s", action));

        return new Semaphore(new Runnable() {
            @Override
            public void run() {
                dispatchFunction.call(
                        jsRuntime,
                        new JSValue(jsContext, action),
                        JS2AJObject.toJS(jsContext, data)
                );

            }
        });
    }
}
