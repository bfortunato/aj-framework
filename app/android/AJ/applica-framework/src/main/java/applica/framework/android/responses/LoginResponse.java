package applica.framework.android.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import applica.framework.android.security.User;

/**
 * Created by bimbobruno on 28/01/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse extends SimpleResponse {

    private String token;
    private User user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
