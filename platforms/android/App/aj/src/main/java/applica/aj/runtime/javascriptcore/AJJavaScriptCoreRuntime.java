package applica.aj.runtime.javascriptcore;

import android.content.Context;
import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSException;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import applica.aj.AJObject;
import applica.aj.Async;
import applica.aj.Semaphore;
import applica.aj.runtime.AJRuntime;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class AJJavaScriptCoreRuntime extends AJRuntime {

    public interface NativeGlobals {

        JSValue __requireInternal(String path);

        void async(JSValue action);

        void trigger(String type, JSValue data);

        JSValue exec(String plugin, String fn, JSValue data);
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
            Async.run(new Runnable() {
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
        public JSValue exec(final String plugin, final String fn, final JSValue data) {
            AJObject argument = JS2AJObject.toAJ(data.toObject());
            AJObject ret = AJJavaScriptCoreRuntime.this.exec(plugin, fn, argument);
            return JS2AJObject.toJS(jsContext, ret);
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

        //create runtime instance
        StringBuilder init = new StringBuilder()
                .append("var aj = require('./aj');\n")
                .append("function __ajinit() {\n")
                .append("   logger.i('Creating runtime...');\n")
                .append("   return aj.createRuntime();\n")
                .append("}\n");

        jsContext.evaluateScript(init.toString());
        jsRuntime = jsContext.property("__ajinit").toFunction().call(null).toObject();

        //create runtime instance
        StringBuilder main = new StringBuilder()
                .append("var main = require('./main');\n")
                .append("(function __ajmain() {\n")
                .append("   main.main();\n")
                .append("})();\n");

        jsContext.evaluateScript(main.toString());

        /*JSFunction createRuntimeFunction = require.require("./aj").toObject().property("createRuntime").toFunction();
        jsRuntime = createRuntimeFunction.call(null).toObject();

        //run main script
        JSValue main = require.require("./main").toObject().property("main");
        main.toFunction().call(null);*/

        dispatchFunction = jsRuntime.property("run").toFunction();
    }

    @Override
    public Semaphore run(final String action, final AJObject data) {
        Log.i("AJ", String.format("Dispatching action %s", action));

        return new Semaphore(new Runnable() {
            @Override
            public void run() {
                dispatchFunction.call(
                        jsRuntime,
                        new JSValue[] {
                                new JSValue(jsContext, action),
                                JS2AJObject.toJS(jsContext, data)
                        }
                );

            }
        });
    }
}
