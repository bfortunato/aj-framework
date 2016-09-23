package applica.framework.android.utils;

/**
 * Created by bimbobruno on 02/02/16.
 */
public class Value {
    private String key;
    private Object value;

    public Value(String key, Object value) {
        this.key = key;
        this.value = value;
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
}
