package applica.framework.android.security;

import android.content.Context;

import applica.framework.android.responses.LoginResponse;
import applica.framework.android.utils.Listener;

/**
 * Created by bimbobruno on 28/01/16.
 */
public interface SessionService {

    void login(Context context, String mail, String password, Listener<LoginResponse> loginListener);
    void facebookLogin(Context context, String fbToken, Listener<LoginResponse> loginListener);
    void refreshToken(Context context, String oldToken, Listener<LoginResponse> loginListener);

}
