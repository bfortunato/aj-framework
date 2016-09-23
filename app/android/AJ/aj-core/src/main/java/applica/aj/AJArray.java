package applica.aj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import applica.aj.runtime.Buffer;
import applica.framework.android.utils.Nulls;

/**
 * Created by bimbobruno on 02/09/16.
 */
public class AJArray {

    private List<Object> internal = new ArrayList<>();

    public Object at(int index) {
        return internal.get(index);
    }

    public int count() {
        return internal.size();
    }

    public void append(Object el) {
        internal.add(el);
    }

    public void removeAtIndex(int index) {
        internal.remove(index);
    }

    public String stringAt(int index) {
        return (String) at(index);
    }

    public Integer intAt(int index) {
        return AJNumberConverter.toInt(at(index));
    }

    public Long longAt(int index) {
        return AJNumberConverter.toLong(at(index));
    }

    public Boolean booleanAt(int index) {
        return (Boolean) at(index);
    }

    public Float floatAt(int index) {
        return AJNumberConverter.toFloat(at(index));
    }

    public Double doubleAt(int index) {
        return AJNumberConverter.toDouble(at(index));
    }

    public AJArray arrayAt(int index) {
        return (AJArray) at(index);
    }

    public AJObject objectAt(int index) {
        AJObject v = (AJObject) at(index);

        if (v == null) {
            return AJObject.empty();
        }

        return v;
    }

    public byte[] bufferAt(int index) {
        if (intAt(index) != null) {
            return Buffer.get(intAt(index));
        } else {
            return null;
        }
    }

    public Bitmap bitmapAt(int index) {
        byte[] buffer = bufferAt(index);
        if (buffer != null) {
            AJValue.toBitmap(buffer);
        }

        return null;
    }

    public Integer colorAt(int index) {
        AJObject obj = objectAt(index);
        if (obj != null) {
            return AJValue.toColor(obj);
        }

        return null;
    }

    public List getList() {
        return internal;
    }
}
