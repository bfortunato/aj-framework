package applica.framework.android.security;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.AccessToken;

import org.springframework.util.StringUtils;

import applica.framework.android.responses.LoginResponse;
import applica.framework.android.services.ServiceLocator;
import applica.framework.android.social.FacebookLogin;
import applica.framework.android.social.FacebookLoginActivity;
import applica.framework.android.social.FacebookLoginHost;
import applica.framework.android.utils.Listener;

/**
 * Created by bimbobruno on 27/01/16.
 */
public class Session {

    public static final String LOGIN_TYPE_MAILPASSWORD = "mail_password";
    public static final String LOGIN_TYPE_FACEBOOK = "facebook";

    public static final String PREFERENCES_KEY = "session";
    public static final String PREFERENCE_PASSWORD = "password";
    public static final String PREFERENCE_MAIL = "mail";
    public static final String PREFERENCE_LOGINTYPE = "mail";
    public static final String PREFERENCE_READPERMISSIONS = "readPermsissions";

    private static Session s_session;
    private FacebookLogin facebookLogin;

    public static Session instance() {
        if (s_session == null) {
            s_session = new Session();
        }

        return s_session;
    }

    private SessionService sessionService;
    private User loggedUser;
    private String authToken;

    private Session() {
        sessionService = ServiceLocator.instance().getService(SessionService.class);
    }

    public boolean isAuthenticated() {
        return loggedUser != null;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void start(final Activity context, final String mail, final String password, final Listener<LoginResponse> startListener) {
        abandon(context);

        sessionService.login(context, mail, password, new Listener<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse value) {
                SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PREFERENCE_MAIL, mail);
                editor.putString(PREFERENCE_PASSWORD, password);
                editor.putString(PREFERENCE_LOGINTYPE, LOGIN_TYPE_MAILPASSWORD);
                editor.apply();

                loggedUser = value.getUser();
                authToken = value.getToken();

                if (startListener != null) {
                    startListener.onSuccess(value);
                }
            }

            @Override
            public void onError(String error) {
                abandon(context);

                if (startListener != null) {
                    startListener.onError(error);
                }
            }
        });
    }

    public void resume(Activity context, Listener<LoginResponse> resumeListener) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        String lastLoginType = preferences.getString(PREFERENCE_LOGINTYPE, null);
        if (StringUtils.hasText(lastLoginType)) {
            if (LOGIN_TYPE_MAILPASSWORD.equals(lastLoginType)) {
                String mail = preferences.getString(PREFERENCE_MAIL, null);
                String password = preferences.getString(PREFERENCE_MAIL, null);

                if (StringUtils.hasLength(mail) && StringUtils.hasLength(password)) {
                    start(context, mail, password, resumeListener);
                }
            } else if (LOGIN_TYPE_FACEBOOK.equals(lastLoginType)) {
                String readPermissions = preferences.getString(PREFERENCE_READPERMISSIONS, "");
                facebookStart((FacebookLoginActivity) context, readPermissions, resumeListener);
            }
        } else {
            resumeListener.onError("never logged in");
        }
    }

    public void facebookStart(final Activity context, final String readPermissions, final Listener<LoginResponse> startListener) {
        ((FacebookLoginHost) context).getFacebookLogin().login(readPermissions, new Listener<AccessToken>() {
            @Override
            public void onSuccess(AccessToken response) {
                sessionService.facebookLogin(context, response.getToken(), new Listener<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse value) {
                        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(PREFERENCE_LOGINTYPE, LOGIN_TYPE_FACEBOOK);
                        editor.putString(PREFERENCE_READPERMISSIONS, readPermissions);
                        editor.apply();

                        loggedUser = value.getUser();
                        authToken = value.getToken();

                        if (startListener != null) {
                            startListener.onSuccess(value);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        abandon(context);

                        if (startListener != null) {
                            startListener.onError(error);
                        }
                    }
                });


            }

            @Override
            public void onError(String error) {
                abandon(context);

                if (startListener != null) {
                    startListener.onError(error);
                }
            }
        });
    }

    public void abandon(Activity context) {
        loggedUser = null;
        authToken = null;

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREFERENCE_MAIL);
        editor.remove(PREFERENCE_PASSWORD);
        editor.remove(PREFERENCE_LOGINTYPE);
        editor.remove(PREFERENCE_READPERMISSIONS);
        editor.apply();

        FacebookLogin.logout();
    }

    public String getAuthToken() {
        return authToken;
    }
}
