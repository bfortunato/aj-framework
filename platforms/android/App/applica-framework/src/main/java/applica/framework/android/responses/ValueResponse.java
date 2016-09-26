package applica.framework.android.responses;

public class ValueResponse extends SimpleResponse {
    private Object value;

    public ValueResponse() {
        setError(false);
    }

    public ValueResponse(Object value) {
        super();
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


}
