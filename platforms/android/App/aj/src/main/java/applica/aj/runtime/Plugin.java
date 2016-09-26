package applica.aj.runtime;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import applica.aj.AJObject;

/**
 * Created by bimbobruno on 26/08/16.
 */
public abstract class Plugin {

    private String name;

    public Plugin(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public AJObject exec(String fn, AJObject data) {
        try {
            Method method = this.getClass().getDeclaredMethod(fn, AJObject.class);
            AJObject result = (AJObject) method.invoke(this, data);

            return result;
        } catch (NoSuchMethodException e) {
            Log.e("AJ", String.format("Plugin method not implemented: %s.%s", getName(), fn));

            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Log.e("AJ", String.format("Cannot invoke plugin method: %s.%s", getName(), fn));

            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            Log.e("AJ", String.format("Plugin method as illegal access. Must be public: %s.%s", getName(), fn));

            throw new RuntimeException(e);
        }
    }
}
