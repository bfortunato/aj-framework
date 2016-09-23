package applica.framework.android.responses;

import org.springframework.http.HttpStatus;

/**
 * Created by iaco on 25/02/15.
 */
public class RestResponse extends SimpleResponse {
    private HttpStatus status;
    private Object value;


    public RestResponse(HttpStatus code) {
        super();
        status = code;
        if (status.equals(HttpStatus.OK)) {
            setError(false);
        } else {
            setError(true);
        }

    }

    public RestResponse(HttpStatus code, String msg) {
        this(code);
        setMessage(msg);
    }

    public RestResponse(HttpStatus code, Object data) {
        this(code);
        value = data;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
