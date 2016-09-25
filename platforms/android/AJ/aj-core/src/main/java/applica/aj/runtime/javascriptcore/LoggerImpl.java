package applica.aj.runtime.javascriptcore;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSObject;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class LoggerImpl extends JSObject implements Logger {

    LoggerImpl(JSContext jsContext) {
        super(jsContext, Logger.class);
    }

    @Override
    public void __i(String msg) {
        Log.i("AJ", msg);
    }

    @Override
    public void __e(String msg) {
        Log.e("AJ", msg);
    }

    @Override
    public void __w(String msg) {
        Log.w("AJ", msg);
    }

}
