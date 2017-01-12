package applica.aj.runtime.v8;

import android.content.Context;

import applica.aj.AJObject;
import applica.aj.Semaphore;
import applica.aj.runtime.AJRuntime;

/**
 * Created by bimbobruno on 1/4/17.
 */

public class AJV8Runtime extends AJRuntime {

    static {
        System.loadLibrary("aj");
    }

    public AJV8Runtime(Context context) {
        super(context);

        init();
        mapFunction("doIt", new Function() {
            @Override
            public void run() {
                executeScript("print(name);");
            }
        });

        executeScript("var name = 'bruno fortunato'; doIt();");
        //destroy();
    }

    private void executeScript2(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                executeScript(s);
            }
        }).start();
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
    }

    @Override
    public Semaphore run(String action, AJObject data) {
        return null;
    }

    public native void init();
    public native void mapFunction(String name, Function fn);
    public native void executeScript(String source);
    public native void destroy();
}
