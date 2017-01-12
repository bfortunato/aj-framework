package applica.aj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import applica.aj.runtime.Buffer;
import applica.framework.android.utils.Nulls;

public class AJValue {

    public static final AJValue EMPTY = new AJValue("_", null);

    private String key;
    private Object value;

    public AJValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String asString() {
        return (String) this.value;
    }

    public Integer asInt() {
        return AJNumberConverter.toInt(value);
    }

    public Long asLong() {
        return AJNumberConverter.toLong(value);
    }

    public Boolean asBoolean() {
        return (Boolean) this.value;
    }

    public Float asFloat() {
        return AJNumberConverter.toFloat(value);
    }

    public Double asDouble() {
        return AJNumberConverter.toDouble(value);
    }

    public AJArray asArray() {
        return (AJArray) this.value;
    }

    public AJObject asObject() {
        return (AJObject) this.value;
    }

    public byte[] asBuffer() {
        if (asInt() != null) {
            return Buffer.get(asInt());
        }

        return null;
    }

    public Bitmap asBitmap() {
        byte[] buffer = asBuffer();
        if (buffer != null) {
            return toBitmap(buffer);
        }

        return null;
    }

    public Integer asColor() {
        if (asObject() != null) {
            return toColor(asObject());
        }

        return null;
    }

    public static Integer toColor(AJObject ajObject) {
        Integer r = ajObject.get("r").asInt();
        Integer g = ajObject.get("g").asInt();
        Integer b = ajObject.get("b").asInt();

        if (Nulls.areNotNull(r, g, b)) {
            Integer a = Nulls.orElse(ajObject.get("a").asInt(), 255);

            return Color.argb(a, r, g, b);
        }

        return null;
    }

    public static Bitmap toBitmap(byte[] buffer) {
        return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
    }

    @Override
    public boolean equals(Object obj) {
        AJValue other = ((AJValue) obj);

        if (other == null) {
            return false;
        }

        if (!this.key.equals(other.key)) {
            return false;
        }

        if (this.isObject()) {
            if (!other.isObject()) {
                return false;
            }

            if (this.asObject() == null && other.asObject() == null) {
                return true;
            }

            if (this.asObject() == null && other.asObject() != null) {
                return false;
            }

            if (this.asObject() != null && other.asObject() == null) {
                return false;
            }

            return this.asObject().equals(other.asObject());
        } else if (this.isArray()) {
            if (!other.isArray()) {
                return false;
            }

            if (this.asArray() == null && other.asArray() == null) {
                return true;
            }

            if (this.asArray() == null && other.asArray() != null) {
                return false;
            }

            if (this.asArray() != null && other.asArray() == null) {
                return false;
            }

            return this.asArray().equals(other.asArray());
        } else {
            if (this.value == null && other.value == null) {
                return true;
            }

            if (this.value != null && other.value == null) {
                return false;
            }

            if (this.value == null && other.value != null) {
                return false;
            }

            return this.value.equals(other.value);
        }
    }

    public boolean isObject() {
        return value instanceof AJObject;
    }

    public boolean isArray() {
        return value instanceof AJArray;
    }
}
