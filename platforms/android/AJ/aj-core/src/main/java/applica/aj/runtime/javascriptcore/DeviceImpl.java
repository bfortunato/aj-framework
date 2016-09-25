package applica.aj.runtime.javascriptcore;

import android.content.Context;
import android.util.DisplayMetrics;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSObject;

/**
 * Created by bimbobruno on 22/04/16.
 */
public class DeviceImpl extends JSObject implements Device {

    private final DisplayMetrics displayMetrics;

    public DeviceImpl(Context context, JSContext jsContext) {
        super(jsContext, Device.class);
        displayMetrics = context.getResources().getDisplayMetrics();
    }

    @Override
    public String getName() {
        return "Android";
    }

    @Override
    public int getHeight() {
        return displayMetrics.heightPixels;
    }

    @Override
    public int getWidth() {
        return displayMetrics.widthPixels;
    }

    @Override
    public float getScale() {
        return displayMetrics.density;
    }
}
