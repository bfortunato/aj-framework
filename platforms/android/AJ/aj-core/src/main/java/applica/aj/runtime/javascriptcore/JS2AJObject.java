package applica.aj.runtime.javascriptcore;

import org.liquidplayer.webkit.javascriptcore.JSArray;
import org.liquidplayer.webkit.javascriptcore.JSBaseArray;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.util.ArrayList;
import java.util.List;

import applica.aj.AJArray;
import applica.aj.AJObject;
import applica.aj.AJValue;

/**
 * Created by bimbobruno on 11/03/16.
 */
public class JS2AJObject {

    private static Object javaValue(JSValue v) {
        Object value;
        if (v.isArray()) {
            JSBaseArray a = v.toJSArray();
            AJArray l = new AJArray();
            for (int i = 0; i < a.size(); i++) {
                JSValue item = (JSValue) a.get(i);
                l.append(javaValue(item));
            }
            value = l;
        } else if (v.isObject()) {
            value = toAJ(v.toObject());
        } else if (v.isNull()) {
            value = null;
        } else if (v.isBoolean()) {
            value = v.toBoolean();
        } else if (v.isNumber()) {
            value = v.toNumber();
        } else if (v.isUndefined()) {
            value = null;
        } else {
            value = v.toString();
        }

        return value;
    }

    public static AJObject toAJ(JSObject js) {
        AJObject aj = new AJObject();
        for (String key : js.propertyNames()) {
            JSValue v = js.property(key);
            aj.set(key, javaValue(v));
        }

        return aj;
    }

    public static JSValue jsValue(JSContext jsContext, Object v) {
        JSValue value;
        if (v instanceof AJObject) {
            value = toJS(jsContext, (AJObject) v);
        } else if (v instanceof AJArray) {
            List l = ((AJArray) v).getList();
            Object[] items = new Object[l.size()];
            int index = 0;
            for (Object item : l) {
                items[index++] = jsValue(jsContext, item);
            }
            value = new JSArray(jsContext, items, JSValue.class);
        } else {
            value = new JSValue(jsContext, v);
        }

        return value;
    }

    public static JSObject toJS(JSContext jsContext, AJObject aj) {
        JSObject js = new JSObject(jsContext);
        for (AJValue v : aj.getValues()) {
            js.property(v.getKey(), jsValue(jsContext, v.getValue()));
        }
        return js;
    }
}
