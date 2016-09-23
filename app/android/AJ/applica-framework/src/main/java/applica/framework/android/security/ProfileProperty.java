package applica.framework.android.security;

/**
 * Created by bimbobruno on 28/01/16.
 */
public class ProfileProperty {

    private String key;
    private String value;

    public ProfileProperty() {}

    public ProfileProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
