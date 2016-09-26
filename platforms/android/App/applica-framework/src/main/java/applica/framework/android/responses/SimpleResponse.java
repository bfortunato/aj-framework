package applica.framework.android.responses;

/**
 * Responses are used in the framework as ajax calls results
 */
public class SimpleResponse {
    private boolean error;
    private String message;

    public SimpleResponse() {
    }

    public SimpleResponse(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
